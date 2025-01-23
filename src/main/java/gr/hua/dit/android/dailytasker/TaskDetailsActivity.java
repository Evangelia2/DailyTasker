package gr.hua.dit.android.dailytasker;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity {

    private TaskDatabase taskDatabase;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Button viewOnMapButton = findViewById(R.id.btnViewOnMap);

        // Retrieve the Task object (assumes it's passed via Intent)
        currentTask = (Task) getIntent().getSerializableExtra("task_key");

        // Get references to the TextViews and Button
        TextView taskIdTextView = findViewById(R.id.taskId);
        TextView taskNameTextView = findViewById(R.id.taskName);
        TextView taskDescriptionTextView = findViewById(R.id.taskDescription);
        TextView taskDurationTextView = findViewById(R.id.taskDuration);
        TextView taskStartTimeTextView = findViewById(R.id.taskStartTime);
        TextView taskLocationTextView = findViewById(R.id.taskLocation);
        TextView taskStatusTextView = findViewById(R.id.taskStatus);
        Button completeTaskButton = findViewById(R.id.btnCompleteTask);

        // Get task data from Intent
        Intent intent = getIntent();
        String taskId = intent.getStringExtra("task_id");
        Log.d("TaskDetailsActivity", "Retrieved Task ID: " + taskId);
        String taskName = intent.getStringExtra("task_name");
        String taskDescription = intent.getStringExtra("task_description");
        String taskDuration = intent.getStringExtra("task_duration");
        Log.d("TaskDetailsActivity", "Task Duration from Intent: " + taskDuration);

        String taskStartTime = intent.getStringExtra("task_start_time");
        String taskLocation = intent.getStringExtra("task_location");
        String taskStatus = intent.getStringExtra("task_status");

        Log.d("TaskDetailsActivity", "Task ID: " + taskId);
        Log.d("TaskDetailsActivity", "Task Name: " + taskName);
        Log.d("TaskDetailsActivity", "Task Description: " + taskDescription);
        Log.d("TaskDetailsActivity", "Task Duration: " + taskDuration);
        Log.d("TaskDetailsActivity", "Task Start Time: " + taskStartTime);
        Log.d("TaskDetailsActivity", "Task Location: " + taskLocation);
        Log.d("TaskDetailsActivity", "Task Status: " + taskStatus);


        // Set default text if data is null
        taskIdTextView.setText("Task ID: " + (taskId != null ? taskId : "N/A"));
        taskNameTextView.setText("Task Name: " + (taskName != null ? taskName : "N/A"));
        taskDescriptionTextView.setText("Description: " + (taskDescription != null ? taskDescription : "N/A"));
        taskDurationTextView.setText("Duration: " + (taskDuration != null ? taskDuration : "N/A"));
        taskStartTimeTextView.setText("Start Time: " + (taskStartTime != null ? taskStartTime : "N/A"));
        taskLocationTextView.setText("Location: " + (taskLocation != null ? taskLocation : "N/A"));

        taskStatusTextView.setText("Status: " + (taskStatus != null ? taskStatus : "N/A"));

        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d("TaskDetailsActivity", "Google Maps app is installed.");
        } else {
            Log.e("TaskDetailsActivity", "Google Maps app is NOT installed.");
        }


        // Show the button if the location is not empty
        if (taskLocation != null && !taskLocation.isEmpty()) {
            viewOnMapButton.setVisibility(View.VISIBLE);
            viewOnMapButton.setOnClickListener(v -> viewOnGoogleMaps());
        }


        // Set up the button to mark the task as completed
        completeTaskButton.setOnClickListener(v -> {
            if (taskId != null) {
                updateTaskStatusInDatabase(taskId, "Completed");
                taskStatusTextView.setText("Status: Completed");
                Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task ID is missing", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("TaskDetailsActivity", "Location to be opened: " + taskLocation);

    }
    private void viewOnGoogleMaps() {
        try {
            // Get location from currentTask or Intent extras
            String location = currentTask != null ? currentTask.getLocation() : null;
            if (location == null) {
                location = getIntent().getStringExtra("task_location");
            }
            Log.d("TaskDetailsActivity", "Location to open: " + location);

            if (location != null && !location.isEmpty()) {
                // Construct the Google Maps URL for the search query
                String googleMapsUri = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(location);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUri));
                startActivity(mapIntent);

            } else {
                Toast.makeText(this, "No valid location provided.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error occurred while opening location.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }





    // Method to update task status in the database
    private void updateTaskStatusInDatabase(String taskId, String newStatus) {
        if (taskId == null || taskId.isEmpty()) {
            Log.e("TaskDetailsActivity", "Task ID is null or empty!");
            Toast.makeText(this, "Task ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskDatabase taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "task-database").allowMainThreadQueries().build();

        Task task = taskDatabase.taskDao().getTaskById(Integer.parseInt(taskId));
        if (task != null) {

            task.setStatus(newStatus);
            taskDatabase.taskDao().updateTask(task);
            Log.d("TaskDetailsActivity", "Task status updated to: " + newStatus);
        } else {
            Log.e("TaskDetailsActivity", "Task not found with ID: " + taskId);
            Toast.makeText(this, "Task not found!", Toast.LENGTH_SHORT).show();
        }
    }

}

