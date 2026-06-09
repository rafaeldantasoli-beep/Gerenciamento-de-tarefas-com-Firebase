package com.example.gerenciadordetarefascomfirebase.data.model

data class Task(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val createdAt: Long = 0L
)
