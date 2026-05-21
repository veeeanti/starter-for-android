package xyz.vee-anti.discordbotrelay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.vee-anti.discordbotrelay.data.models.Message
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isBot) Arrangement.Start else Arrangement.End
    ) {
        if (message.isBot) {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = message.sender,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF5865F2))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
                Text(
                    text = timeFormat.format(message.timestamp),
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        } else {
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(
                    text = message.sender,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF202225))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
                Text(
                    text = timeFormat.format(message.timestamp),
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
