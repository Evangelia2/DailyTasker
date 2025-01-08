package gr.hua.dit.android.dailytasker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class CreateTaskActivity extends AppCompatActivity {

    private TaskDatabase taskDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Initialize the database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "task-database").allowMainThreadQueries().build();

        EditText shortNameInput = findViewById(R.id.editTextShortName);
        EditText descriptionInput = findViewById(R.id.editTextDescription);
        EditText startTimeInput = findViewById(R.id.editTextStartTime);
        EditText durationInput = findViewById(R.id.editTextDuration);
        EditText locationInput = findViewById(R.id.editTextLocation);
        Button saveButton = findViewById(R.id.buttonSaveTask);

        saveButton.setOnClickListener(v -> {
            String shortName = shortNameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String startTime = startTimeInput.getText().toString().trim();
            String durationText = durationInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();

            if (shortName.isEmpty() || startTime.isEmpty() || durationText.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(durationText);
                if (duration <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Duration must be a positive number!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new task
            Task task = new Task();
            task.setShortName(shortName);
            task.setDescription(description);
            task.setStartTime(startTime);
            task.setDuration(duration);
            task.setStatus("recorded");
            task.setLocation(location);

            // Insert into database
            taskDatabase.taskDao().insertTask(task);

            Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
            Log.d("CreateTaskActivity", "Task saved: " + task.toString());

            finish(); // Close the activity
        });
    }
}
