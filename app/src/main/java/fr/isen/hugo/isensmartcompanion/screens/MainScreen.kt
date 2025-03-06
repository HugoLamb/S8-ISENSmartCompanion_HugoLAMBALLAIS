package fr.isen.hugo.isensmartcompanion.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import fr.isen.hugo.isensmartcompanion.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
    fun MainScreen(innerPadding: PaddingValues) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var userInput by remember { mutableStateOf("") }
        var chatHistory by remember { mutableStateOf<List<String>>(emptyList()) }
        val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = "AIzaSyAGoi-_Uus9uaglqBCrxKj6onoI-xGhpC8")

        suspend fun sendMessage(message: String) {
            withContext(Dispatchers.IO) {
                try {
                    val response = generativeModel.generateContent(message)
                    response.text?.let {
                        chatHistory = chatHistory + "User: $message" + "\nAI: $it"
                    }
                } catch (e: Exception) {
                    Log.e("Gemini", "Error: ${e.message}")
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Image(
                    painter = painterResource(R.drawable.isen),
                    contentDescription = context.getString(R.string.isen_logo),
                    modifier = Modifier.width(120.dp).height(50.dp)
                )
                Text(text = "Smart Companion")
                Spacer(modifier = Modifier.weight(1f))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                chatHistory.forEach { message ->
                    Text(message, modifier = Modifier.padding(8.dp))
                }
            }

            Column(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp)
                ) {
                    TextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        shape = RoundedCornerShape(18.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
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
    }
