package com.driverapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.driverapp.R;
import com.driverapp.models.TaskModel;

import java.util.List;

/**
 * Created by bridgelabz on 20/07/18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskModel> taskList;
    private TaskAdapter.Listner listner;
    public TaskAdapter(List<TaskModel> taskList,TaskAdapter.Listner listner){
        this.taskList = taskList;
        this.listner = listner;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate,tvStatus,tvDropAddress;
        public TaskViewHolder(View view){
            super(view);
            tvDate = (TextView) view.findViewById(R.id.card_task_tv_date);
            tvStatus = (TextView) view.findViewById(R.id.card_task_tv_status);
            tvDropAddress = (TextView) view.findViewById(R.id.card_task_tv_drop_address);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onTaskClicked(taskList.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) (parent.getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.card_task,parent,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
            holder.tvDropAddress.setText(taskList.get(position).drop.address);
            holder.tvDate.setText(taskList.get(position).time);
            holder.tvStatus.setText(taskList.get(position).status.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public interface Listner{
        public void onTaskClicked(TaskModel task);
    }
}
