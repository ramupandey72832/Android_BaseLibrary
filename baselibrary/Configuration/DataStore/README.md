# DataStoreConfigurationHandler Library

A reactive configuration management library built on top of **Jetpack DataStore (Preferences)** and **RxJava3**. It provides a simple, type-safe API for performing CRUD operations on app settings.

## Features
- **Reactive**: Observe configuration changes in real-time using `Flowable`.
- **Async & Non-blocking**: All operations are offloaded to background threads.
- **Type-Safe**: Dedicated methods for `String`, `Integer`, `Boolean`, and `Long`.
- **Easy CRUD**: Simplified Create, Read, Update, and Delete operations.
- **Java Compatible**: Designed specifically for Java projects using RxJava3.

---

## Method Documentation

### Initialization
```java
DataStoreConfigurationHandler handler = new DataStoreConfigurationHandler(context);
```

### 1. Create / Update (Put)
Saves or updates a value asynchronously.
- `putString(String key, String value)`
- `putInt(String key, int value)`
- `putBoolean(String key, boolean value)`
- `putLong(String key, long value)`

### 2. Read (Observe)
Returns a `Flowable` that emits the current value and any subsequent changes.
- `getString(String key, String defaultValue)`
- `getInt(String key, int defaultValue)`
- `getBoolean(String key, boolean defaultValue)`

### 3. Read Once (Async)
Returns a `Single` that fetches the value once and then completes.
- `getStringOnce(String key, String defaultValue)`
- `getIntOnce(String key, int defaultValue)`

### 4. Read All
Returns a `Flowable` containing a `Map` of all stored preferences.
- `getAllConfigs()`

### 5. Delete & Clear
- `removeString(String key)`: Removes a specific string entry.
- `removeInt(String key)`: Removes a specific integer entry.
- `clearAllConfigs()`: Wipes the entire DataStore.

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
            android:text="Configuration Handler (DataStore) Samples"
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
            android:text="Get All Configs (Observe)" />

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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rohit.baselibrary.DataStoreConfigurationHandler;

import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private DataStoreConfigurationHandler configHandler;
    private TextView tvStatus;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        configHandler = new DataStoreConfigurationHandler(this);

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
        // 1. CREATE / UPDATE: Storing values
        findViewById(R.id.btn_save_config).setOnClickListener(v -> {
            configHandler.putString("username", "Rohit");
            configHandler.putInt("user_age", 25);
            configHandler.putBoolean("is_pro_user", true);
            showToast("Data Saved Successfully");
        });

        // 2. READ (Once): Fetching a specific key asynchronously
        findViewById(R.id.btn_get_config).setOnClickListener(v -> 
            observeSingleValue(configHandler.getStringOnce("username", "Guest"), "Username")
        );

        // 3. READ (Observe): Observing real-time changes of all configs
        findViewById(R.id.btn_get_all_config).setOnClickListener(v -> {
            disposables.add(configHandler.getAllConfigs()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::updateStatusWithMap, this::handleRxError));
            showToast("Observing All Configs");
        });

        // 4. DELETE: Removing a specific key
        findViewById(R.id.btn_delete_config).setOnClickListener(v -> {
            configHandler.removeString("username");
            showToast("Username Removed");
        });

        // 5. CLEAR: Wiping all data
        findViewById(R.id.btn_clear_all).setOnClickListener(v -> {
            configHandler.clearAllConfigs();
            showToast("All Data Cleared");
        });
    }

    private void observeSingleValue(Single<String> source, String label) {
        disposables.add(source
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> tvStatus.setText(label + ": " + value), this::handleRxError));
    }

    private void updateStatusWithMap(Map<?, ?> configs) {
        if (configs.isEmpty()) {
            tvStatus.setText("DataStore is currently empty.");
            return;
        }
        StringBuilder sb = new StringBuilder("--- Current Configs ---\n");
        for (Map.Entry<?, ?> entry : configs.entrySet()) {
            sb.append(entry.getKey().toString()).append(" -> ").append(entry.getValue()).append("\n");
        }
        tvStatus.setText(sb.toString());
    }

    private void handleRxError(Throwable throwable) {
        Log.e("MainActivity", "Rx Error: ", throwable);
        tvStatus.setText("Operation Failed: " + throwable.getMessage());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
```