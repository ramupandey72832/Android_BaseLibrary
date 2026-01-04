# FileLogger Library

A robust, file-based logging utility for Android that supports automatic log rotation based on date and file size. It helps developers persist logs locally on the device for debugging and auditing purposes.

## Features
- **Daily Rotation**: Automatically creates a new log file for each day.
- **Size-based Rotation**: Rotates the log file if it exceeds a predefined threshold (default: 1MB).
- **Multiple Log Levels**: Dedicated methods for `INFO`, `DEBUG`, `WARN`, and `ERROR`.
- **Logcat Sync**: Simultaneously outputs logs to the system Logcat for real-time monitoring.
- **Internal Storage**: Stores logs securely in the application's internal files directory.

---

## Method Documentation

### Initialization
```java
FileLogger logger = new FileLogger(context, "YourCustomTag");
```

### 1. Logging Methods
Appends a timestamped log entry to the current log file.
- `logInfo(String message)`
- `logDebug(String message)`
- `logWarning(String message)`
- `logError(String message)`

### 2. File Access
- `getLogFile()`: Returns the `File` object pointing to the current active log file.

---

## Usage Example

### activity_main.xml
A simple UI to trigger logs and display the file content.
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
            android:id="@+id/textView_logs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Logs will appear here"
            android:padding="8dp"
            android:background="#F0F0F0"
            android:textSize="12sp"
            android:fontFamily="monospace"
            android:layout_marginBottom="16dp" />

        <Button
            android:id="@+id/btn_log_info"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Log INFO" />

        <Button
            android:id="@+id/btn_log_debug"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Log DEBUG" />

        <Button
            android:id="@+id/btn_log_warn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Log WARNING" />

        <Button
            android:id="@+id/btn_log_error"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Log ERROR" />

        <Button
            android:id="@+id/btn_refresh_logs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Refresh Display" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
Demonstrates how to log events and read them back from the file.
```java
package com.rohit.makingpermissionlibrary;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rohit.baselibrary.utils.FileLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FileLogger logger;
    private TextView textViewLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        initViews();
        
        // Initialize the FileLogger
        logger = new FileLogger(this, "MainActivityLogger");
        
        setupListeners();
        refreshLogsDisplay();
    }

    private void initViews() {
        textViewLogs = findViewById(R.id.textView_logs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        findViewById(R.id.btn_log_info).setOnClickListener(v -> {
            logger.logInfo("This is an INFO log message.");
            refreshLogsDisplay();
        });

        findViewById(R.id.btn_log_debug).setOnClickListener(v -> {
            logger.logDebug("This is a DEBUG log message.");
            refreshLogsDisplay();
        });

        findViewById(R.id.btn_log_warn).setOnClickListener(v -> {
            logger.logWarning("This is a WARNING log message.");
            refreshLogsDisplay();
        });

        findViewById(R.id.btn_log_error).setOnClickListener(v -> {
            logger.logError("This is an ERROR log message.");
            refreshLogsDisplay();
        });

        findViewById(R.id.btn_refresh_logs).setOnClickListener(v -> refreshLogsDisplay());
    }

    /**
     * Reads the current log file and displays it in the TextView.
     */
    private void refreshLogsDisplay() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logger.getLogFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            sb.append("Error reading log file or file is empty.");
        }
        
        if (sb.length() == 0) {
            textViewLogs.setText("Log file is currently empty.");
        } else {
            textViewLogs.setText(sb.toString());
        }
    }
}
```
