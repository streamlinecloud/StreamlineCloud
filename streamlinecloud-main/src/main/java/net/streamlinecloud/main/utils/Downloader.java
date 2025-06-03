package net.streamlinecloud.main.utils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class Downloader {

    Continue next = null;

    public void download(URL url, File dstFile) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy()) // adds HTTP REDIRECT support to GET and POST methods
                .build();
        try {
            HttpGet get = new HttpGet(url.toURI()); // we're using GET but it could be via POST as well
            File downloaded = httpclient.execute(get, new FileDownloadResponseHandler(dstFile, next));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(httpclient);
        }
    }

    public void download(URL url, File file, Continue next) {
        this.next = next;
        download(url, file);
    }

    static class FileDownloadResponseHandler implements ResponseHandler<File> {

        private final File target;
        Continue next = null;

        public FileDownloadResponseHandler(File target, Continue next) {
            this.next = next;
            this.target = target;
        }

        public void startProgressbar(HttpResponse httpResponse) {
            ProgressBar progressBar = new ProgressBar("Downloading file... ",httpResponse.getEntity().getContentLength());

            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

            scheduledExecutorService.scheduleAtFixedRate(() -> {

                if (target.length() >= httpResponse.getEntity().getContentLength()) {
                    scheduledExecutorService.shutdownNow();

                    if (next != null) {
                        next.execute("s");
                    }
                }

                progressBar.stepTo(target.length());

            }, 0, 500, TimeUnit.MILLISECONDS);

        }

        @Override
        public File handleResponse(HttpResponse response) throws IOException {
            startProgressbar(response);
            InputStream source = response.getEntity().getContent();
            FileUtils.copyInputStreamToFile(source, this.target);
            return this.target;
        }

    }

    public interface Continue {
        void execute(String s);
    }

}