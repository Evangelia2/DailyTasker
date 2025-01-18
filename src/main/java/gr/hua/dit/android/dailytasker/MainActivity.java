package gr.hua.dit.android.dailytasker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TaskDatabase taskDatabase;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView todayDate = findViewById(R.id.today_date);

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Set the formatted date to the TextView
        todayDate.setText(currentDate);


        // Initialize the database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "task-database").allowMainThreadQueries().build();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load tasks from database
        List<Task> tasks = taskDatabase.taskDao().getAllTasks();

        // Set up the adapter with a click listener
        taskAdapter = new TaskAdapter(tasks, task -> {
            Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
            intent.putExtra("taskId", task.getUid()); // Pass the task ID to the new activity
            intent.putExtra("task_name", task.getShortName());
            intent.putExtra("task_status", task.getStatus());

            Log.d("MainActivity", "Task ID: " + task.getUid()); // Debug log
            Log.d("MainActivity", "Task Name: " + task.getShortName());
            Log.d("MainActivity", "Task Status: " + task.getStatus());
            // Add other task details if necessary
            startActivity(intent);
        });

        recyclerView.setAdapter(taskAdapter);




        // Add a button to navigate to the CreateTaskActivity
        Button createTaskButton = findViewById(R.id.btnAddTask);
        createTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });

        Button buttonDeleteTask = findViewById(R.id.btnDeleteTask);
        buttonDeleteTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeleteTaskActivity.class);
            startActivity(intent);
        });

        // Schedule periodic task updates
        WorkManager workManager = WorkManager.getInstance(this);
        PeriodicWorkRequest taskStatusCheckRequest =
                new PeriodicWorkRequest.Builder(TaskStatusUpdateWorker.class, 1, TimeUnit.HOURS)
                        .build();
        workManager.enqueueUniquePeriodicWork(
                "TaskStatusUpdateWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                taskStatusCheckRequest
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload tasks when returning to the MainActivity
        List<Task> tasks = taskDatabase.taskDao().getAllTasks();
        Log.d("MainActivity", "Tasks loaded: " + tasks.size());

        if (taskAdapter != null) {
            taskAdapter.updateTasks(tasks);
        } else {
            // Fallback to initialize adapter in case of null
            taskAdapter = new TaskAdapter(tasks, task -> {
                Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                intent.putExtra("taskId", task.getUid()); // Pass the task ID to the new activity
                startActivity(intent);
            });
            RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
            recyclerView.setAdapter(taskAdapter);
        }
    }

}