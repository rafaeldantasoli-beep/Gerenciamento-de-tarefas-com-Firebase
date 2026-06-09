package com.example.gerenciadordetarefascomfirebase.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciadordetarefascomfirebase.data.model.Task
import com.example.gerenciadordetarefascomfirebase.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository = TaskRepository()) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getTasks().collect { tasks ->
                    _tasks.value = tasks
                    _isLoading.value = false
                    
                    if (tasks.isEmpty()) {
                        addSampleTasks()
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar tarefas"
                _isLoading.value = false
            }
        }
    }

    private fun addSampleTasks() {
        viewModelScope.launch {
            saveTask(null, "Aprender Jetpack Compose 🚀", "Estudar layouts e estados", {})
            saveTask(null, "Configurar Firebase 🔧", "Ativar Auth e Realtime Database", {})
            saveTask(null, "Projeto Finalizado ✅", "Apresentar o app de tarefas", {})
        }
    }

    fun saveTask(id: String?, name: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (id == null) {
                    repository.addTask(Task(name = name, description = description))
                } else {
                    repository.updateTask(Task(id = id, name = name, description = description))
                }
                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao salvar tarefa"
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao excluir tarefa"
            }
        }
    }

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            try {
                repository.toggleTaskStatus(task.id, !task.completed)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar status"
            }
        }
    }

    suspend fun getTaskById(taskId: String): Task? {
        return repository.getTaskById(taskId)
    }
}
