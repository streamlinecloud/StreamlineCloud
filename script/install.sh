#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

###############################################################################
# CONFIGURATION & GLOBAL STATE
###############################################################################

# --- Installer configuration ---
API_URL="https://api.github.com/repos/streamlinecloud/StreamlineCloud/releases?page=0&per_page=1"
DEFAULT_USER="streamlinecloud"
DEFAULT_INSTALL_DIR="/opt/streamlinecloud"

# --- Runtime state (filled during execution) ---
PKG_MANAGER=""
RELEASE_TAG=""
DOWNLOAD_URL=""
STREAM_USER=""
INSTALL_DIR=""

###############################################################################
# LOGGING HELPERS
###############################################################################

log_info()  { printf "[INFO] %s\n" "$*"; }
log_warn()  { printf "[WARN] %s\n" "$*"; } >&2
log_error() { printf "[ERROR] %s\n" "$*"; } >&2

###############################################################################
# BASIC CHECKS (ROOT, PACKAGE MANAGER)
###############################################################################

require_root() {
  if [[ "${EUID:-$(id -u)}" -ne 0 ]]; then
    log_error "This script must be run as root (sudo)."
    exit 1
  fi
}

detect_pkg_manager() {
  if command -v apt-get >/dev/null 2>&1; then
    PKG_MANAGER="apt"
  elif command -v apt >/dev/null 2>&1; then
    PKG_MANAGER="apt"
  elif command -v pacman >/dev/null 2>&1; then
    PKG_MANAGER="pacman"
  else
    PKG_MANAGER="unknown"
  fi
}

install_packages() {
  local pkgs=("$@")
  if [[ ${#pkgs[@]} -eq 0 ]]; then
    return 0
  fi

  case "$PKG_MANAGER" in
    apt)
      log_info "Installing packages with apt: ${pkgs[*]}"
      DEBIAN_FRONTEND=noninteractive apt-get update -y
      DEBIAN_FRONTEND=noninteractive apt-get install -y "${pkgs[@]}"
      ;;
    pacman)
      log_info "Installing packages with pacman: ${pkgs[*]}"
      pacman -Sy --noconfirm --needed "${pkgs[@]}"
      ;;
    *)
      log_warn "No supported package manager detected. Please install manually: ${pkgs[*]}"
      return 1
      ;;
  esac
}

###############################################################################
# SET COLORS
###############################################################################

setup_colors() {
  export NEWT_COLORS='
root=lightgray,black
window=lightgray,black
border=red,black
shadow=black,black
title=white,black

textbox=white,black
acttextbox=white,red

entry=black,lightgray

label=lightgray,black
button=white,red

listbox=lightgray,black
actlistbox=white,red
'
}

###############################################################################
# WHIPTAIL SUPPORT (DIALOGS & PROGRESS BARS)
###############################################################################

ensure_whiptail() {
  if command -v whiptail >/dev/null 2>&1; then
    return 0
  fi

  case "$PKG_MANAGER" in
    apt)
      install_packages whiptail || true
      ;;
    pacman)
      # whiptail binary is provided by libnewt on Arch
      install_packages libnewt || true
      ;;
  esac

  if ! command -v whiptail >/dev/null 2>&1; then
    log_warn "whiptail could not be installed. Falling back to simple CLI prompts."
  fi
}

has_whiptail() {
  command -v whiptail >/dev/null 2>&1
}

ask_yes_no() {
  local text="$1"
  local default_no="${2:-true}"  # default_no=true => default = No for CLI fallback

  if has_whiptail; then
    if whiptail --yesno "$text" 10 70 --title "StreamlineCloud Installer"; then
      return 0
    else
      return 1
    fi
  else
    local prompt="[y/N]"
    if [[ "$default_no" == "false" ]]; then
      prompt="[Y/n]"
    fi
    read -r -p "$text $prompt " ans
    ans=${ans:-}
    if [[ "$default_no" == "false" ]]; then
      # default: yes
      [[ -z "$ans" || "$ans" =~ ^[Yy]$ ]]
    else
      # default: no
      [[ "$ans" =~ ^[Yy]$ ]]
    fi
  fi
}

ask_input() {
  local prompt="$1"
  local default="$2"
  local __resultvar="$3"
  local value

  if has_whiptail; then
    if ! value=$(whiptail --inputbox "$prompt" 10 70 "$default" 3>&1 1>&2 2>&3); then
      log_error "Aborted by user."
      exit 1
    fi
  else
    printf "%s [%s]: " "$prompt" "$default"
    read -r value
    value=${value:-$default}
  fi

  printf -v "$__resultvar" '%s' "$value"
}

show_message() {
  local text="$1"
  if has_whiptail; then
    whiptail --msgbox "$text" 10 70 --title "StreamlineCloud Installer"
  else
    log_info "$text"
  fi
}

# Run a long-running command with a whiptail gauge (fake but useful progress)
run_with_optional_gauge() {
  local title="$1"; shift
  local message="$1"; shift

  if has_whiptail; then
    {
      echo 5
      "$@" &
      local cmd_pid=$!
      local progress=10

      # simple "spinner" style progress until command is done
      while kill -0 "$cmd_pid" 2>/dev/null; do
        echo "$progress"
        progress=$((progress + 8))
        if [[ $progress -gt 95 ]]; then
          progress=95
        fi
        sleep 1
      done

      wait "$cmd_pid"
      local status=$?
      echo 100
      sleep 0.2
      exit "$status"
    } | whiptail --gauge "$message" 10 70 0 --title "$title"
  else
    "$@"
  fi
}

###############################################################################
# STAGE 1: DEPENDENCIES (jq, curl, wget, unzip, screen) & JAVA 21+
###############################################################################

check_or_install_non_java_dependencies() {
  # Note: java is handled separately
  local deps=(jq curl wget unzip screen)
  local missing=()

  for dep in "${deps[@]}"; do
    if ! command -v "$dep" >/dev/null 2>&1; then
      missing+=("$dep")
    fi
  done

  if [[ ${#missing[@]} -eq 0 ]]; then
    log_info "All non-Java dependencies are already installed."
    return 0
  fi

  log_info "Missing dependencies: ${missing[*]}"

  run_with_optional_gauge \
    "Dependencies" \
    "Installing required dependencies: ${missing[*]} ..." \
    install_packages "${missing[@]}" || true

  local still_missing=()
  for dep in "${deps[@]}"; do
    if ! command -v "$dep" >/dev/null 2>&1; then
      still_missing+=("$dep")
    fi
  done

  if [[ ${#still_missing[@]} -gt 0 ]]; then
    log_error "The following packages are still missing and must be installed manually: ${still_missing[*]}"
    exit 1
  fi
}

get_java_major_version() {
  if ! command -v java >/dev/null 2>&1; then
    echo 0
    return 0
  fi

  local ver
  ver=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | head -n1)

  if [[ -z "$ver" ]]; then
    echo 0
    return 0
  fi

  # Examples: "1.8.0_352" or "21.0.1"
  if [[ "$ver" == 1.* ]]; then
    echo "$ver" | awk -F '.' '{print $2}'
  else
    echo "$ver" | awk -F '.' '{print $1}'
  fi
}

ensure_java21() {
  local major
  major=$(get_java_major_version)

  if [[ "$major" -ge 21 ]]; then
    log_info "Detected Java version is sufficient (>=21, current: $major)."
    return 0
  fi

  log_warn "Java 21 or higher is not installed (current major: $major)."
  if ask_yes_no "Java 21+ is required. Attempt automatic installation?" true; then
    case "$PKG_MANAGER" in
      apt)
        if ! run_with_optional_gauge \
          "Java" \
          "Installing OpenJDK 21 (headless)..." \
          install_packages openjdk-21-jre-headless; then
          log_warn "Failed to install openjdk-21-jre-headless. Trying openjdk-21-jdk..."
          run_with_optional_gauge \
            "Java" \
            "Installing OpenJDK 21 (JDK)..." \
            install_packages openjdk-21-jdk || true
        fi
        ;;
      pacman)
        run_with_optional_gauge \
          "Java" \
          "Installing jre-openjdk (latest Java)..." \
          install_packages jre-openjdk || true
        ;;
      *)
        log_warn "Automatic Java installation is not supported on this system."
        ;;
    esac

    major=$(get_java_major_version)
    if [[ "$major" -ge 21 ]]; then
      log_info "Java successfully installed (major version: $major)."
      return 0
    fi

    log_error "Java 21+ could not be installed automatically. Please install it manually and re-run the installer."
    exit 1
  else
    log_error "Java 21+ is required. Please install a suitable Java version and re-run this script."
    exit 1
  fi
}

###############################################################################
# STAGE 2: FETCH LATEST RELEASE INFO FROM GITHUB
###############################################################################

fetch_release_info() {
  log_info "Fetching latest StreamlineCloud release info from GitHub..."
  local json

  if ! json=$(curl -fsSL "$API_URL"); then
    log_error "Failed to reach GitHub API: $API_URL"
    exit 1
  fi

  local tag url
  tag=$(jq -r '.[0].tag_name // empty' <<<"$json")
  url=$(jq -r '.[0].assets[] | select(.name | endswith(".zip")) | .browser_download_url' <<<"$json" | head -n1)

  if [[ -z "$tag" || -z "$url" || "$tag" == "null" || "$url" == "null" ]]; then
    log_error "Failed to extract release info (tag or ZIP asset) from GitHub API response."
    exit 1
  fi

  RELEASE_TAG="$tag"
  DOWNLOAD_URL="$url"

  log_info "Detected release tag: $RELEASE_TAG"
  log_info "Download URL: $DOWNLOAD_URL"

  show_message "Latest StreamlineCloud release detected:\n\nTag: $RELEASE_TAG\n\nDownload URL:\n$DOWNLOAD_URL"
}

###############################################################################
# STAGE 3: CREATE/USE SERVICE USER
###############################################################################

setup_stream_user() {
  STREAM_USER="$DEFAULT_USER"

  if ! ask_yes_no "Create/use a dedicated system user for StreamlineCloud?" false; then
    log_info "No dedicated system user will be used. Files will belong to root/current user."
    STREAM_USER=""
    return 0
  fi

  ask_input "System username for StreamlineCloud" "$DEFAULT_USER" STREAM_USER

  if id -u "$STREAM_USER" >/dev/null 2>&1; then
    log_info "User '$STREAM_USER' already exists. Using existing user."
  else
    log_info "Creating system user '$STREAM_USER'..."
    useradd -r -m -s /bin/bash "$STREAM_USER"
    log_info "User '$STREAM_USER' created."
  fi

  if ask_yes_no "Set a password for '$STREAM_USER'?" true; then
    passwd "$STREAM_USER"
  fi
}

###############################################################################
# STAGE 4: CHOOSE INSTALL DIRECTORY
###############################################################################

choose_install_directory() {
  INSTALL_DIR="$DEFAULT_INSTALL_DIR"
  ask_input "Installation directory" "$DEFAULT_INSTALL_DIR" INSTALL_DIR

  mkdir -p "$INSTALL_DIR"

  if [[ -n "$(ls -A "$INSTALL_DIR" 2>/dev/null || true)" ]]; then
    if ! ask_yes_no "The directory '$INSTALL_DIR' is not empty.\n\nProceed and overwrite existing files?" true; then
      log_error "Aborted by user."
      exit 1
    fi
  fi

  log_info "Installation directory set to: $INSTALL_DIR"
}

###############################################################################
# STAGE 5: DOWNLOAD & INSTALL (ZIP -> /tmp -> copy streamlinecloud_image/*)
###############################################################################

download_file() {
  local url="$1"
  local dest="$2"

  if command -v curl >/dev/null 2>&1; then
    curl -fL "$url" -o "$dest"
  else
    wget -O "$dest" "$url"
  fi
}

download_and_install() {
  local tmpfile tmpdir image_dir
  tmpfile=$(mktemp /tmp/streamlinecloud_XXXXXX.zip)
  tmpdir=$(mktemp -d /tmp/streamlinecloud_XXXXXX)

  log_info "Downloading release archive..."
  run_with_optional_gauge \
    "Download" \
    "Downloading StreamlineCloud image..." \
    download_file "$DOWNLOAD_URL" "$tmpfile"

  log_info "Extracting ZIP to temporary directory: $tmpdir"
  unzip -oq "$tmpfile" -d "$tmpdir"

  image_dir="$tmpdir/streamlinecloud_image"
  if [[ ! -d "$image_dir" ]]; then
    log_error "Expected folder 'streamlinecloud_image' not found inside the archive."
    rm -f "$tmpfile"
    rm -rf "$tmpdir"
    exit 1
  fi

  log_info "Copying contents of streamlinecloud_image into $INSTALL_DIR ..."
  mkdir -p "$INSTALL_DIR"
  cp -R "$image_dir"/. "$INSTALL_DIR"/

  rm -f "$tmpfile"
  rm -rf "$tmpdir"

  if [[ -n "${STREAM_USER:-}" ]]; then
    chown -R "$STREAM_USER":"$STREAM_USER" "$INSTALL_DIR"
  fi

  show_message "StreamlineCloud installation completed.\n\nVersion: $RELEASE_TAG\nInstall path: $INSTALL_DIR"
  log_info "Installation completed successfully."
}

###############################################################################
# STAGE 6: MAIN ENTRY POINT
###############################################################################

main() {
  # Stage 0: basic setup
  require_root
  detect_pkg_manager
  log_info "Detected package manager: $PKG_MANAGER"
  setup_colors
  ensure_whiptail

  show_message "Welcome to the StreamlineCloud installer.\n\nThis script will:\n- Ensure required dependencies are present\n- Ensure Java 21+ is installed\n- Download the latest StreamlineCloud image\n- Optionally create a dedicated system user\n- Install files into a chosen directory"

  # Stage 1: dependencies
  check_or_install_non_java_dependencies
  ensure_java21

  # Stage 2: latest release info from GitHub
  fetch_release_info

  # Stage 3: user setup
  setup_stream_user

  # Stage 4: installation directory
  choose_install_directory

  # Stage 5: download & install
  download_and_install

  log_info "StreamlineCloud $RELEASE_TAG has been installed to '$INSTALL_DIR'."
}

main "$@"
