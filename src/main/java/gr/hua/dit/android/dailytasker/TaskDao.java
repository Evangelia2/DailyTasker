package gr.hua.dit.android.dailytasker;

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

}