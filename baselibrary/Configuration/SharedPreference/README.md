# SharedPreferenceConfigurationHandler

A simple and efficient wrapper around Android's `SharedPreferences` to handle application configurations and settings with ease. It supports full CRUD (Create, Read, Update, Delete) operations for all standard data types.

## Features
- **Simple API**: Easy-to-use methods for storing and retrieving data.
- **Support for Multiple Types**: Handles `String`, `int`, `boolean`, `long`, and `float`.
- **Lightweight**: Uses standard Android APIs without any external dependencies.
- **Bulk Retrieval**: Fetch all stored configurations at once as a `Map`.

---

## Method Documentation

### Initialization
```java
SharedPreferenceConfigurationHandler prefHandler = new SharedPreferenceConfigurationHandler(context);
```

### 1. Create / Update (Put)
Stores a value asynchronously using `apply()`.
- `putString(String key, String value)`
- `putInt(String key, int value)`
- `putBoolean(String key, boolean value)`
- `putLong(String key, long value)`
- `putFloat(String key, float value)`

### 2. Read (Get)
Retrieves a stored value with a provided default value if the key is not found.
- `getString(String key, String defaultValue)`
- `getInt(String key, int defaultValue)`
- `getBoolean(String key, boolean defaultValue)`
- `getLong(String key, long defaultValue)`
- `getFloat(String key, float defaultValue)`

### 3. Utility Methods
- `getAllConfigs()`: Returns a `Map<String, ?>` containing all stored key-value pairs.
- `contains(String key)`: Returns `true` if the key exists in the preferences.
- `remove(String key)`: Removes a specific entry.
- `clearAllConfigs()`: Wipes all data stored in the preference file.

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
            android:text="Shared Preferences Samples"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_save_config"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Save 'username' = 'Rohit'" />

        <Button
            android:id="@+id/btn_get_config"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Get 'username' (Specific)" />

        <Button
            android:id="@+id/btn_get_all_config"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Get All Configs" />

        <Button
            android:id="@+id/btn_delete_config"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Delete 'username'" />

        <Button
            android:id="@+id/btn_clear_all"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Clear All Configs" />

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

import com.rohit.baselibrary.SharedPreferenceConfigurationHandler;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private SharedPreferenceConfigurationHandler prefHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        prefHandler = new SharedPreferenceConfigurationHandler(this);
        setupListeners();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.textView_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        // CREATE / UPDATE
        findViewById(R.id.btn_save_config).setOnClickListener(v -> {
            prefHandler.putString("username", "Rohit");
            prefHandler.putInt("user_age", 25);
            prefHandler.putBoolean("is_logged_in", true);
            showStatus("Data Saved: username=Rohit, age=25, logged_in=true");
        });

        // READ (Specific)
        findViewById(R.id.btn_get_config).setOnClickListener(v -> {
            String name = prefHandler.getString("username", "Unknown");
            int age = prefHandler.getInt("user_age", 0);
            showStatus("Retrieved: " + name + " (Age: " + age + ")");
        });

        // READ (All)
        findViewById(R.id.btn_get_all_config).setOnClickListener(v -> {
            Map<String, ?> allEntries = prefHandler.getAllConfigs();
            StringBuilder sb = new StringBuilder("All Configs:\n");
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            showStatus(sb.toString());
        });

        // DELETE
        findViewById(R.id.btn_delete_config).setOnClickListener(v -> {
            prefHandler.remove("username");
            showStatus("Key 'username' removed.");
        });

        // CLEAR ALL
        findViewById(R.id.btn_clear_all).setOnClickListener(v -> {
            prefHandler.clearAllConfigs();
            showStatus("All configurations cleared.");
        });
    }

    private void showStatus(String message) {
        tvStatus.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
```
