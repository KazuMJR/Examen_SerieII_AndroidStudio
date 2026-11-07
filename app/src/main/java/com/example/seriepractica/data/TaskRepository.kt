package com.example.seriepractica.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Small offline task store. All screens read the same private preferences file. */
class TaskRepository(private val preferences: SharedPreferences) {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    )

    fun all(): List<Task> {
        val array = JSONArray(preferences.getString(TASKS_KEY, "[]"))
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Task(
                id = item.getString("id"),
                title = item.getString("title"),
                description = item.getString("description"),
                createdAt = item.getLong("createdAt"),
                completed = item.optBoolean("completed", false)
            )
        }.sortedByDescending(Task::createdAt)
    }

    fun find(id: String): Task? = all().find { it.id == id }

    fun add(title: String, description: String): Task {
        val cleanTitle = title.trim()
        val cleanDescription = description.trim()
        require(cleanTitle.isNotEmpty() && cleanTitle.length <= Task.MAX_TITLE_LENGTH)
        require(cleanDescription.isNotEmpty() && cleanDescription.length <= Task.MAX_DESCRIPTION_LENGTH)
        val task = Task(UUID.randomUUID().toString(), cleanTitle, cleanDescription, System.currentTimeMillis())
        save(all() + task)
        return task
    }

    fun setCompleted(id: String, completed: Boolean) {
        save(all().map { if (it.id == id) it.copy(completed = completed) else it })
    }

    fun delete(id: String) {
        save(all().filterNot { it.id == id })
    }

    private fun save(tasks: List<Task>) {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("createdAt", task.createdAt)
                put("completed", task.completed)
            })
        }
        // apply updates memory immediately and persists asynchronously to disk.
        preferences.edit { putString(TASKS_KEY, array.toString()) }
    }

    companion object {
        const val PREFERENCES_NAME = "com.example.seriepractica.tasks"
        private const val TASKS_KEY = "tasks_v1"
    }
}
