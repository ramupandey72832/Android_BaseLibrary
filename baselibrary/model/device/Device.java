package com.rohit.baselibrary.model;

import android.content.Context;
import android.provider.Settings;

public class Device {
    private String id;
    private String name;

    // Constructor that generates a unique ID
    public Device(Context context, String name) {
        // Persistent unique ID for the device
        this.id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Device{name='" + name + "', id='" + id + "'}";
    }
}
