package xyz.vee-anti.discordbotrelay.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import xyz.vee-anti.discordbotrelay.data.repository.DiscordRepository
import xyz.vee-anti.discordbotrelay.data.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DiscordRepository.getInstance(application)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchMessages()
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        _isSending.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.sendMessage(content)
            result.onSuccess {
                _messages.value = _messages.value + it
            }.onFailure {
                _error.value = it.message
            }
            _isSending.value = false
        }
    }

    fun fetchMessages() {
        viewModelScope.launch {
            val result = repository.fetchMessages()
            result.onSuccess {
                _messages.value = it
            }.onFailure {
                _error.value = it.message
            }
        }
    }
}
