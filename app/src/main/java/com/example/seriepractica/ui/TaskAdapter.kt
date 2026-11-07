package com.example.seriepractica.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.seriepractica.R
import com.example.seriepractica.data.Task
import com.example.seriepractica.databinding.ItemTaskBinding

class TaskAdapter(private val onOpen: (Task) -> Unit) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TaskViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) = with(binding) {
            taskTitle.text = task.title
            taskDescription.text = task.description
            taskCheck.isChecked = task.completed
            taskCheck.contentDescription = root.context.getString(R.string.complete_task_accessibility, task.title)
            taskTitle.paintFlags = if (task.completed) {
                taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            taskStatus.setText(if (task.completed) R.string.task_completed else R.string.task_pending)
            taskStatus.setBackgroundResource(if (task.completed) R.drawable.bg_completed else R.drawable.bg_pending)
            taskStatus.setTextColor(ContextCompat.getColor(root.context,
                if (task.completed) R.color.success else R.color.pending))
            root.setOnClickListener { onOpen(task) }
        }
    }

    private object TaskDiff : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
