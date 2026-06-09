package com.example.gerenciadordetarefascomfirebase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gerenciadordetarefascomfirebase.ui.screens.*
import com.example.gerenciadordetarefascomfirebase.ui.viewmodel.AuthViewModel
import com.example.gerenciadordetarefascomfirebase.ui.viewmodel.TaskViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()
    
    val startDestination = if (authViewModel.user.value != null) "tasks" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = {
                    navController.navigate("tasks") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("tasks") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("tasks") {
            LaunchedEffect(Unit) {
                taskViewModel.loadTasks()
            }
            
            TaskListScreen(
                taskViewModel = taskViewModel,
                authViewModel = authViewModel,
                onAddTask = { navController.navigate("taskForm") },
                onEditTask = { taskId -> navController.navigate("taskForm/$taskId") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("tasks") { inclusive = true }
                    }
                }
            )
        }
        composable("taskForm") {
            TaskFormScreen(
                taskId = null,
                viewModel = taskViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "taskForm/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskFormScreen(
                taskId = taskId,
                viewModel = taskViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
