package com.rohit.baselibrary;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * SharedPreferenceConfigurationHandler
 * Handles app configuration using standard Android SharedPreferences.
 * Supports CRUD operations for multiple data types.
 */
public class SharedPreferenceConfigurationHandler {

    private static final String PREF_NAME = "app_prefs";
    private final SharedPreferences sharedPreferences;

    public SharedPreferenceConfigurationHandler(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- CREATE / UPDATE Operations ---

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    // --- READ Operations ---

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * Retrieve all configurations as a Map.
     */
    public Map<String, ?> getAllConfigs() {
        return sharedPreferences.getAll();
    }

    // --- DELETE Operations ---

    /**
     * Remove a specific key from preferences.
     */
    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    /**
     * Clear all configurations.
     */
    public void clearAllConfigs() {
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Check if a key exists in preferences.
     */
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }
}
