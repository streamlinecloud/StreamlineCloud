
> [!WARNING]
> This project is in Beta state.  
> Please report bugs you may find

# StreamlineCloud

StreamlineCloud is a free and opensource cloud system for paper and velocity based minecraft networks.

## Tags

[![Check, Build and Release](https://github.com/streamlinecloud/StreamlineCloud/actions/workflows/docker-image.yml/badge.svg)](https://github.com/streamlinecloud/StreamlineCloud/actions/workflows/docker-image.yml)

## Features

- **Easy to use:** Manage multiple Minecraft servers across different nodes from a single dashboard.
- **Extensions:** Easily extend functionality with plugins to tailor the system to your needs.
- **Scalable:** Scale your Minecraft server infrastructure seamlessly as your player base grows.
- **Opensource:** Intuitive web-based interface for managing and monitoring your Minecraft servers. (Comming Soon)

## Installation

1. **Prerequisites:**
   - You need to have Java 21 installed.
   - We recommend at least 4GB of ram and a modern CPU.
   - A Linux based system (like Debian 12). At the moment Windows Systems are not supported due to the file system managment.

2. **Create a folder:**
   ```bash
   adduser streamlinecloud
   su - streamlinecloud
   cd
   ```

3. **Download StreamlineCloud:**
   Go to the releases section of this repository and copy the url of the latest release.
   You can find the latest release [here](https://github.com/streamlinecloud/StreamlineCloud/releases).
   ```bash
   wget <url>
   ```

4. **Start StreamlineCloud:**
    ```bash
   bash ./start.sh
   ```

## Setup

Getting started is simple. After launching Streamline Cloud, you'll be guided through a quick setup process. You can choose your preferred language and decide whether to use the default server configuration (recommended for new users) or customize everything yourself. Just follow the prompts, Streamline Cloud will walk you through each step.

## More Information
For details on the API, advanced build and start arguments, and more complex setup scenarios, please visit our [wiki](https://streamlinecloud.net/wiki). You'll find everything you need to customize and extend Streamline Cloud to fit your needs.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your improvements.

## Contributors

Thanks to all the amazing contributors!

[![Contributors](https://contrib.rocks/image?repo=streamlinecloud/streamlinecloud)](https://github.com/streamlinecloud/streamlinecloud/graphs/contributors)

