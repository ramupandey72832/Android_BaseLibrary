# BootReceiver & BootWorker Library

A robust solution for handling device restart events in Android. This component ensures that background tasks are correctly rescheduled and logged even on devices using **File-Based Encryption (FBE)** by utilizing Device Protected Storage.

## Features
- **Reliable Boot Detection**: Listens for `BOOT_COMPLETED` and `LOCKED_BOOT_COMPLETED` to support Direct Boot.
- **Background Task Continuity**: Automatically schedules a `WorkManager` task (`BootWorker`) upon restart.
- **FBE Compatibility**: Uses `createDeviceProtectedStorageContext()` to allow access to logs and storage before the first user unlock.
- **Testing Support**: Includes a custom intent filter for easy simulation via ADB.

---

## Component Documentation

### 1. BootReceiver.java
The entry point for system boot broadcasts. It handles:
- `android.intent.action.BOOT_COMPLETED`
- `android.intent.action.LOCKED_BOOT_COMPLETED`
- `com.rohit.test.TRIGGER_BOOT` (Custom action for simulation)

It initializes a `OneTimeWorkRequest` for `BootWorker` to perform heavy lifting off the main thread.

### 2. BootWorker.java
A `ListenableWorker` that performs post-boot logic. 
- Logs a success message to `FileLogger`.
- Re-establishes app logic or triggers data synchronization.

---

## Testing & Simulation

To test the boot logic without restarting the device, use the following ADB command:

```bash
adb shell am broadcast -a com.rohit.test.TRIGGER_BOOT -p com.rohit.makingpermissionlibrary
```

---

## Usage Example (Verification)

Use the following `MainActivity` and layout to verify that the boot event was successfully processed and logged by the `BootWorker`.

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
            android:id="@+id/btn_refresh_logs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Refresh Logs (Verify Boot)" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
```java
package com.rohit.makingpermissionlibrary;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.rohit.baselibrary.utils.FileLogger.FileLogger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FileLogger logger;
    private TextView textViewLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textViewLogs = findViewById(R.id.textView_logs);
        logger = new FileLogger(this, "MainActivityLogger");
        
        findViewById(R.id.btn_refresh_logs).setOnClickListener(v -> refreshLogsDisplay());
        
        // Initial refresh to check for existing logs (including boot logs)
        refreshLogsDisplay();
    }

    private void refreshLogsDisplay() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logger.getLogFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            sb.append("No logs found or error reading file.");
        }
        textViewLogs.setText(sb.length() == 0 ? "Log file is currently empty." : sb.toString());
    }
}
```
