package gr.hua.dit.android.dailytasker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        // Ensure the start time is in __:__ format (e.g., 12:30)
        startTimeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                String input = charSequence.toString();

                // Automatically add the colon after the first two digits
                if (input.length() == 2 && !input.contains(":")) {
                    startTimeInput.setText(input + ":");
                    startTimeInput.setSelection(startTimeInput.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


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
                if (duration <= 0 || duration > 24) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Duration must be a positive number and less than or equal to 24 hours!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate the start time format __:__ and check if the hours and minutes are valid
            if (!startTime.matches("\\d{2}:\\d{2}")) {
                Toast.makeText(this, "Start time must be in the format HH:mm!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Split the time into hours and minutes
            String[] timeParts = startTime.split(":");
            int hours, minutes;
            try {
                hours = Integer.parseInt(timeParts[0]);
                minutes = Integer.parseInt(timeParts[1]);

                // Validate hours and minutes
                if (hours < 0 || hours > 23) {
                    Toast.makeText(this, "Hours must be between 00 and 23!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (minutes < 0 || minutes > 59) {
                    Toast.makeText(this, "Minutes must be between 00 and 59!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid time format. Please ensure hours and minutes are valid.", Toast.LENGTH_SHORT).show();
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