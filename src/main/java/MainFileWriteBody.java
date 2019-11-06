import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import services.Downloader;

import java.io.IOException;

public class MainFileWriteBody {
    public static void main(String[] args) {
        writeBodyToFile(new OkHttpClient(), "https://github.com", "");
    }

    public static void writeBodyToFile(OkHttpClient client, String url, String path) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            try {
                Downloader.downloadData(response.body().byteStream(), path + "body.txt", "");
            } catch (IOException e) {
                System.out.println("Failed to write a file  " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Failed to get a response " + e.getMessage());
        }
        Downloader.shutdownAndAwaitTermination(client.dispatcher().executorService());
    }
}
