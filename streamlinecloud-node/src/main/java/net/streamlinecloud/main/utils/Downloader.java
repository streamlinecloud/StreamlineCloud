package net.streamlinecloud.main.utils;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

public class Downloader {

    public void download(URL url, File dstFile) throws IOException, InterruptedException {
        download(url, dstFile, null);
    }

    public void download(URL url, File dstFile, Continue next) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .GET()
                .build();

        System.out.println("Downloading: " + url);

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        long contentLength = response.headers()
                .firstValueAsLong("Content-Length")
                .orElse(-1);

        ProgressBarBuilder pbBuilder = new ProgressBarBuilder()
                .setTaskName("Downloading")
                .setUnit("MiB", 1048576)
                .setStyle(ProgressBarStyle.builder()
                        .leftBracket("[")
                        .rightBracket("]")
                        .block('=')
                        .rightSideFractionSymbol('>')
                        .build())
                .clearDisplayOnFinish()
                .setUpdateIntervalMillis(1000);

        if (contentLength > 0) {
            pbBuilder.setInitialMax(contentLength);
        }

        try (InputStream in = response.body();
             OutputStream out = Files.newOutputStream(dstFile.toPath(),
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE);
             ProgressBar pb = pbBuilder.build()) {

            byte[] buffer = new byte[8192];
            int read;
            long totalRead = 0;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                totalRead += read;
                if (contentLength > 0) pb.stepBy(read);
            }

            pb.stepTo(totalRead);
        }

        System.out.println("Download complete: " + dstFile.getAbsolutePath());

        if (next != null) {
            next.execute(dstFile.getAbsolutePath());
        }
    }

    @FunctionalInterface
    public interface Continue {
        void execute(String filePath);
    }
}
