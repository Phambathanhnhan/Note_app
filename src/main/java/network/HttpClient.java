package network;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HttpClient {
    public static String sendpost(String targetUrl, String urlParameters) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            byte[] out = urlParameters.getBytes(StandardCharsets.UTF_8);
            try (OutputStream stream = conn.getOutputStream()) {
                stream.write(out);
            }
            Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name());
            return s.hasNext() ? s.useDelimiter("\\A").next() : "";
        } catch (Exception e) { return "error"; }
    }

    public static String sendget(String targetUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
            conn.setRequestMethod("GET");
            Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name());
            return s.hasNext() ? s.useDelimiter("\\A").next() : "";
        } catch (Exception e) { return "error"; }
    }
}