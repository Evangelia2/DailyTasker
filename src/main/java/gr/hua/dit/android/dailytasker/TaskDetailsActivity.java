package gr.hua.dit.android.dailytasker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class TaskDetailsActivity extends AppCompatActivity {

    private TaskDatabase taskDatabase;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        TextView taskName = findViewById(R.id.taskName);
        TextView taskStatus = findViewById(R.id.taskStatus);
        Button btnCompleteTask = findViewById(R.id.btnCompleteTask);

        // Retrieve task details from the Intent
        int taskId = getIntent().getIntExtra("task_id", -1);
        String taskNameStr = getIntent().getStringExtra("task_name");
        String taskStatusStr = getIntent().getStringExtra("task_status");

        if (taskId == -1 || taskNameStr == null || taskStatusStr == null) {
            Toast.makeText(this, "Error: Task data missing!", Toast.LENGTH_SHORT).show();
            finish(); // Exit the activity if data is invalid
        }

        // Initialize database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "task-database").allowMainThreadQueries().build();

        // Display the task details
        taskName.setText(taskNameStr);
        taskStatus.setText("Status: " + taskStatusStr);

        // Mark the task as completed
        btnCompleteTask.setOnClickListener(v -> {
            Task task = taskDatabase.taskDao().getTaskById(taskId);
            if (task != null) {
                task.setStatus("Completed");
                taskDatabase.taskDao().updateTask(task);
                Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();
                finish(); // Return to the previous activity
            }if (task == null) {
                Toast.makeText(this, "Task not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
