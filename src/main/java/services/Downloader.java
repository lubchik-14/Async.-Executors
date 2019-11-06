package services;

import response.BingImage;
import response.BingResponse;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Downloader {
    public static void downloadPictures(OkHttpClient client, int count, Way way) {
        String url = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=9&n=" + count + "&mkt=en-US";
        Request archiveRequest = new Request.Builder()
                .url(url)
                .build();
        switch (way) {
            case SYNC: {
                downloadPicturesSync(archiveRequest, client);
                break;
            }
            case ASYNC: {
                downloadPicturesAsync(archiveRequest, client);
                break;
            }
        }
    }

    private static void downloadPicturesSync(Request archiveRequest, OkHttpClient client) {
        try (Response response = client.newCall(archiveRequest).execute()) {
            readBingResponse(response.body(), client, Way.SYNC);
        } catch (IOException e) {
            System.out.println("Failed to get a response " + e.getMessage());
        }
    }

    private static void downloadPicturesAsync(Request archiveRequest, OkHttpClient client) {
        client.newCall(archiveRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Failed with " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try (ResponseBody BingResponseBody = response.body()) {
                    readBingResponse(BingResponseBody, client, Way.ASYNC);
                }
            }
        });
    }

    private static void readBingResponse(ResponseBody responseBody, OkHttpClient client, Way way) throws IOException {
        Gson gson = new Gson();
        BingResponse bingResp = gson.fromJson(responseBody.string(), BingResponse.class);
        for (BingImage bingImage : bingResp.getBingImages()) {
            Request fileUrlRequest = new Request.Builder()
                    .url("https://bing.com" + bingImage.getUrl())
                    .build();
            switch (way) {
                case ASYNC: {
                    downloadFileAsync(fileUrlRequest, client, bingImage);
                    break;
                }
                case SYNC: {
                    downloadFileSync(fileUrlRequest, client, bingImage);
                    break;
                }
            }
        }
    }

    private static void downloadFileSync(Request fileUrlRequest, OkHttpClient client, BingImage bingImage) {
        try (Response response = client.newCall(fileUrlRequest).execute()) {
            try {
                Downloader.downloadData(response.body().byteStream(), bingImage.getHsh() + "(sync).jpg", "");
            } catch (IOException e) {
                System.out.println("Failed to write a file  " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Failed to get a response " + e.getMessage());
        }
    }

    private static void downloadFileAsync(Request fileUrlRequest, OkHttpClient client, BingImage bingImage) {
        client.newCall(fileUrlRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                System.out.println("Failed with " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    try {
                        downloadData(responseBody.byteStream(), bingImage.getHsh() + ".jpg", "");
                    } catch (IOException e) {
                        System.out.println("Failed to write a file  " + e.getMessage());
                    }
                }
            }
        });
    }

    public static void downloadData(InputStream inputStream, String fileName, String path) throws IOException {
        try (OutputStream out = new FileOutputStream(path + fileName);
             BufferedInputStream in = new BufferedInputStream(inputStream)) {
            int readCount;
            byte[] buffer = new byte[4096];
            while ((readCount = in.read(buffer)) != -1) {
                out.write(buffer, 0, readCount);
            }
        }
    }

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
