package net.streamlinecloud.mc.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * BackendRequest is a utility class for making HTTP requests to the backend.
 */
@RequiredArgsConstructor
public class BackendRequest {

    @NonNull
    String method;
    String url = "http://localhost:5378/streamline/";

    RestType restType = RestType.GET;
    ApplicationType applicationType;
    HashMap<String, String> headers = new HashMap<>();
    String body;

    @Getter
    String response;
    @Getter
    int statusCode;

    boolean auth = true;

    /**
     * Fetches data from the backend server using the specified method and URL.
     * This method is a shorthand for calling {@link #fetch(Consumer)} with an empty consumer.
     * This method is useful for simple requests where no further processing is needed, the response is not needed or to store the response data and use it in another method or class.
     */
    public BackendRequest fetch() {
        fetch(backendRequest -> {});
        return this;
    }

    /**
     * Sets the request body.
     * @param body the request body
     * @return this instance.
     */
    public BackendRequest withBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * Sets the request headers.
     * @param headers the request headers
     * @return this instance.
     */
    public BackendRequest withHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    /**
     * Sets the request method.
     * @param type the request method
     * @return this instance.
     * @see RestType RestType for available request methods
     */
    public BackendRequest setType(@NonNull RestType type) {
        this.restType = type;
        return this;
    }

    /**
     * Sets whether to use authentication.<br>
     * If set to true, the request will include an authorization header with a token.<br>
     * If set to false, the request will not include an authorization header.<br>
     * This should be set to false for public endpoints.<br>
     * <strong>For most use cases it's not required to change auth to false</strong>
     * @param auth true to use authentication, false to not use authentication - default is true
     * @return this instance.
     */
    public BackendRequest setAuth(boolean auth) {
        this.auth = auth;
        return this;
    };

    /**
     * Sets the request URL.
     * @param url the request URL
     * @return this instance.
     */
    public BackendRequest setUrl(@NonNull String url) {
        this.url = url;
        return this;
    };

    /**
     * Sets the request method.
     * @param applicationType the request application type
     * @return this instance.
     * @see ApplicationType ApplicationType for available request application types
     */
    public BackendRequest setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
        return this;
    }

    /**
     * Fetches data from the backend server using the specified method and URL.
     * @param consumer a consumer that accepts the BackendRequest instance as an argument
     */
    public void fetch(Consumer<BackendRequest> consumer) {

        try {
            String url = this.url + method;

            URL apiUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            connection.setRequestMethod(restType.toString());

            headers.forEach(connection::setRequestProperty);

            if (applicationType != null) {
                connection.setRequestProperty("Content-Type", applicationType.getTypeFormat());
            }

            connection.setRequestProperty("auth_key", StaticCache.accessKey);

            connection.setDoOutput(true);

            if (body != null && (restType == RestType.POST || restType == RestType.PUT)) {

                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.write(body.getBytes(StandardCharsets.UTF_8));
                }

            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder res = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                connection.disconnect();
                response = res.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        consumer.accept(this);
    }

    public enum RestType {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        OPTIONS,
        TRACE,
        PATCH
    }

    @AllArgsConstructor
    @Getter
    public enum ApplicationType {
        APPLICATION_JSON("application/json"),
        APPLICATION_XML("application/xml"),
        APPLICATION_OCTET_STREAM("application/octet-stream"),
        APPLICATION_PDF("application/pdf"),
        APPLICATION_ZIP("application/zip"),
        APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
        TEXT_PLAIN("text/plain"),
        TEXT_HTML("text/html"),
        TEXT_CSS("text/css"),
        TEXT_JAVASCRIPT("text/javascript"),
        IMAGE_JPEG("image/jpeg"),
        IMAGE_PNG("image/png"),
        IMAGE_GIF("image/gif"),
        IMAGE_SVG("image/svg+xml"),
        IMAGE_WEBP("image/webp"),
        AUDIO_MPEG("audio/mpeg"),
        AUDIO_OGG("audio/ogg"),
        AUDIO_WAV("audio/wav"),
        AUDIO_FLAC("audio/flac"),
        VIDEO_MP4("video/mp4"),
        VIDEO_WEBM("video/webm"),
        VIDEO_OGG("video/ogg"),
        MULTIPART_FORM_DATA("multipart/form-data"),
        MULTIPART_BYTERANGES("multipart/byteranges"),
        APPLICATION_JAVASCRIPT("application/javascript"),
        APPLICATION_LD_JSON("application/ld+json"),
        APPLICATION_SQL("application/sql"),
        APPLICATION_API_JSON("application/vnd.api+json");
        ;
        String typeFormat;
    }

}