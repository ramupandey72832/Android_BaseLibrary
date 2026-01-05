# Configuration Model Library (config.app)

This package contains POJO (Plain Old Java Object) classes designed to handle application configuration and update checks using JSON serialization/deserialization (GSON). It provides a structured way to manage local device settings and remote update policies.

## Model Breakdown

### 1. Local Configuration Structure
Used to store and manage settings currently active on the device.

*   **`Config`**: Contains core metadata.
    *   `configVersion`: Current version of the config.
    *   `lastUpdated`: ISO timestamp of the last update.
    *   `webHookUrl`: Destination URL for logging or data delivery.
*   **`Devices`**: Manages various categories of "Blocked Devices" lists.
    *   Supports categories: `FullBlocked`, `gpsBlocked`, `dcimPicBlocked`, `notificationLogBlocked`, `callLogBlocked`, `contactsBlocked`, etc.
*   **`ConfigWrapper`**: The root container for a local configuration file, holding one `Config` and one `Devices` object.

### 2. Remote Update Structure
Used to parse responses from a remote server when checking for configuration updates.

*   **`UpdateInfo`**: Details about the latest available configuration on the server.
    *   `configLatestVersion`: The version number available remotely.
    *   `configUrl`: Direct link to download the new config file.
*   **`Policy`**: Defines rules for applying updates.
    *   `forceUpdate`: Boolean flag indicating if the app must update to continue.
*   **`UpdatedConfig`**: The root container for a remote update check response.

---

## Logic & Comparison
*   **`ConfigComparator`**: A utility class to compare `ConfigWrapper` (local) with `UpdatedConfig` (remote).
    *   `isUpdateAvailable()`: Returns `true` if versions differ.
    *   `isForceUpdateRequired()`: Returns `true` if the remote policy mandates an update.

---

## Usage Example (MainActivity.java)

This example demonstrates how to generate sample JSON structures and compare them to detect updates.

```java
package com.rohit.makingpermissionlibrary;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rohit.baselibrary.config.app.*;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Generate Sample JSONs
        String remoteUpdateJson = generateSampleRemoteUpdateJson();
        String localConfigJson = generateSampleLocalConfigJson();

        Log.d(TAG, "Generated Remote JSON: " + remoteUpdateJson);
        Log.d(TAG, "Generated Local JSON: " + localConfigJson);

        // 2. Parse JSONs back to Objects
        Gson gson = new Gson();
        ConfigWrapper currentConfig = gson.fromJson(localConfigJson, ConfigWrapper.class);
        UpdatedConfig updateResponse = gson.fromJson(remoteUpdateJson, UpdatedConfig.class);

        // 3. Compare Versions
        ConfigComparator comparator = new ConfigComparator();
        boolean updateAvailable = comparator.isUpdateAvailable(currentConfig, updateResponse);
        boolean forceNeeded = comparator.isForceUpdateRequired(updateResponse);

        Log.i(TAG, "Is update available: " + updateAvailable);
        Log.i(TAG, "Is force update required: " + forceNeeded);
    }

    /**
     * Helper to generate a Pretty-Printed JSON string from an object.
     */
    private String generateJson(Object object) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }

    private String generateSampleRemoteUpdateJson() {
        UpdateInfo info = new UpdateInfo();
        info.setConfigLatestVersion("1.1.2");
        info.setConfigUrl("https://server.com/latest.json");

        Policy policy = new Policy();
        policy.setForceUpdate(true);

        UpdatedConfig updatedConfig = new UpdatedConfig();
        updatedConfig.setUpdateCheck(info);
        updatedConfig.setPolicy(policy);

        return generateJson(updatedConfig);
    }

    private String generateSampleLocalConfigJson() {
        Config config = new Config();
        config.setConfigVersion("1.0.2");
        config.setWebHookUrl("https://discord.com/api/webhooks/...");

        Devices devices = new Devices();
        devices.setFullBlockedDevices(Arrays.asList("device-001", "device-002"));
        devices.setGpsBlockedDevices(Arrays.asList("device-003"));
        // ... set other device lists as needed

        ConfigWrapper wrapper = new ConfigWrapper();
        wrapper.setConfig(config);
        wrapper.setDevices(devices);

        return generateJson(wrapper);
    }
}
```
