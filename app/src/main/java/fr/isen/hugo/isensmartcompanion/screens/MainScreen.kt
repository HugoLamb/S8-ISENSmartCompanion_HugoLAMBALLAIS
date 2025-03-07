package fr.isen.hugo.isensmartcompanion.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.hugo.isensmartcompanion.R
import fr.isen.hugo.isensmartcompanion.database.ChatDatabase
import fr.isen.hugo.isensmartcompanion.database.ChatEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var userInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    val listState = rememberLazyListState()

    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyAGoi-_Uus9uaglqBCrxKj6onoI-xGhpC8"
    )

    val database = ChatDatabase.getDatabase(context)
    val chatDao = database.chatDao()

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            try {
                val prompt = if (chatHistory.isEmpty()) {
                    "User: $message"
                } else {
                    chatHistory + "User: $message"
                }

                val response = generativeModel.generateContent(prompt.toString())
                response.text?.let {
                    chatHistory = chatHistory + "User: $message" + "\nAI: $it"

                    chatDao.insertChat(
                        ChatEntities(
                            question = message,
                            answer = response.text.toString(),
                            date = System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("Gemini", "Error: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Image(
                painter = painterResource(R.drawable.isen),
                contentDescription = context.getString(R.string.isen_logo),
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp)
            )
            val title1 = "IsenCompanio"
            Text(text = title1)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            items(chatHistory) { message ->
                ChatBubble(message)
            }
        }

        LaunchedEffect(chatHistory.size) {
            delay(100)
            listState.animateScrollToItem(chatHistory.size)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        if (userInput.isNotEmpty()) {
                            coroutineScope.launch {
                                sendMessage(userInput)
                            }
                            Toast.makeText(
                                context,
                                "Question Submitted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_send),
                            contentDescription = "Send",
                            tint = Color.Red
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ChatBubble(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier
                .padding(12.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        )
    }
}