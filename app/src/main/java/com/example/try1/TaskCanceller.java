package com.example.try1;

import android.os.AsyncTask;

public class TaskCanceller implements Runnable {

    private AsyncTask task;

    public TaskCanceller(AsyncTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
        }
    }

}
