# HttpClientWrapper Library

A simple and efficient wrapper around **OkHttp3** to handle network requests in Android applications. It provides asynchronous and synchronous methods for fetching text and uploading files, with automatic main-thread callback delivery.

## Features
- **Async & Sync GET**: Fetch text data from any URL with minimal boilerplate.
- **File Upload (POST)**: Easily upload files using Multipart requests.
- **Thread Management**: Built-in `ExecutorService` to handle network operations off the UI thread.
- **Main-Thread Callbacks**: Success and Error results are automatically delivered back to the UI thread using a `Handler`.
- **Resource Cleanup**: Simple `shutdown()` method to release executor resources.

---

## Method Documentation

### Initialization
```java
HttpClientWrapper httpClient = new HttpClientWrapper();
```

### 1. Fetch Text (Asynchronous)
Performs a GET request and delivers the result via a callback on the main thread.
```java
httpClient.getText("https://api.example.com/data", new HttpClientWrapper.Callback() {
    @Override
    public void onSuccess(String text) {
        // Handle success result on UI thread
    }

    @Override
    public void onError(String errorMessage) {
        // Handle error result on UI thread
    }
});
```

### 2. Fetch Text (Synchronous)
Performs a GET request and returns the result string. **Warning**: Do not call this on the Main Thread.
```java
try {
    String data = httpClient.getTextSync("https://api.example.com/data");
} catch (IOException e) {
    e.printStackTrace();
}
```

### 3. Upload File (Asynchronous)
Uploads a file to a specified URL using a Multipart POST request.
```java
httpClient.uploadFile("https://api.example.com/upload", file, "image/png", new HttpClientWrapper.Callback() {
    @Override
    public void onSuccess(String text) {
        // Handle upload success
    }

    @Override
    public void onError(String errorMessage) {
        // Handle upload error
    }
});
```

### 4. Cleanup
Always shutdown the executor when the activity or application is destroyed to prevent memory leaks.
```java
httpClient.shutdown();
```

---

## Usage Example

### activity_main.xml
Define buttons to trigger network requests and a TextView to display the result.
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    tools:context=".MainActivity">

    <LinearLayout
        android:id="@+id/main"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center_horizontal">

        <TextView
            android:id="@+id/textView_status"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Network status will appear here"
            android:padding="8dp"
            android:background="#F0F0F0"
            android:textSize="14sp"
            android:fontFamily="monospace"
            android:layout_marginBottom="16dp" />

        <Button
            android:id="@+id/btn_get_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Fetch Text (Async GET)" />

        <Button
            android:id="@+id/btn_upload_file"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Upload Sample File (Async POST)" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
Demonstrate initialization, async GET, and file upload.
```java
package com.rohit.makingpermissionlibrary;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rohit.baselibrary.network.HttpClientWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private HttpClientWrapper httpClient;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.textView_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        httpClient = new HttpClientWrapper();

        setupNetworkSamples();
    }

    private void setupNetworkSamples() {
        // 1. Asynchronous GET Request
        findViewById(R.id.btn_get_text).setOnClickListener(v -> {
            tvStatus.setText("Fetching data...");
            String url ="https://raw.githubusercontent.com/ramupandey72832/Android_BaseLibrary/refs/heads/main/baselibrary/mykey.txt";
            httpClient.getText(url, new HttpClientWrapper.Callback() {
                @Override
                public void onSuccess(String text) {
                    tvStatus.setText("GET Success:\n" + text);
                }

                @Override
                public void onError(String errorMessage) {
                    tvStatus.setText("GET Error: " + errorMessage);
                }
            });
        });

        // 2. Asynchronous POST (File Upload)
        findViewById(R.id.btn_upload_file).setOnClickListener(v -> {
            File dummyFile = createDummyFile();
            if (dummyFile != null) {
                tvStatus.setText("Uploading file...");
                httpClient.uploadFile("https://httpbin.org/post", dummyFile, "text/plain", new HttpClientWrapper.Callback() {
                    @Override
                    public void onSuccess(String text) {
                        tvStatus.setText("Upload Success:\n" + text);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        tvStatus.setText("Upload Error: " + errorMessage);
                    }
                });
            }
        });
    }

    private File createDummyFile() {
        try {
            File file = new File(getFilesDir(), "test_upload.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("This is a test file for HttpClientWrapper upload demonstration.");
            writer.close();
            return file;
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create dummy file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (httpClient != null) {
            httpClient.shutdown();
        }
    }
}
```
