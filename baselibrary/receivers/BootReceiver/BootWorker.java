package com.rohit.baselibrary.receivers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rohit.baselibrary.utils.FileLogger.FileLogger;

public class BootWorker extends Worker {
    public BootWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("BootWorker", "Running post-boot tasks...");

        // Example: log to file
        FileLogger logger = new FileLogger(getApplicationContext(), "BootWorker");
        logger.logInfo("Device restarted, WorkManager task executed.");

        // Do your actual boot-time work here
        return Result.success();
    }
}
