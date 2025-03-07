package fr.isen.hugo.isensmartcompanion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.hugo.isensmartcompanion.models.EventModel
import fr.isen.hugo.isensmartcompanion.screens.EventsScreen
import fr.isen.hugo.isensmartcompanion.screens.HistoryScreen
import fr.isen.hugo.isensmartcompanion.screens.MainScreen
import fr.isen.hugo.isensmartcompanion.screens.TabView
import fr.isen.hugo.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("lifecycle", "MainActivity onCreate")
        enableEdgeToEdge()
        setContent {
            val homeTab = TabBarItem(title = getString(R.string.bottom_navbar_home), selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
            val eventsTab = TabBarItem(title = getString(R.string.bottom_navbar_events), selectedIcon = Icons.Filled.Notifications, unselectedIcon = Icons.Outlined.Notifications)
            val historyTab = TabBarItem(title = getString(R.string.bottom_navbar_history), selectedIcon = Icons.Filled.List, unselectedIcon = Icons.Outlined.List)

            val tabBarItems = listOf(homeTab, eventsTab, historyTab)

            val navController = rememberNavController()

            ISENSmartCompanionTheme {
                Scaffold( bottomBar = {
                    TabView(tabBarItems, navController)
                },
                    modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(navController = navController, startDestination = homeTab.title) {
                        composable(homeTab.title) {
                            MainScreen(innerPadding)
                        }
                        composable(eventsTab.title) {
                            EventsScreen(
                                    eventHandler = { event -> startEventDataActivity(event) }

                            )
                        }
                        composable(historyTab.title) {
                            HistoryScreen(innerPadding)
                        }
                    }

                }
            }
        }
    }


    fun startEventDataActivity(event: EventModel) {
        val intent = Intent(this, EventDetailActivity::class.java).apply {
            putExtra(EventDetailActivity.eventExtraKey, event)
        }
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("lifecycle", "MainActivity onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycle", "MainActivity onResume")
    }

    override fun onStop() {
        Log.d("lifecycle", "MainActivity onStop")
        super.onStop()
    }

    override fun onPause() {
        Log.d("lifecycle", "MainActivity onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("lifecycle", "MainActivity onDestroy")
        super.onDestroy()
    }
}