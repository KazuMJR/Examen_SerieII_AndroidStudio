package com.example.seriepractica

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seriepractica.data.TaskRepository
import com.example.seriepractica.databinding.ActivityMainBinding
import com.example.seriepractica.ui.BaseActivity
import com.example.seriepractica.ui.TaskAdapter
import com.example.seriepractica.ui.TaskContract
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: TaskRepository
    private val adapter = TaskAdapter { task ->
        Toast.makeText(this, task.description, Toast.LENGTH_LONG).show()
    }
    private var filter = "ALL"
    private val addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data?.getStringExtra(TaskContract.EXTRA_TASK_ID) != null) {
            selectFilter("ALL")
            binding.taskList.scrollToPosition(0)
            Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(binding.root)
        repository = TaskRepository(this)
        filter = savedInstanceState?.getString("filter") ?: "ALL"
        binding.today.text = SimpleDateFormat("d MMM", Locale("es", "GT")).format(Date())
        binding.taskList.layoutManager = LinearLayoutManager(this)
        binding.taskList.adapter = adapter
        binding.addTask.setOnClickListener {
            addTaskLauncher.launch(Intent(this, AddTaskActivity::class.java))
        }
        binding.filterAll.setOnClickListener { selectFilter("ALL") }
        binding.filterPending.setOnClickListener { selectFilter("PENDING") }
        binding.filterCompleted.setOnClickListener { selectFilter("COMPLETED") }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("filter", filter)
        super.onSaveInstanceState(outState)
    }

    private fun selectFilter(value: String) {
        filter = value
        refresh()
    }

    private fun refresh() = with(binding) {
        val tasks = repository.all()
        val completed = tasks.count { it.completed }
        progressCount.text = getString(R.string.progress_count, completed, tasks.size)
        progressBar.max = tasks.size.coerceAtLeast(1)
        progressBar.progress = completed
        progressHint.text = when {
            tasks.isEmpty() -> getString(R.string.progress_empty)
            completed == tasks.size -> getString(R.string.progress_done)
            else -> getString(R.string.progress_pending, tasks.size - completed)
        }
        val visibleTasks = tasks.filter {
            when (filter) {
                "PENDING" -> !it.completed
                "COMPLETED" -> it.completed
                else -> true
            }
        }
        filterAll.isSelected = filter == "ALL"
        filterPending.isSelected = filter == "PENDING"
        filterCompleted.isSelected = filter == "COMPLETED"
        adapter.submitList(visibleTasks)
        emptyState.isVisible = visibleTasks.isEmpty()
        taskList.isVisible = visibleTasks.isNotEmpty()
        emptyTitle.setText(when (filter) {
            "PENDING" -> R.string.empty_pending
            "COMPLETED" -> R.string.empty_completed
            else -> R.string.empty_title
        })
        emptyDescription.setText(if (filter == "ALL") R.string.empty_description else R.string.empty_filter_description)
    }
}
