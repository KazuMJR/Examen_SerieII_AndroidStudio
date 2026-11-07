package com.example.seriepractica.ui

import android.app.AlertDialog
import android.content.Context
import com.example.seriepractica.R
import com.example.seriepractica.data.Task

fun confirmTaskDeletion(context: Context, task: Task, onConfirm: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle(R.string.delete_title)
        .setMessage(context.getString(R.string.delete_message, task.title))
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.delete) { _, _ -> onConfirm() }
        .show()
}
