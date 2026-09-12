
package echo.music.iad1tya.notune

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.ui.theme.NothingFont

import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.playback.queues.ListQueue
import echo.music.iad1tya.utils.toMediaItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskNoTuneScreen(
    navController: NavController,
    viewModel: AskNoTuneViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    var textFieldValue by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val playerConnection = LocalPlayerConnection.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AskNoTuneEvent.PlaySongs -> {
                    playerConnection?.playQueue(ListQueue("NØTUNE AI", event.songs.map { it.toMediaItem() }, 0))
                }
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ASK NØTUNE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = NothingFont,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.arrow_back),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
                
                if (isTyping) {
                    item {
                        Text(
                            "NØTUNE IS THINKING...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = NothingFont,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        cursorBrush = SolidColor(Color.White),
                        decorationBox = { innerTextField ->
                            if (textFieldValue.isEmpty()) {
                                Text(
                                    "Describe a mood, song, or scene...",
                                    style = TextStyle(color = Color.White.copy(alpha = 0.3f), fontSize = 16.sp)
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    IconButton(
                        onClick = {
                            viewModel.sendMessage(textFieldValue)
                            textFieldValue = ""
                        },
                        enabled = textFieldValue.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (textFieldValue.isNotBlank()) Color(0xFFFF0031) else Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = if (isUser) "YOU" else "NØTUNE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = NothingFont,
                color = if (isUser) Color.White.copy(alpha = 0.5f) else Color(0xFFFF0031)
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Box(
            modifier = Modifier
                .background(
                    if (isUser) Color.White.copy(alpha = 0.1f) else Color.Transparent,
                    RoundedCornerShape(4.dp)
                )
                .then(if (!isUser) Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp)) else Modifier)
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
