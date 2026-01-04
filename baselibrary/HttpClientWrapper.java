package com.rohit.baselibrary.network;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientWrapper {

    private static final String TAG = "HttpClientWrapper";
    private final OkHttpClient client;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public HttpClientWrapper() {
        client = new OkHttpClient();
        executor = Executors.newFixedThreadPool(4); // configurable pool size
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Asynchronous method to fetch text from a URL.
     * Result is delivered via Callback on the main/UI thread.
     */
    public void getText(String url, Callback callback) {
        executor.submit(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body().string();
                    postSuccess(callback, result);
                } else {
                    Log.e(TAG, "Request failed: " + response);
                    postError(callback, "Request failed: " + response.code());
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching text", e);
                postError(callback, "Error fetching text: " + e.getMessage());
            }
        });
    }

    /**
     * Synchronous method to fetch text from a URL.
     * Should not be called on the main thread.
     */
    public String getTextSync(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("Request failed: " + response.code());
            }
        }
    }

    /**
     * Asynchronous POST request to upload a file. * @param url The endpoint to upload to * @param file The file to upload * @param mediaType The MIME type of the file (e.g. "text/plain", "image/png")
     */
    public void uploadFile(String url, File file, String mediaType, Callback callback) {
        executor.submit(() -> {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse(mediaType));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), fileBody)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    postSuccess(callback, response.body().string());
                } else {
                    postError(callback, "POST failed: " + response.code());
                }
            } catch (IOException e) {
                Log.e(TAG, "POST error", e);
                postError(callback, "POST error: " + e.getMessage());
            }
        });
    }

    /**
     * Shutdown the executor when no longer needed.
     */
    public void shutdown() {
        executor.shutdown();
    }

    // Helper methods to post results back to main thread
    private void postSuccess(Callback callback, String result) {
        mainHandler.post(() -> callback.onSuccess(result));
    }

    private void postError(Callback callback, String errorMessage) {
        mainHandler.post(() -> callback.onError(errorMessage));
    }

    // Generic callback interface
    public interface Callback {
        void onSuccess(String text);

        void onError(String errorMessage);
    }
}
