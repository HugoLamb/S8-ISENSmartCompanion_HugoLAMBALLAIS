package fr.isen.hugo.isensmartcompanion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.hugo.isensmartcompanion.R
import fr.isen.hugo.isensmartcompanion.database.ChatDatabase
import fr.isen.hugo.isensmartcompanion.database.ChatEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val database = ChatDatabase.getDatabase(context)
    val chatDao = database.chatDao()
    val coroutineScope = rememberCoroutineScope()
    var chats by remember { mutableStateOf<List<ChatEntities>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val loadedChats = chatDao.getAllChats()
            withContext(Dispatchers.Main) {
                chats = loadedChats
            }
        }
    }

    fun refreshHistory() {
        coroutineScope.launch(Dispatchers.IO) {
            val newChats = chatDao.getAllChats()
            withContext(Dispatchers.Main) {
                chats = newChats
            }
        }
    }

    Scaffold { innerPaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chat History",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        chatDao.clearChats()
                        refreshHistory()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Clear History", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chats) { chat ->
                    HistoryItem(chatEntities = chat, onDelete = {
                        coroutineScope.launch(Dispatchers.IO) {
                            chatDao.deleteChat(chat)
                            refreshHistory()
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun HistoryItem(chatEntities: ChatEntities, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Q: ${chatEntities.question}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("A: ${chatEntities.answer}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Date: ${dateFormat.format(Date(chatEntities.date))}", fontSize = 12.sp, color = Color.DarkGray)
        }

        IconButton(onClick = onDelete,
            Modifier.size(24.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.trashcan),
                contentDescription = "Delete",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
