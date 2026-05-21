package xyz.veeanti.discordbotrelay.data.repository

import android.content.Context
import io.appwrite.Client
import io.appwrite.enums.ExecutionMethod
import io.appwrite.services.Functions
import xyz.veeanti.discordbotrelay.constants.DiscordConfig
import xyz.veeanti.discordbotrelay.data.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class SendMessageRequest(
    val content: String,
    val username: String = "Android App"
)

@Serializable
data class SendMessageResponse(
    val success: Boolean,
    val message_id: String? = null,
    val content: String? = null
)

@Serializable
data class GetMessagesResponse(
    val messages: List<AppwriteMessage> = emptyList(),
    val total: Int = 0
)

@Serializable
data class AppwriteMessage(
    val content: String = "",
    val sender: String = "Unknown",
    val timestamp: String = "",
    val isBot: Boolean = false
)

class DiscordRepository private constructor(context: Context) {

    private val client = Client(context)
        .setEndpoint(DiscordConfig.APPWRITE_ENDPOINT)
        .setProject(DiscordConfig.APPWRITE_PROJECT_ID)

    private val functions = Functions(client)

    suspend fun sendMessage(content: String): Result<Message> {
        if (DiscordConfig.APPWRITE_FUNCTION_ID.isBlank()) {
            return Result.failure(Exception("Appwrite Function ID not configured"))
        }

        return try {
            val request = SendMessageRequest(content = content)
            val json = Json.encodeToString(SendMessageRequest.serializer(), request)

            val response = withContext(Dispatchers.IO) {
                functions.createExecution(
                    functionId = DiscordConfig.APPWRITE_FUNCTION_ID,
                    body = json,
                    method = ExecutionMethod.POST
                )
            }

            val body = response.responseBody?.toString() ?: "{}"
            val result = Json.decodeFromString(SendMessageResponse.serializer(), body)

            if (result.success == true) {
                Result.success(
                    Message(
                        id = result.message_id ?: UUID.randomUUID().toString(),
                        content = content,
                        sender = "You",
                        isBot = false
                    )
                )
            } else {
                Result.failure(Exception("Function returned error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMessages(): Result<List<Message>> {
        if (DiscordConfig.APPWRITE_FUNCTION_ID.isBlank()) {
            return Result.success(emptyList())
        }

        return try {
            val response = withContext(Dispatchers.IO) {
                functions.createExecution(
                    functionId = DiscordConfig.APPWRITE_FUNCTION_ID,
                    body = "",
                    method = ExecutionMethod.GET
                )
            }

            val body = response.responseBody?.toString() ?: "{}"
            val result = Json.decodeFromString(GetMessagesResponse.serializer(), body)

            val messages = result.messages.map { msg ->
                Message(
                    id = UUID.randomUUID().toString(),
                    content = msg.content,
                    sender = msg.sender,
                    timestamp = msg.timestamp.toLongOrNull() ?: System.currentTimeMillis(),
                    isBot = msg.isBot
                )
            }

            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
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