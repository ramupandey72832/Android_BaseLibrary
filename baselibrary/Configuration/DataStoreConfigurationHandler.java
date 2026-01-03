package com.rohit.baselibrary;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * ConfigurationHandler
 * Handles app configuration using Jetpack DataStore (Preferences) with RxJava3.
 * Supports CRUD operations for multiple data types.
 */
public class DataStoreConfigurationHandler {

    private static final String DATASTORE_NAME = "app_config";
    private final RxDataStore<Preferences> dataStore;

    public DataStoreConfigurationHandler(Context context) {
        // Initialize RxDataStore
        dataStore = new RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build();
    }

    // --- CREATE / UPDATE Operations ---

    public void putString(String key, String value) {
        update(PreferencesKeys.stringKey(key), value);
    }

    public void putInt(String key, int value) {
        update(PreferencesKeys.intKey(key), value);
    }

    public void putBoolean(String key, boolean value) {
        update(PreferencesKeys.booleanKey(key), value);
    }

    public void putLong(String key, long value) {
        update(PreferencesKeys.longKey(key), value);
    }

    private <T> void update(Preferences.Key<T> key, T value) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.set(key, value);
            return Single.just(mutablePrefs);
        });
    }

    // --- READ Operations (Observe stream of changes) ---

    public Flowable<String> getString(String key, String defaultValue) {
        return observe(PreferencesKeys.stringKey(key), defaultValue);
    }

    public Flowable<Integer> getInt(String key, int defaultValue) {
        return observe(PreferencesKeys.intKey(key), defaultValue);
    }

    public Flowable<Boolean> getBoolean(String key, boolean defaultValue) {
        return observe(PreferencesKeys.booleanKey(key), defaultValue);
    }

    private <T> Flowable<T> observe(Preferences.Key<T> key, T defaultValue) {
        return dataStore.data().map(prefs -> {
            T value = prefs.get(key);
            return value != null ? value : defaultValue;
        });
    }

    // --- READ Operations (Fetch once asynchronously) ---

    public Single<String> getStringOnce(String key, String defaultValue) {
        return fetchOnce(PreferencesKeys.stringKey(key), defaultValue);
    }

    public Single<Integer> getIntOnce(String key, int defaultValue) {
        return fetchOnce(PreferencesKeys.intKey(key), defaultValue);
    }

    private <T> Single<T> fetchOnce(Preferences.Key<T> key, T defaultValue) {
        return dataStore.data()
                .firstOrError()
                .map(prefs -> {
                    T value = prefs.get(key);
                    return value != null ? value : defaultValue;
                })
                .onErrorReturnItem(defaultValue);
    }

    /**
     * Retrieve all configurations as a Map.
     */
    public Flowable<Map<Preferences.Key<?>, Object>> getAllConfigs() {
        return dataStore.data().map(Preferences::asMap);
    }

    // --- DELETE Operations ---

    public void removeString(String key) {
        remove(PreferencesKeys.stringKey(key));
    }

    public void removeInt(String key) {
        remove(PreferencesKeys.intKey(key));
    }

    private <T> void remove(Preferences.Key<T> key) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.remove(key);
            return Single.just(mutablePrefs);
        });
    }

    public void clearAllConfigs() {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.clear();
            return Single.just(mutablePrefs);
        });
    }
}