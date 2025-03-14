package com.tanh.scribblegame.presentation.match.item

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.tanh.scribblegame.domain.model.Chat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessageItem(
    userId: String,
    message: Chat,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxSize()

    ) {
        if(message.userId == userId) {
            Text(
                text = message.content,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                text = message.content,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }
        Text(message.time.formatToString())
    }
}

fun LocalDateTime.formatToString(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM HH:ss")
        val formattedDate = this.format(formatter)
        return formattedDate
    } else {
        return ""
    }
}