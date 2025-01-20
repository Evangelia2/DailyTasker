package gr.hua.dit.android.dailytasker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

public class TaskContentProvider extends ContentProvider {

    private static final String AUTHORITY = "gr.hua.dit.android.dailytasker.provider";
    private static final String PATH_TASKS = "tasks";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_TASKS);

    private static final int TASKS = 1;
    private static final int TASK_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_TASKS, TASKS);
        uriMatcher.addURI(AUTHORITY, PATH_TASKS + "/#", TASK_ID);
    }

    private TaskDatabase taskDatabase;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        taskDatabase = Room.databaseBuilder(context, TaskDatabase.class, "task-database").build();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case TASKS:
                cursor = taskDatabase.taskDao().getAllTasksCursor();
                break;
            case TASK_ID:
                long id = ContentUris.parseId(uri);
                cursor = taskDatabase.taskDao().getTaskByIdCursor((int) id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = uriMatcher.match(uri);
        if (match != TASKS) {
            throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

        Task task = Task.fromContentValues(values);
        long id = taskDatabase.taskDao().insertTaskAndReturnId(task);

        if (id == -1) {
            return null;
        }

        Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case TASK_ID:
                int id = (int) ContentUris.parseId(uri);
                Task task = Task.fromContentValues(values);
                task.setUid(id);
                int rowsUpdated = taskDatabase.taskDao().updateTaskAndReturnRows(task);
                if (rowsUpdated > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case TASK_ID:
                int id = (int) ContentUris.parseId(uri);
                int rowsDeleted = taskDatabase.taskDao().deleteTaskById(id);
                if (rowsDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion not supported for " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case TASKS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + PATH_TASKS;
            case TASK_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + PATH_TASKS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
