package gr.hua.dit.android.testclient;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gr.hua.dit.android.dailytasker.R;

public class MainActivity extends AppCompatActivity {
    private static final String PROVIDER_URI = "content://gr.hua.dit.android.dailytasker.provider/tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_provider);


        Button addButton = findViewById(R.id.btnAddTestData);
        Button readButton = findViewById(R.id.btnReadUpcoming);
        Button updateButton = findViewById(R.id.btnUpdateTestData);
        Button deleteButton = findViewById(R.id.btnDeleteUpcoming);

        addButton.setOnClickListener(v -> addTestData());
        readButton.setOnClickListener(v -> readUpcomingTask());
        updateButton.setOnClickListener(v -> updateTestData());
        deleteButton.setOnClickListener(v -> deleteUpcomingTask());
    }

    /*private void addTestData() {
        ContentValues values = new ContentValues();
        values.put("task_name", "Test Data");
        values.put("task_description", "This is a test task");
        values.put("task_duration", 1);
        values.put("task_start_time", "12:00");
        values.put("task_location", "Athens");
        values.put("task_status", "recorded");

        Uri uri = getContentResolver().insert(Uri.parse(PROVIDER_URI), values);
        if (uri != null) {
            long newTaskId = ContentUris.parseId(uri);
            Toast.makeText(this, "Task Added with ID: " + newTaskId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void addTestData() {
        ContentValues values = new ContentValues();
        values.put("shortName", "Test Data");
        values.put("description", "This is a test task");
        values.put("startTime", "12:00");
        values.put("duration", 3);
        values.put("location", "Athens");
        values.put("status", "recorded");

        Uri uri = getContentResolver().insert(Uri.parse(PROVIDER_URI), values);
        if (uri != null) {
            long newTaskId = ContentUris.parseId(uri);
            Log.d("TestClient", "Task Added with ID: " + newTaskId);
            Toast.makeText(this, "Task Added with ID: " + newTaskId, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("TestClient", "Failed to add task.");
            Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
        }
    }


    private void readUpcomingTask() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (Cursor cursor = getContentResolver().query(Uri.parse(PROVIDER_URI), null, "status = ?", new String[]{"recorded"}, "startTime ASC")) {
                if (cursor != null) {
                    // Log available column names to check for errors
                    Log.d("TestClient", "Database Columns: " + Arrays.toString(cursor.getColumnNames()));

                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex("shortName");
                        int timeIndex = cursor.getColumnIndex("startTime");

                        // Validate indexes before accessing them
                        if (nameIndex != -1 && timeIndex != -1) {
                            String taskName = cursor.getString(nameIndex);
                            String startTime = cursor.getString(timeIndex);

                            runOnUiThread(() -> Toast.makeText(this, "Upcoming Task: " + taskName + " at " + startTime, Toast.LENGTH_LONG).show());
                        } else {
                            runOnUiThread(() -> Toast.makeText(this, "Column names not found!", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "No upcoming tasks found", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }



    /*private void debugDatabaseColumns() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (Cursor cursor = getContentResolver().query(Uri.parse(PROVIDER_URI), null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    Log.d("TestClient", "Database Columns: " + Arrays.toString(cursor.getColumnNames()));
                } else {
                    Log.d("TestClient", "No data found in database.");
                }
            }
        });
    }*/

    private void updateTestData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int taskId = -1;
            try (Cursor cursor = getContentResolver().query(Uri.parse(PROVIDER_URI), null, "shortName = ?", new String[]{"Test Data"}, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex("uid");  // Ensure correct column name
                    if (idIndex != -1) {
                        taskId = cursor.getInt(idIndex);
                        Log.d("TestClient", "Updating Task ID: " + taskId);
                    }
                } else {
                    Log.d("TestClient", "No 'Test Data' task found.");
                }
            }

            if (taskId != -1) {
                ContentValues values = new ContentValues();
                values.put("shortName", "Test Data Updated");

                int rowsUpdated = getContentResolver().update(Uri.parse(PROVIDER_URI + "/" + taskId), values, null, null);
                Log.d("TestClient", "Rows Updated: " + rowsUpdated);

                runOnUiThread(() -> {
                    if (rowsUpdated > 0) {
                        Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No task found to update", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "No 'Test Data' task found", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void deleteUpcomingTask() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int taskId = -1;
            try (Cursor cursor = getContentResolver().query(Uri.parse(PROVIDER_URI), null, "status = ?", new String[]{"recorded"}, "startTime ASC")) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex("uid");  // Ensure correct column name
                    if (idIndex != -1) {
                        taskId = cursor.getInt(idIndex);
                        Log.d("TestClient", "Deleting Task ID: " + taskId);
                    }
                } else {
                    Log.d("TestClient", "No upcoming tasks found.");
                }
            }

            if (taskId != -1) {
                int rowsDeleted = getContentResolver().delete(Uri.parse(PROVIDER_URI + "/" + taskId), null, null);
                Log.d("TestClient", "Rows Deleted: " + rowsDeleted);

                runOnUiThread(() -> {
                    if (rowsDeleted > 0) {
                        Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "No upcoming task found to delete", Toast.LENGTH_SHORT).show());
            }
        });
    }




}
