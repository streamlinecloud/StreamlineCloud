package io.streamlinemc.mc.utils;

import com.google.gson.Gson;
import io.streamlinemc.api.packet.StaticServerDataPacket;
import lombok.SneakyThrows;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Functions {

    public static void post(Object o, String path) {
        try {
            // URL to send the POST request to
            String url = "http://localhost:5378/streamline/" + path;

            // Data to be sent in the request body
//            String postData = "version=" + versionPacket.getVersion() + "&apiversion=" + versionPacket.getApiVersion() + "&buildate= " + versionPacket.getBuildDate();
            String postData = new Gson().toJson(o, o.getClass());
            // Create a URL object
            URL apiUrl = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // Set up the request
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set a custom header
            connection.setRequestProperty("auth_key", StaticCache.accessKey);

            // Write data to the request body
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            /*
            if (responseCode == 500) {
                WaterfallSCP.getInstance().getProxy().stop();
            }
             */

            // Read the response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Print the response
                System.out.println("Response: " + response.toString());
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String path) {
        try {
            URL apiUrl = new URL("http://localhost:5378/streamline/" + path);

            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // Set up the request
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            // Set a custom header
            connection.setRequestProperty("auth_key", StaticCache.accessKey);

            int responseCode = connection.getResponseCode();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                connection.disconnect();
                return response.toString();

            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SneakyThrows
    public static void startup() {
        StaticCache.plFolder = new File(System.getProperty("user.dir"));
        System.out.println(StaticCache.plFolder);
        File key = new File(StaticCache.plFolder.getAbsolutePath() + "/.apikey.json");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(key));
        String line = bufferedReader.readLine();

        StaticCache.accessKey = line.split(",_,")[0];
        StaticCache.serverData = new Gson().fromJson(line.split(",_,")[1], StaticServerDataPacket.class);

        bufferedReader.close();
        key.delete();

        post(StaticCache.serverData.getUuid(), "post/server/hello-world");
    }

}