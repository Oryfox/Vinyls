//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LastFM {
    static String apiKey;
    static HttpURLConnection connection;

    public static void init(String apiKey) {
        LastFM.apiKey = apiKey;
    }

    private static String getImageUrl(String url) {
        StringBuilder response = new StringBuilder();

        try {
            connection = (HttpURLConnection)(new URL(url)).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int status = connection.getResponseCode();
            BufferedReader reader = status > 299 ? new BufferedReader(new InputStreamReader(connection.getErrorStream())) : new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            JSONObject root = new JSONObject(response.toString());
            return root.getJSONObject("results").getJSONObject("albummatches").getJSONArray("album").getJSONObject(0).getJSONArray("image").getJSONObject(3).getString("#text");
        } catch (IOException var10) {
            var10.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return "";
    }

    public static BufferedImage getCoverImage(String artist, String albumName) {
        if (apiKey == null) throw new RuntimeException("LastFM not initialized");
        String imageUrl = getImageUrl("https://ws.audioscrobbler.com/2.0/?method=album.search&album=" + artist.replaceAll(" ", "%20") + "%20" + albumName.replaceAll(" ", "%20") + "&format=json&api_key=" + apiKey);

        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (IOException var4) {
            var4.printStackTrace();
            throw new RuntimeException(var4.getMessage());
        }
    }

    public static ArrayList<String> getSongsForAlbum(String artist, String album) {
        String url = "https://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=" + apiKey + "&artist=" + artist.replaceAll(" ", "%20") + "&album=" + album.replaceAll(" ", "%20") + "&format=json";

        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();
        String line;
        BufferedReader reader;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("LastFM Response: " + response.toString());
            ArrayList<String> list = new ArrayList<>();
            JSONObject root = new JSONObject(response.toString());

            JSONArray tracks = root.getJSONObject("album").getJSONObject("tracks").getJSONArray("track");
            for (int i = 0; i < tracks.length(); i++) {
                list.add(tracks.getJSONObject(i).getString("name"));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
