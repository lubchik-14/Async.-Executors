import okhttp3.Dispatcher;
import services.Way;
import okhttp3.OkHttpClient;
import services.Downloader;
import services.Logging;

import java.util.concurrent.Executors;

public class MainOkHttpSync {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(Logging.getLogger(System.nanoTime()))
                .dispatcher(new Dispatcher(Executors.newFixedThreadPool(50)))
                .build();
        System.out.println("SYNC");
        Downloader.downloadPictures(client, 10, Way.SYNC);
    }
}
