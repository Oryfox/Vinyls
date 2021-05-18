import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class YouTube {
    public static String apiKey;

    public YouTube() {
    }

    public static void init(String apiKey) {
        YouTube.apiKey = apiKey;
    }

    public static JSONObject search(String term) {
        if (apiKey == null) {
            throw new RuntimeException("No api key was set. Use method 'init(YOUR_API_KEY)'");
        } else {
            StringBuilder builder = new StringBuilder();

            try {
                HttpURLConnection connection = (HttpURLConnection)(new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + term.replaceAll(" ", "%20") + "&key=" + apiKey)).openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                int status = connection.getResponseCode();
                BufferedReader reader;
                if (status >= 299) {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }

                String line;
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();
                connection.disconnect();
            } catch (IOException var6) {
                var6.printStackTrace();
            }

            System.out.println(builder);
            return new JSONObject(builder.toString());
        }
    }

    public static String searchID(String term) {
        try {
            return search(term).getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
        } catch (JSONException var2) {
            throw new RuntimeException("No video found for search term: " + term);
        }
    }

    public static void watchByKeyword(String term) throws IOException {
        try {
            Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=" + searchID(term)));
        } catch (URISyntaxException var2) {
            var2.printStackTrace();
        }

    }

    public static void watchById(String videoID) throws IOException {
        try {
            Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=" + videoID));
        } catch (URISyntaxException var2) {
            var2.printStackTrace();
        }

    }
}