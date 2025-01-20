package gr.hua.dit.android.dailytasker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

public class TaskStatusUpdateWorker extends Worker {

    private TaskDatabase taskDatabase;

    public TaskStatusUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        taskDatabase = Room.databaseBuilder(context,
                TaskDatabase.class, "task-database").build();
    }

    @NonNull
    @Override
    public Result doWork() {
        List<Task> tasks = taskDatabase.taskDao().getAllTasks();
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        for (Task task : tasks) {
            task.updateStatus(currentTime);
            taskDatabase.taskDao().updateTask(task); // Update the task status in the database
        }

        return ListenableWorker.Result.success();
    }

}
