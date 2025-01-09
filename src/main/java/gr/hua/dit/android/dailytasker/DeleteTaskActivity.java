package gr.hua.dit.android.dailytasker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class DeleteTaskActivity extends AppCompatActivity {

    private TaskDatabase taskDatabase;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_task);

        // Initialize the database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "task-database").allowMainThreadQueries().build();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAllTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load tasks from database and set up the adapter
        List<Task> tasks = taskDatabase.taskDao().getAllTasks();
        taskAdapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(taskAdapter);

        // Handle delete task button click
        EditText editTextTaskId = findViewById(R.id.editTextTaskId);
        Button buttonDeleteTask = findViewById(R.id.buttonDeleteTask);

        buttonDeleteTask.setOnClickListener(v -> {
            String taskIdText = editTextTaskId.getText().toString().trim();

            if (taskIdText.isEmpty()) {
                Toast.makeText(this, "Please enter a Task ID.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int taskId = Integer.parseInt(taskIdText);
                int rowsAffected = taskDatabase.taskDao().deleteTaskById(taskId);

                Log.d("DeleteTaskActivity", "Task ID: " + taskId + ", Rows Affected: " + rowsAffected);

                if (rowsAffected > 0) {
                    Toast.makeText(this, "Task deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No task found with the provided ID.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid Task ID. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshTasks() {
        // Reload tasks from database and update RecyclerView
        List<Task> updatedTasks = taskDatabase.taskDao().getAllTasks();
        taskAdapter.updateTasks(updatedTasks);
    }
}
