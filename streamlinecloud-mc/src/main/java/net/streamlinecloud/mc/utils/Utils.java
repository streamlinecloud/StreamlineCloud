package net.streamlinecloud.mc.utils;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.VersionPacket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Utils {

    public static HashMap<String, Double> servers;

    public static void postStart(VersionPacket versionPacket) {
        //e
        try {
            // URL to send the POST request to
            String url = "http://localhost:5378/streamline/post/proxy/version";

            // Data to be sent in the request body
//            String postData = "version=" + versionPacket.getVersion() + "&apiversion=" + versionPacket.getApiVersion() + "&buildate= " + versionPacket.getBuildDate();
            String postData = new Gson().toJson(versionPacket, VersionPacket.class);
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

}