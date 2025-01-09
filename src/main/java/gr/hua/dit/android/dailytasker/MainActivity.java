package gr.hua.dit.android.dailytasker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TaskDatabase taskDatabase;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "task-database").allowMainThreadQueries().build();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load tasks from database
        List<Task> tasks = taskDatabase.taskDao().getAllTasks();

        // Initialize the adapter and set it to the RecyclerView
        taskAdapter = new TaskAdapter(tasks);
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
            taskAdapter = new TaskAdapter(tasks);
            RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
            recyclerView.setAdapter(taskAdapter);
        }
    }

}
