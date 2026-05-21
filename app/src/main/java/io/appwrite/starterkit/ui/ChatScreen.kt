package io.appwrite.starterkit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.appwrite.starterkit.data.models.Message
import io.appwrite.starterkit.ui.components.ChatBubble
import io.appwrite.starterkit.ui.components.ChatHeader
import io.appwrite.starterkit.ui.components.ChatInput
import io.appwrite.starterkit.ui.components.EmptyChat
import io.appwrite.starterkit.viewmodels.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ChatHeader(
                    messageCount = messages.size,
                    onInfoClick = { }
                )

                if (messages.isEmpty()) {
                    EmptyChat(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(messages) { message: Message ->
                            ChatBubble(message = message)
                        }
                    }
                }

                ChatInput(
                    onSend = { content ->
                        viewModel.sendMessage(content)
                    },
                    isSending = isSending
                )
            }

            if (error != null) {
                snackbarHostState.currentSnackbarData?.let {
                    androidx.compose.runtime.Composable {
                        androidx.compose.material3.SnackbarHost(snackbarHostState)
                    }
                }
            }
        }
    }
}
