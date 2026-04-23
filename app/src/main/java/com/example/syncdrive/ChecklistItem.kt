package com.example.syncdrive

// Model: Represents a single hardware sensor or system check
data class ChecklistItem(
    val componentName: String,
    val status: String
)