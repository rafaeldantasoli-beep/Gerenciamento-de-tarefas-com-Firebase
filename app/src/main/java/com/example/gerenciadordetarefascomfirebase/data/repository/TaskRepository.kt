package com.example.gerenciadordetarefascomfirebase.data.repository

import com.example.gerenciadordetarefascomfirebase.data.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val database = FirebaseDatabase.getInstance("https://gerenciamento-firebase-rafael-default-rtdb.firebaseio.com/")
    private val auth = FirebaseAuth.getInstance()

    private fun getTaskRef() = database.getReference("users")
        .child(auth.currentUser?.uid ?: "")
        .child("tasks")

    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children.mapNotNull { child ->
                    child.getValue(Task::class.java)?.copy(id = child.key ?: "")
                }
                trySend(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        val ref = getTaskRef()
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun addTask(task: Task) {
        val ref = getTaskRef().push()
        val newTask = task.copy(id = ref.key ?: "", createdAt = System.currentTimeMillis())
        ref.setValue(newTask).await()
    }

    suspend fun updateTask(task: Task) {
        getTaskRef().child(task.id).setValue(task).await()
    }

    suspend fun deleteTask(taskId: String) {
        getTaskRef().child(taskId).removeValue().await()
    }

    suspend fun toggleTaskStatus(taskId: String, isCompleted: Boolean) {
        getTaskRef().child(taskId).child("completed").setValue(isCompleted).await()
    }

    suspend fun getTaskById(taskId: String): Task? {
        val snapshot = getTaskRef().child(taskId).get().await()
        return snapshot.getValue(Task::class.java)?.copy(id = snapshot.key ?: "")
    }
}
