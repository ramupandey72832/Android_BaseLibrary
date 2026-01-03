package com.rohit.baselibrary.threading;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * WorkManagerHandler
 * A wrapper class to simplify scheduling and managing background tasks using Android WorkManager.
 */
public class WorkManagerHandler {

    private final WorkManager workManager;

    public WorkManagerHandler(Context context) {
        this.workManager = WorkManager.getInstance(context);
    }

    /**
     * Schedule a one-time background task.
     *
     * @param workerClass The class extending ListenableWorker to run.
     * @param tag         A unique tag for the work.
     * @param inputData   Key-value pairs to pass to the worker.
     */
    public void scheduleOneTimeWork(Class<? extends ListenableWorker> workerClass, String tag, Data inputData) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(workerClass)
                .addTag(tag)
                .setInputData(inputData)
                .setConstraints(getDefaultConstraints())
                .build();

        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, request);
    }

    /**
     * Schedule a periodic background task.
     *
     * @param workerClass   The class extending ListenableWorker to run.
     * @param tag           A unique tag for the work.
     * @param repeatInterval The interval between runs.
     * @param timeUnit       The unit of time for the interval.
     */
    public void schedulePeriodicWork(Class<? extends ListenableWorker> workerClass, String tag, long repeatInterval, TimeUnit timeUnit) {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(workerClass, repeatInterval, timeUnit)
                .addTag(tag)
                .setConstraints(getDefaultConstraints())
                .build();

        workManager.enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.UPDATE, request);
    }

    /**
     * Cancel a specific work by its tag.
     */
    public void cancelWorkByTag(String tag) {
        workManager.cancelAllWorkByTag(tag);
    }

    /**
     * Cancel work by its unique name.
     */
    public void cancelUniqueWork(String name) {
        workManager.cancelUniqueWork(name);
    }

    /**
     * Cancel all background work scheduled by this app.
     */
    public void cancelAllWork() {
        workManager.cancelAllWork();
    }

    /**
     * Get LiveData to observe the status of work by tag.
     */
    public LiveData<List<WorkInfo>> getWorkInfoByTag(String tag) {
        return workManager.getWorkInfosByTagLiveData(tag);
    }

    /**
     * A helper to get the status of unique work.
     */
    public LiveData<List<WorkInfo>> getWorkInfosForUniqueWork(String name) {
        return workManager.getWorkInfosForUniqueWorkLiveData(name);
    }

    /**
     * Internal helper for common constraints (e.g., require internet).
     */
    private Constraints getDefaultConstraints() {
        return new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
    }
}
