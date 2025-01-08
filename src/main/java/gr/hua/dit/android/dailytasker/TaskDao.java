package gr.hua.dit.android.dailytasker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insertTask(Task task);

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("DELETE FROM tasks WHERE uid = :taskId")
    int deleteTaskById(int taskId); // Returns the number of rows affected
}
