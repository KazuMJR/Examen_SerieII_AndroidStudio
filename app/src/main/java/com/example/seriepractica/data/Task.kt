package com.example.seriepractica.data

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: Long,
    val completed: Boolean = false
) {
    companion object {
        const val MAX_TITLE_LENGTH = 80
        const val MAX_DESCRIPTION_LENGTH = 500
    }
}
