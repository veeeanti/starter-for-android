package io.appwrite.starterkit.data.repository

import android.content.Context
import io.appwrite.starterkit.constants.DiscordConfig
import io.appwrite.starterkit.data.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID


class DiscordRepository private constructor(context: Context) {

    private val client = OkHttpClient()

    suspend fun sendMessage(content: String): Result<Message> {
        val webhookUrl = DiscordConfig.DISCORD_WEBHOOK_URL
        if (webhookUrl.isBlank()) {
            return Result.failure(Exception("DISCORD_WEBHOOK_URL not configured"))
        }

        return try {
            val json = JSONObject().apply {
                put("content", content)
            }
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                Result.success(
                    Message(
                        id = UUID.randomUUID().toString(),
                        content = content,
                        sender = "You",
                        isBot = false
                    )
                )
            } else {
                Result.failure(Exception("Discord responded: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMessages(): Result<List<Message>> {
        val webhookUrl = DiscordConfig.DISCORD_WEBHOOK_URL
        if (webhookUrl.isBlank()) {
            return Result.success(emptyList())
        }

        return try {
            val request = Request.Builder()
                .url(webhookUrl)
                .get()
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                Result.success(parseMessages(body))
            } else {
                Result.failure(Exception("Failed to fetch messages: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseMessages(body: String): List<Message> {
        val messages = mutableListOf<Message>()
        try {
            val jsonArray = org.json.JSONArray(body)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val content = obj.optString("content", "")
                val username = obj.optString("username", "Bot")
                val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                messages.add(
                    Message(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        content = content,
                        sender = username,
                        timestamp = timestamp,
                        isBot = true
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return messages
    }

    companion object {
        @Volatile
        private var INSTANCE: DiscordRepository? = null

        fun getInstance(context: Context): DiscordRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DiscordRepository(context).also { INSTANCE = it }
            }
        }
    }
}
