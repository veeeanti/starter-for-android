package io.appwrite.starterkit.data.models

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isBot: Boolean = false
)
