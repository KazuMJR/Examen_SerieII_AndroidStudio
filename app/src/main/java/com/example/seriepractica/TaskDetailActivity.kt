package com.example.seriepractica

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.seriepractica.data.TaskRepository
import com.example.seriepractica.databinding.ActivityTaskDetailBinding
import com.example.seriepractica.ui.BaseActivity
import com.example.seriepractica.ui.TaskContract
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class TaskDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var repository: TaskRepository
    private var taskId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(binding.root)
        repository = TaskRepository(this)
        taskId = intent.getStringExtra(TaskContract.EXTRA_TASK_ID).orEmpty()
        binding.backButton.setOnClickListener { finish() }
        binding.returnToList.setOnClickListener { finish() }
        binding.completeTask.setOnClickListener {
            repository.find(taskId)?.let { repository.setCompleted(it.id, !it.completed) }
            renderTask()
        }
    }

    override fun onResume() {
        super.onResume()
        renderTask()
    }

    private fun renderTask() = with(binding) {
        val task = repository.find(taskId)
        if (task == null) {
            Toast.makeText(this@TaskDetailActivity, R.string.task_missing, Toast.LENGTH_SHORT).show()
            finish()
            return@with
        }
        taskTitle.text = task.title
        completeTask.setText(if (task.completed) R.string.reopen_task else R.string.complete_task)
        taskDescription.text = task.description
        taskDate.text = DateFormat.getDateTimeInstance(
            DateFormat.LONG, DateFormat.SHORT, Locale("es", "GT")
        ).format(Date(task.createdAt))
        taskStatus.setText(if (task.completed) R.string.task_completed else R.string.task_pending)
        taskStatus.setBackgroundResource(if (task.completed) R.drawable.bg_completed else R.drawable.bg_pending)
        taskStatus.setTextColor(ContextCompat.getColor(this@TaskDetailActivity,
            if (task.completed) R.color.success else R.color.pending))
    }
}
