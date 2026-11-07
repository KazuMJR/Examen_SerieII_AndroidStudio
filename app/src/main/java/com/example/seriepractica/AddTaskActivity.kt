package com.example.seriepractica

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import com.example.seriepractica.data.TaskRepository
import com.example.seriepractica.databinding.ActivityAddTaskBinding
import com.example.seriepractica.ui.BaseActivity
import com.example.seriepractica.ui.TaskContract

class AddTaskActivity : BaseActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private var saving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(binding.root)
        binding.descriptionCounter.text = getString(R.string.description_counter, 0)
        binding.descriptionInput.doAfterTextChanged {
            binding.descriptionCounter.text = getString(R.string.description_counter, it?.length ?: 0)
        }
        binding.saveTask.setOnClickListener { save() }
        binding.backButton.setOnClickListener { confirmExit() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = confirmExit()
        })
    }

    private fun save() = with(binding) {
        if (saving) return@with
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        titleInput.error = if (title.isEmpty()) getString(R.string.title_required) else null
        descriptionInput.error = if (description.isEmpty()) getString(R.string.description_required) else null
        when {
            title.isEmpty() -> titleInput.requestFocus()
            description.isEmpty() -> descriptionInput.requestFocus()
            else -> {
                saving = true
                saveTask.isEnabled = false
                val task = TaskRepository(this@AddTaskActivity).add(title, description)
                setResult(RESULT_OK, Intent().putExtra(TaskContract.EXTRA_TASK_ID, task.id))
                finish()
            }
        }
    }

    private fun confirmExit() {
        if (binding.titleInput.text.isBlank() && binding.descriptionInput.text.isBlank()) {
            finish()
            return
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.discard_title)
            .setMessage(R.string.discard_message)
            .setNegativeButton(R.string.continue_editing, null)
            .setPositiveButton(R.string.discard) { _, _ -> finish() }
            .show()
    }
}
