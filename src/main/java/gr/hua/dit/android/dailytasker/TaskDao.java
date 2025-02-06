package gr.hua.dit.android.dailytasker;

import android.database.Cursor;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertTask(Task task);

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("DELETE FROM tasks WHERE uid = :taskId")
    int deleteTaskById(int taskId); // Returns the number of rows affected

    @Update
    void updateTask(Task task); // Update method for tasks

    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    Task getTaskById(int taskId);

    @Query("SELECT * FROM tasks WHERE status != 'Completed' " +
            "ORDER BY " +
            "CASE WHEN status = 'expired' THEN 1 ELSE 0 END DESC, " + // Urgent (expired)
            "CASE WHEN status = 'In Progress' THEN 1 ELSE 0 END DESC, " +                // In Progress
            "datetime(startTime) ASC")
    List<Task> getActiveTasksOrdered();

    @Query("SELECT * FROM tasks WHERE status != 'Completed'")
    List<Task> getIncompleteTasks();

    @Query("SELECT * FROM tasks")
    Cursor getAllTasksCursor();

    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    Cursor getTaskByIdCursor(int taskId);

    @Insert
    long insertTaskAndReturnId(Task task);

    @Update
    int updateTaskAndReturnRows(Task task);

    @Query("SELECT * FROM tasks WHERE status = 'Completed'")
    List<Task> getCompletedTasks(); // Query for completed tasks

}