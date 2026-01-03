# SchedulerWrapper Library

A lightweight utility class to simplify thread management in Android applications using **RxJava3**. It provides easy, static access to various Schedulers and helper methods to switch between background and UI threads without the boilerplate of complex Rx chains.

## Features
- **Centralized Threading**: Unified access to `IO`, `Main`, `Computation`, `Single`, and `NewThread` schedulers.
- **Easy Context Switching**: Simplified methods to run tasks on background threads and update the UI.
- **Testability**: Facilitates unit testing by allowing easy swapping or mocking of schedulers.
- **Clean Syntax**: Reduces the need for long `subscribeOn` and `observeOn` chains for simple tasks.

---

## Method Documentation

### Static Schedulers
These methods return the standard RxJava3 Schedulers for use in streams.
- `SchedulerWrapper.io()`: Optimized for I/O-bound work (Network, Database, Disk).
- `SchedulerWrapper.main()`: The Android Main thread for UI operations.
- `SchedulerWrapper.computation()`: Optimized for CPU-intensive tasks.
- `SchedulerWrapper.single()`: Executes work on a single, shared background thread.
- `SchedulerWrapper.newThread()`: Spawns a new thread for every unit of work.

### Helper Methods
Quickly execute a `Runnable` on a specific thread.
- `SchedulerWrapper.runOnIO(Runnable runnable)`: Immediately schedules a task on the IO scheduler.
- `SchedulerWrapper.runOnMain(Runnable runnable)`: Immediately schedules a task on the Android Main thread.

---

## Usage Example

### activity_main.xml
Define the UI components to trigger and observe threading operations.
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
            android:text="Run on IO" />

        <Button
            android:id="@+id/btn_run_main"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Run on Main" />

        <Button
            android:id="@+id/btn_io_scheduler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Use IO Scheduler" />

        <Button
            android:id="@+id/btn_main_scheduler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Use Main Scheduler" />

        <Button
            android:id="@+id/btn_computation_scheduler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Use Computation Scheduler" />

        <Button
            android:id="@+id/btn_single_scheduler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Use Single Scheduler" />

        <Button
            android:id="@+id/btn_new_thread_scheduler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Use New Thread Scheduler" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
Demonstrate how to wire UI events to the `SchedulerWrapper` methods.
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

import com.rohit.baselibrary.threading.Scheduler.SchedulerWrapper;

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
        // 1. runOnIO: Quick background execution
        findViewById(R.id.btn_run_io).setOnClickListener(v -> {
            tvStatus.setText("Running on IO...");
            SchedulerWrapper.runOnIO(() -> {
                logAndToastOnMain("Executed via runOnIO() on: " + Thread.currentThread().getName());
            });
        });

        // 2. runOnMain: Explicit UI thread execution
        findViewById(R.id.btn_run_main).setOnClickListener(v -> {
            SchedulerWrapper.runOnMain(() -> {
                tvStatus.setText("Executed via runOnMain() on: " + Thread.currentThread().getName());
            });
        });

        // 3. io() scheduler: Standard Rx access
        findViewById(R.id.btn_io_scheduler).setOnClickListener(v -> {
            tvStatus.setText("Using IO Scheduler...");
            SchedulerWrapper.io().scheduleDirect(() -> {
                logAndToastOnMain("Executed using io() Scheduler on: " + Thread.currentThread().getName());
            });
        });

        // 4. main() scheduler: Standard Rx access
        findViewById(R.id.btn_main_scheduler).setOnClickListener(v -> {
            SchedulerWrapper.main().scheduleDirect(() -> {
                tvStatus.setText("Executed using main() Scheduler on: " + Thread.currentThread().getName());
            });
        });

        // 5. computation() scheduler: For processing
        findViewById(R.id.btn_computation_scheduler).setOnClickListener(v -> {
            tvStatus.setText("Using Computation Scheduler...");
            SchedulerWrapper.computation().scheduleDirect(() -> {
                logAndToastOnMain("Executed using computation() Scheduler on: " + Thread.currentThread().getName());
            });
        });

        // 6. single() scheduler: Sequential background work
        findViewById(R.id.btn_single_scheduler).setOnClickListener(v -> {
            tvStatus.setText("Using Single Scheduler...");
            SchedulerWrapper.single().scheduleDirect(() -> {
                logAndToastOnMain("Executed using single() Scheduler on: " + Thread.currentThread().getName());
            });
        });

        // 7. newThread() scheduler: Independent execution
        findViewById(R.id.btn_new_thread_scheduler).setOnClickListener(v -> {
            tvStatus.setText("Using New Thread Scheduler...");
            SchedulerWrapper.newThread().scheduleDirect(() -> {
                logAndToastOnMain("Executed using newThread() Scheduler on: " + Thread.currentThread().getName());
            });
        });
    }

    /**
     * Helper to update UI from background threads.
     */
    private void logAndToastOnMain(String message) {
        SchedulerWrapper.runOnMain(() -> {
            tvStatus.setText(message);
            Toast.makeText(MainActivity.this, "Task Finished", Toast.LENGTH_SHORT).show();
        });
    }
}
```
