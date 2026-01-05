# Device Information Library

A simple utility to retrieve device-specific information like unique Android ID and device name.

## Features
- **Unique Device ID**: Retrieves the `ANDROID_ID` which is unique to each device.
- **Device Name**: Allows assigning a custom name or label to the device object.

## Method Documentation

### Initialization
```java
Device device = new Device(context, "DeviceName");
```

### 1. Get Device ID
Returns the persistent unique ID for the device.
```java
String id = device.getId();
```

### 2. Get Device Name
Returns the name assigned to the device object.
```java
String name = device.getName();
```

---

## Usage Example: Sending Device ID to Discord

This example demonstrates how to use the `Device` class along with `HttpClientWrapper` to send the device's unique ID to a Discord Webhook.

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
            android:textSize="14sp"
            android:fontFamily="monospace"
            android:layout_marginBottom="16dp" />

        <Button
            android:id="@+id/btn_send_device_id"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Send Device ID to Discord" />

    </LinearLayout>
</ScrollView>
```

### MainActivity.java
```java
package com.rohit.makingpermissionlibrary;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rohit.baselibrary.model.Device;
import com.rohit.baselibrary.network.HttpClientWrapper;

public class MainActivity extends AppCompatActivity {

    private static final String DISCORD_WEBHOOK_URL = "YOUR_DISCORD_WEBHOOK_URL";
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

        findViewById(R.id.btn_send_device_id).setOnClickListener(v -> sendDeviceIdToDiscord());
    }

    private void sendDeviceIdToDiscord() {
        // 1. Get Device ID using Device.java
        Device device = new Device(this, "SampleDevice");
        String deviceId = device.getId();

        // 2. Prepare JSON for Discord Webhook
        String jsonPayload = "{\"content\": \"Device ID: " + deviceId + "\"}";

        // 3. Send to Discord using HttpClientWrapper
        HttpClientWrapper httpClient = new HttpClientWrapper();
        tvStatus.setText("Sending Device ID...");
        
        httpClient.postJson(DISCORD_WEBHOOK_URL, jsonPayload, new HttpClientWrapper.Callback() {
            @Override
            public void onSuccess(String text) {
                tvStatus.setText("Successfully sent Device ID to Discord.");
                Toast.makeText(MainActivity.this, "Sent!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                tvStatus.setText("Failed to send Device ID: " + errorMessage);
                Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```
