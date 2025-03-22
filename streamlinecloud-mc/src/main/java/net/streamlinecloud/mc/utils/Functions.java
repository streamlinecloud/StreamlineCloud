package net.streamlinecloud.mc.utils;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.StaticServerDataPacket;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Functions {

    public static String post(Object o, String path) {
        String res = "";
        try {
            String url = "http://localhost:5378/streamline/" + path;

            String postData = new Gson().toJson(o, o.getClass());
            URL apiUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setRequestProperty("auth_key", StaticCache.accessKey);

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();


            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                res = response.toString();
            }

            connection.disconnect();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
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

    public static void startup() {
        try {
            StaticCache.plFolder = new File(System.getProperty("user.dir"));
            System.out.println(StaticCache.plFolder);
            File key = new File(StaticCache.plFolder.getAbsolutePath() + "/.apikey.json");

            String keyString = FileUtils.readFileToString(key, Charset.defaultCharset());

            StaticCache.accessKey = keyString.split(",_,")[0];
            StaticCache.serverData = new Gson().fromJson(keyString.split(",_,")[1], StaticServerDataPacket.class);

            FileUtils.forceDelete(key);

            System.out.println("DEBUG PORT: " + StaticCache.serverData.getPort());

            post(StaticCache.serverData.getUuid(), "post/server/hello-world");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}