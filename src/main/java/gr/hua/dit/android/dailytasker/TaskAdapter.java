package gr.hua.dit.android.dailytasker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        // Set task details
        holder.shortNameTextView.setText(task.getShortName());
        holder.taskIdTextView.setText("ID: " + task.getUid());
        holder.statusTextView.setText(task.getStatus());

        // Define a list of colors
        int[] colors = {
                R.color.task_red,
                R.color.task_blue,
                R.color.task_yellow,
                R.color.task_green,
                R.color.task_purple,
                R.color.task_orange,
                R.color.task_cyan
        };

        // Generate a random color
        int randomColor = colors[new Random().nextInt(colors.length)];

        // Set the random color as the background of the CardView
        holder.taskCard.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), randomColor)
        );


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks; // Assign new list to the adapter
        notifyDataSetChanged(); // Force RecyclerView to refresh all items

    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView shortNameTextView;
        TextView taskIdTextView;
        TextView statusTextView;
        CardView taskCard;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            shortNameTextView = itemView.findViewById(R.id.textViewShortName);
            taskIdTextView = itemView.findViewById(R.id.textViewTaskId);
            statusTextView = itemView.findViewById(R.id.textViewStatus);
            taskCard = itemView.findViewById(R.id.taskCard);

        }
    }

}
