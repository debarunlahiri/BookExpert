package com.debarunlahiri.bookexpert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.debarunlahiri.bookexpert.ui.navigation.Routes
import com.debarunlahiri.bookexpert.ui.screens.home.HomeScreen
import com.debarunlahiri.bookexpert.ui.screens.items.ItemScreen
import com.debarunlahiri.bookexpert.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser == null) Routes.SIGN_IN else Routes.HOME

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.SIGN_IN) { 
            SignInScreen(navController) 
        }
        
        composable(Routes.HOME) { 
            HomeScreen(navController) 
        }
        
        composable(Routes.PDF_VIEWER) { 
            PdfViewerScreen(onBackClick = { navController.popBackStack() }) 
        }
        
        composable(Routes.IMAGE_HANDLER) { 
            ImageHandlerScreen() 
        }
        
        composable(Routes.SETTINGS) { 
            SettingsScreen() 
        }
        
        composable(Routes.ITEMS) { 
            ItemScreen() 
        }
    }
}
