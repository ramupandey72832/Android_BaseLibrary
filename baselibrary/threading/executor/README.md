# ExecutorServiceWrapper Library

A robust and efficient threading utility for Android applications that wraps Java's `ExecutorService` API. It provides a structured way to manage different types of thread pools for various tasks (IO, Network, Sequential) and simplifies posting results back to the Android Main Thread.

## Features

- **Optimized Thread Pools**: Pre-configured executors for specific task types (IO-bound, Network-bound, and Sequential).
- **CPU-Aware**: The IO thread pool is automatically sized based on the number of available processors.
- **Main Thread Integration**: Built-in support for switching context back to the UI thread using Android's `Looper`.
- **Simplified API**: Static methods provide easy access to threading without managing complex lifecycle or executor objects manually.
- **Resource Management**: Includes a centralized shutdown method to clean up background resources.

---

## Method Documentation

### Static Helper Methods

- `ExecutorServiceWrapper.runOnIO(Runnable runnable)`: Best for disk operations or CPU-intensive tasks. Uses a fixed thread pool.
- `ExecutorServiceWrapper.runOnNetwork(Runnable runnable)`: Best for network requests. Uses a cached thread pool that scales with demand.
- `ExecutorServiceWrapper.runOnSingleThread(Runnable runnable)`: Best for tasks that must run sequentially. Uses a single background thread.
- `ExecutorServiceWrapper.runOnMain(Runnable runnable)`: Post a task to be executed on the Android Main (UI) thread.

### Lifecycle Management

- `ExecutorServiceWrapper.shutdownAll()`: Shuts down all background executors. Call this when your application or library is being destroyed to prevent memory leaks.

---

## Usage Example

### activity_main.xml
The following layout provides buttons to trigger various threading scenarios.

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
            android:text="Status will appear here"
            android:padding="8dp"
            android:background="#F0F0F0"
            android:textSize="16sp"
            android:layout_marginBottom="16dp" />

        <Button
            android:id="@+id/btn_run_io"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Run on IO Executor" />

        <Button
            android:id="@+id/btn_run_network"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Run on Network Executor" />

        <Button
            android:id="@+id/btn_run_single"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Run on Single Thread" />

        <Button
            android:id="@+id/btn_run_main"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Run on Main Thread" />

        <Button
            android:id="@+id/btn_shutdown"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Shutdown All" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
Demonstrates how to switch from background executors back to the Main thread.

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

import com.rohit.baselibrary.threading.executor.ExecutorServiceWrapper;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.textView_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupClickListeners() {
        // 1. runOnIO: For resource-intensive tasks
        findViewById(R.id.btn_run_io).setOnClickListener(v -> {
            tvStatus.setText("Task started on IO Executor...");
            ExecutorServiceWrapper.runOnIO(() -> {
                simulateWork("IO Task Completed");
            });
        });

        // 2. runOnNetwork: For network calls
        findViewById(R.id.btn_run_network).setOnClickListener(v -> {
            tvStatus.setText("Task started on Network Executor...");
            ExecutorServiceWrapper.runOnNetwork(() -> {
                simulateWork("Network Task Completed");
            });
        });

        // 3. runOnSingleThread: For sequential processing
        findViewById(R.id.btn_run_single).setOnClickListener(v -> {
            tvStatus.setText("Task started on Single Thread Executor...");
            ExecutorServiceWrapper.runOnSingleThread(() -> {
                simulateWork("Single Thread Task Completed");
            });
        });

        // 4. runOnMain: To update UI
        findViewById(R.id.btn_run_main).setOnClickListener(v -> {
            ExecutorServiceWrapper.runOnMain(() -> {
                tvStatus.setText("Task executed directly on Main Thread");
                Toast.makeText(this, "Hello from Main Thread!", Toast.LENGTH_SHORT).show();
            });
        });

        // 5. shutdownAll: Resource cleanup
        findViewById(R.id.btn_shutdown).setOnClickListener(v -> {
            ExecutorServiceWrapper.shutdownAll();
            tvStatus.setText("All Executors Shutdown");
            Toast.makeText(this, "Executors Closed", Toast.LENGTH_SHORT).show();
        });
    }

    private void simulateWork(String resultMessage) {
        try {
            // Simulate background work
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final String finalMessage = resultMessage + " on: " + Thread.currentThread().getName();

        // Switch back to Main Thread to update UI
        ExecutorServiceWrapper.runOnMain(() -> {
            tvStatus.setText(finalMessage);
        });
    }
}
```
