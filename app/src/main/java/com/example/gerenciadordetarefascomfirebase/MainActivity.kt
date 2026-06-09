package com.example.gerenciadordetarefascomfirebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gerenciadordetarefascomfirebase.ui.navigation.AppNavigation
import com.example.gerenciadordetarefascomfirebase.ui.theme.GerenciadorDeTarefasComFirebaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GerenciadorDeTarefasComFirebaseTheme {
                AppNavigation()
            }
        }
    }
}
