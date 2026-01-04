package com.rohit.baselibrary.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * File-based logger with rotation support.
 * Rotates logs daily or when file size exceeds a threshold.
 */
public class FileLogger {

    private static final String DEFAULT_TAG = "FileLogger";
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    private static final String DATE_PATTERN = "yyyy-MM-dd"; // daily rotation

    private final String tag;
    private final Context context;
    private File logFile;

    public FileLogger(Context context, String tag) {
        this.context = context;
        this.tag = (tag != null && !tag.isEmpty()) ? tag : DEFAULT_TAG;
        this.logFile = getRotatedLogFile();
    }

    public FileLogger(Context context) {
        this(context, DEFAULT_TAG);
    }

    private File getRotatedLogFile() {
        String date = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(new Date());
        File file = new File(context.getFilesDir(), "events_log_" + date + ".txt");

        // If file exceeds max size, create a new one with suffix
        if (file.exists() && file.length() > MAX_FILE_SIZE) {
            int counter = 1;
            File rotated;
            do {
                rotated = new File(context.getFilesDir(),
                        "events_log_" + date + "_" + counter + ".txt");
                counter++;
            } while (rotated.exists());
            file = rotated;
        }
        return file;
    }

    private void rotateIfNeeded() {
        String today = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(new Date());
        if (!logFile.getName().contains(today) || logFile.length() > MAX_FILE_SIZE) {
            logFile = getRotatedLogFile();
        }
    }

    public void log(String level, String message) {
        rotateIfNeeded();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        String logEntry = timestamp + " [" + level + "] " + message + "\n";

        // Logcat output
        Log.i(tag, logEntry);

        // Append to file
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.append(logEntry);
        } catch (IOException e) {
            Log.e(tag, "Failed to write log to file", e);
        }
    }

    public void logInfo(String message) {
        log("INFO", message);
    }

    public void logDebug(String message) {
        log("DEBUG", message);
    }

    public void logWarning(String message) {
        log("WARN", message);
    }

    public void logError(String message) {
        log("ERROR", message);
    }

    public File getLogFile() {
        return logFile;
    }
}
