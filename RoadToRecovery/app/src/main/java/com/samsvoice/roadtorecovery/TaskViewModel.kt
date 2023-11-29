package com.samsvoice.roadtorecovery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate


class TaskViewModel: ViewModel()
{
    var taskItems = MutableLiveData<MutableList<TaskItem>>()
    private lateinit var mAuth: FirebaseAuth
    init {
        taskItems.value = mutableListOf()
    }

    fun addTaskItem(newTask: TaskItem)
    {
        // Initialize Firebase Realtime Database reference
        val dbRef = FirebaseDatabase.getInstance().reference
        val newTaskRef = dbRef.child("tasks").push() // Creates a new unique reference
        val taskId = newTaskRef.key.toString()

        // Save the new task to Firebase Realtime Database
        newTaskRef.setValue(newTask)
    }

    fun updateTaskItem(id: String, name: String, desc: String, completedDate: String, entryDate: String)
    {
        if (id == null) {
            return
        }
        mAuth= FirebaseAuth.getInstance()
        val userid = mAuth.currentUser!!.uid
        val toDoListRoute ="$userid/ToDoList" //
        //val dbRef = FirebaseDatabase.getInstance().getReference("tasks").child(id)
        val task = TaskItem(name, desc, completedDate, entryDate, id)

        val dbRef = FirebaseDatabase.getInstance().getReference(toDoListRoute).child(id)
        dbRef.setValue(task)
    }

    fun setCompleted(taskItem: TaskItem)
    {
        taskItem.completedDate = (LocalDate.now()).toString()
        updateTaskItem(taskItem.id, taskItem.name, taskItem.desc, (LocalDate.now()).toString(), taskItem.entryDate)
    }
}

