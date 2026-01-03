# WorkManagerHandler Library

A simplified wrapper around Android's **WorkManager** API to manage background tasks with ease. It supports one-time and periodic work requests, task monitoring via LiveData, and centralized cancellation.

## Features
- **Simplified Scheduling**: Single methods for one-time and periodic tasks.
- **Unique Work Support**: Ensures only one instance of a specific task runs at a time using unique names.
- **Automatic Constraints**: Tasks are pre-configured to run only when a network connection is available.
- **Reactive Status Tracking**: Observe the status of your background tasks using LiveData.
- **Easy Cleanup**: Methods to cancel specific unique tasks or all background work.

---

## Method Documentation

### Initialization
```java
WorkManagerHandler workHandler = new WorkManagerHandler(context);
```

### 1. Schedule One-Time Work
Enqueues a one-time task that runs as soon as constraints are met.
```java
workHandler.scheduleOneTimeWork(SampleWorker.class, "task_unique_name", inputData);
```

### 2. Schedule Periodic Work
Enqueues a recurring task. Note: The minimum interval allowed by Android is 15 minutes.
```java
workHandler.schedulePeriodicWork(SampleWorker.class, "periodic_unique_name", 15, TimeUnit.MINUTES);
```

### 3. Observe Work Status
Returns `LiveData` containing the list of `WorkInfo` for the specified unique work name.
```java
workHandler.getWorkInfosForUniqueWork("unique_name").observe(lifecycleOwner, workInfos -> {
    // Check work state (ENQUEUED, RUNNING, SUCCEEDED, etc.)
});
```

### 4. Cancellation
- `cancelUniqueWork(String name)`: Cancels a specific unique work request.
- `cancelAllWork()`: Cancels all background work scheduled by the app.

---

## Usage Example

### activity_main.xml
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
            android:layout_marginBottom="16dp" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="WorkManager Samples"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_schedule_one_time"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Schedule One-Time Work" />

        <Button
            android:id="@+id/btn_schedule_periodic"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Schedule Periodic Work" />

        <Button
            android:id="@+id/btn_observe_work"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Observe Work Status" />

        <Button
            android:id="@+id/btn_cancel_unique"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Cancel Unique Work" />

        <Button
            android:id="@+id/btn_cancel_all"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Cancel All Work" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
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
import androidx.work.Data;

import com.rohit.baselibrary.threading.WorkManagerHandler;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private WorkManagerHandler workHandler;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        workHandler = new WorkManagerHandler(this);
        setupWorkManagerSamples();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.textView_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupWorkManagerSamples() {
        // 1. One-Time Work Request
        findViewById(R.id.btn_schedule_one_time).setOnClickListener(v -> {
            Data input = new Data.Builder().putString("key", "one_time_value").build();
            workHandler.scheduleOneTimeWork(SampleWorker.class, "one_time_task", input);
            showStatus("Scheduled One-Time Work: 'one_time_task'");
        });

        // 2. Periodic Work Request (Minimum interval is 15 minutes)
        findViewById(R.id.btn_schedule_periodic).setOnClickListener(v -> {
            workHandler.schedulePeriodicWork(SampleWorker.class, "periodic_task", 15, TimeUnit.MINUTES);
            showStatus("Scheduled Periodic Work: 'periodic_task' (15 min interval)");
        });

        // 3. Observe Work Status (Example for unique work)
        findViewById(R.id.btn_observe_work).setOnClickListener(v -> {
            workHandler.getWorkInfosForUniqueWork("one_time_task").observe(this, workInfos -> {
                if (workInfos != null && !workInfos.isEmpty()) {
                    String state = workInfos.get(0).getState().name();
                    showStatus("Work Status ('one_time_task'): " + state);
                } else {
                    showStatus("No work info found for 'one_time_task'");
                }
            });
        });

        // 4. Cancel Specific Work
        findViewById(R.id.btn_cancel_unique).setOnClickListener(v -> {
            workHandler.cancelUniqueWork("one_time_task");
            showStatus("Cancelled unique work: 'one_time_task'");
        });

        // 5. Cancel All Work
        findViewById(R.id.btn_cancel_all).setOnClickListener(v -> {
            workHandler.cancelAllWork();
            showStatus("All background work cancelled.");
        });
    }

    private void showStatus(String message) {
        if (tvStatus != null) tvStatus.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
```

### SampleWorker.java
```java
package com.rohit.makingpermissionlibrary;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SampleWorker extends Worker {
    public SampleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SampleWorker", "Background task is running...");
        // Do some background work here
        return Result.success();
    }
}
```
