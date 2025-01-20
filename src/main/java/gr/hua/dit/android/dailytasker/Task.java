package gr.hua.dit.android.dailytasker;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "shortName")
    private String shortName;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "startTime")
    private String startTime; // Use a string for simplicity, format: HH:mm
    @ColumnInfo(name = "duration")
    private int duration;// Duration in hours
    @ColumnInfo(name = "status")
    private String status; // "recorded"
    @ColumnInfo(name = "location")
    private String location; // Optional, can be empty

    // Getters and Setters
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    // Update status based on the current time
    public void updateStatus(String currentTime) {
        if (status.equals("expired")) return;

        if (currentTime.compareTo(startTime) > 0) {
            if (currentTime.compareTo(calculateEndTime()) > 0) {
                status = "expired";
            } else {
                status = "in-progress";
            }
        }
    }

    private String calculateEndTime() {
        // Assuming startTime is in HH:mm format
        String[] timeParts = startTime.split(":");
        int hours = Integer.parseInt(timeParts[0]) + duration;
        int minutes = Integer.parseInt(timeParts[1]);
        if (hours >= 24) {
            hours -= 24; // Adjust for 24-hour time format
        }
        return String.format("%02d:%02d", hours, minutes);
    }

    // Implement the fromContentValues method
    public static Task fromContentValues(ContentValues values) {
        Task task = new Task();
        if (values.containsKey("shortName")) {
            task.setShortName(values.getAsString("shortName"));
        }
        if (values.containsKey("description")) {
            task.setDescription(values.getAsString("description"));
        }
        if (values.containsKey("startTime")) {
            task.setStartTime(values.getAsString("startTime"));
        }
        if (values.containsKey("duration")) {
            task.setDuration(values.getAsInteger("duration"));
        }
        if (values.containsKey("status")) {
            task.setStatus(values.getAsString("status"));
        }
        if (values.containsKey("location")) {
            task.setLocation(values.getAsString("location"));
        }
        return task;
    }

}
