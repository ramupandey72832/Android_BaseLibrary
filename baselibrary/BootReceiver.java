package com.rohit.baselibrary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.rohit.baselibrary.utils.FileLogger.FileLogger;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action) ||
                "com.rohit.test.TRIGGER_BOOT".equals(action)) {
                // Remove Com.rohit.test.Trigger_Boot.equals(action) from the if statement
                // and Manifest on Final Publication

            // Use Device Protected Storage if needed
            Context storageContext = context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                storageContext = context.createDeviceProtectedStorageContext();
            }

            // Schedule work with WorkManager
            OneTimeWorkRequest workRequest =
                    new OneTimeWorkRequest.Builder(BootWorker.class)
                            .build();

            WorkManager.getInstance(storageContext).enqueue(workRequest);
        }
    }
}
