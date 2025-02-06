package gr.hua.dit.android.dailytasker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        for (Task task : tasks) {
            Log.d("MainActivity", "Task Loaded: ID=" + task.getUid() +
                    ", Name=" + task.getShortName() +
                    ", Duration=" + task.getDuration());
        }

        // Set up the adapter with a click listener
        taskAdapter = new TaskAdapter(tasks, task -> {
            Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
            intent.putExtra("task_id", String.valueOf(task.getUid())); // Pass the task ID to the new activity
            intent.putExtra("task_name", task.getShortName());
            intent.putExtra("task_status", task.getStatus());
            intent.putExtra("task_description", task.getDescription());
            intent.putExtra("task_duration", String.valueOf(task.getDuration()));
            Log.d("MainActivity", "Passing Duration: " + task.getDuration());

            intent.putExtra("task_start_time", task.getStartTime());
            intent.putExtra("task_location", task.getLocation());

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

        Button exportTasksButton = findViewById(R.id.btnExportTasks);
        exportTasksButton.setOnClickListener(v -> exportTasksToFile());

        // Add a button to show completed tasks
        Button btnCompletedTasks = findViewById(R.id.btnCompletedTasks);
        btnCompletedTasks.setOnClickListener(v -> {
            // Query the database for completed tasks
            List<Task> completedTasks = taskDatabase.taskDao().getCompletedTasks();
            taskAdapter.updateTasks(completedTasks); // Update RecyclerView with completed tasks
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

    private void exportTasksToFile() {
        // Fetch tasks that are not completed
        List<Task> incompleteTasks = taskDatabase.taskDao().getActiveTasksOrdered();

        // Prepare the file content
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("<html><head><title>Incomplete Tasks</title></head><body>");
        fileContent.append("<h1>Incomplete Tasks</h1>");
        for (Task task : incompleteTasks) {
            fileContent.append("<div style='margin-bottom:20px;'>")
                    .append("<strong>Name:</strong> ").append(task.getShortName()).append("<br>")
                    .append("<strong>ID:</strong> ").append(task.getUid()).append("<br>")
                    .append("<strong>Description:</strong> ").append(task.getDescription()).append("<br>")
                    .append("<strong>Status:</strong> ").append(task.getStatus()).append("<br>")
                    .append("<strong>Start Time:</strong> ").append(task.getStartTime()).append("<br>")
                    .append("<strong>Duration:</strong> ").append(task.getDuration()).append("<br>")
                    .append("<strong>Location:</strong> ").append(task.getLocation()).append("<br>")
                    .append("</div>");
        }
        fileContent.append("</body></html>");

        // Define the file location
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Tasks");
        if (!exportDir.exists()) {
            exportDir.mkdirs(); // Create the directory if it doesn't exist
        }
        File file = new File(exportDir, "incomplete_tasks.html");

        // Write to the file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(fileContent.toString());
            Toast.makeText(this, "Tasks exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export tasks: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load active and ordered tasks
        List<Task> activeTasks = taskDatabase.taskDao().getActiveTasksOrdered();
        Log.d("MainActivity", "Active Tasks loaded: " + activeTasks.size());

        // Update RecyclerView with the filtered and sorted list
        taskAdapter.updateTasks(activeTasks);
    }

}