package services;

import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

public class Logging {
    public static HttpLoggingInterceptor getLogger(long start) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NotNull String message) {
                System.out.println((System.nanoTime() - start) / 1000000 + " ms. "
                        + "Id:"
                        + Thread.currentThread().getId()
                        + " " + Thread.currentThread().getName()
                        + " " + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return logging;
    }
}
