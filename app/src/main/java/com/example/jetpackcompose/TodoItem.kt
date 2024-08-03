package com.example.jetpackcompose

data class TodoItem(
    val id: Int,
    val title: String,
    val description: String,
    var isDone: Boolean = false
)
