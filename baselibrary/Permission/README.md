# PermissionManager Library

A simplified Android Permission Management library that handles both normal and special permissions (like `SYSTEM_ALERT_WINDOW`, `WRITE_SETTINGS`) with version-aware logic.

## Features

- **Unified API**: Request standard and special permissions using the same methods.
- **Version Aware**: Automatically handles API-specific permissions (e.g., Media permissions on Android 13+, Foreground Service types on Android 14+).
- **Batch Requests**: Request multiple permissions at once.
- **Status Reporting**: Get a human-readable status report for a list of permissions.

---

## Prerequisites

Add the required permissions to your `AndroidManifest.xml`:

```xml
<manifest ...>
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA"/>
    <!-- Add others as needed for your use case -->
</manifest>
```

---

## Method Documentation

### 1. `PermissionManager(ComponentActivity activity, PermissionCallback callback)`
Initializes the manager. It must be called during `onCreate` or as a member variable initialization because it registers an `ActivityResultLauncher`.

### 2. `boolean isPermissionGranted(String permissionName)`
Checks if a specific runtime permission is granted.

### 3. `void requestPermission(Activity activity, String permissionName)`
Requests a single permission. It automatically detects if a permission requires a special Intent (like Overlay permissions) or a standard system dialog.

### 4. `void requestPermissionsBatch(Activity activity, List<String> permissions)`
Loops through a list of permissions and requests each one.

### 5. `Map<String, String> getPermissionStatusReport(List<String> permissions)`
Returns a Map where keys are permission names and values are status strings (e.g., "GRANTED ✅", "DENIED ❌", "SPECIAL HANDLED ⚙️").

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
            android:text="Permission Status will appear here"
            android:padding="8dp"
            android:background="#F0F0F0"
            android:layout_marginBottom="16dp" />

        <Button
            android:id="@+id/btn_is_granted"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Check if CAMERA is Granted" />

        <Button
            android:id="@+id/btn_request_single"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Request CAMERA Permission" />

        <Button
            android:id="@+id/btn_request_special"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Request SYSTEM_ALERT_WINDOW" />

        <Button
            android:id="@+id/btn_request_batch"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Request Batch Permissions" />

        <Button
            android:id="@+id/btn_status_report"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Get Status Report" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
```java
package com.rohit.makingpermissionlibrary;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rohit.baselibrary.PermissionManager;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private PermissionManager manager;
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

        // 1. Initialize PermissionManager
        manager = new PermissionManager(this, (granted, denied) -> {
            tvStatus.setText("Callback Result:\n");
            tvStatus.append("Granted: " + granted + "\n");
            tvStatus.append("Denied: " + denied + "\n");
        });

        // 2. Sample: isPermissionGranted(String)
        findViewById(R.id.btn_is_granted).setOnClickListener(v -> {
            boolean isGranted = manager.isPermissionGranted(Manifest.permission.CAMERA);
            Toast.makeText(this, "Camera Granted: " + isGranted, Toast.LENGTH_SHORT).show();
        });

        // 3. Sample: requestPermission(Activity, String) - Normal Permission
        findViewById(R.id.btn_request_single).setOnClickListener(v -> {
            manager.requestPermission(this, Manifest.permission.CAMERA);
        });

        // 4. Sample: requestPermission(Activity, String) - Special Permission
        findViewById(R.id.btn_request_special).setOnClickListener(v -> {
            manager.requestPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW);
        });

        // 5. Sample: requestPermissionsBatch(Activity, List<String>)
        findViewById(R.id.btn_request_batch).setOnClickListener(v -> {
            List<String> perms = List.of(
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_MEDIA_IMAGES
            );
            manager.requestPermissionsBatch(this, perms);
        });

        // 6. Sample: getPermissionStatusReport(List<String>)
        findViewById(R.id.btn_status_report).setOnClickListener(v -> {
            List<String> perms = List.of(
                    Manifest.permission.CAMERA,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.POST_NOTIFICATIONS
            );
            Map<String, String> report = manager.getPermissionStatusReport(perms);
            
            tvStatus.setText("Status Report:\n");
            for (Map.Entry<String, String> entry : report.entrySet()) {
                tvStatus.append(entry.getKey().replace("android.permission.", "") + ": " + entry.getValue() + "\n");
            }
        });
    }
}
```