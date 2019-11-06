import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import services.Downloader;
import services.Logging;
import services.Way;

import java.util.concurrent.Executors;

public class MainOkHttpAsync {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(Logging.getLogger(System.nanoTime()))
                .dispatcher(new Dispatcher(Executors.newFixedThreadPool(50)))
                .build();
        System.out.println("ASYNC");
        Downloader.downloadPictures(client, 10, Way.ASYNC);
    }
}