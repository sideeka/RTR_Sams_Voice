package com.samsvoice.roadtorecovery

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.samsvoice.roadtorecovery.databinding.FragmentNewTaskSheetBinding
import java.time.LocalDate
import java.time.LocalTime

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment()
{
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueTimeString: String = ""
    private var dueTime: LocalTime? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()


        mAuth= FirebaseAuth.getInstance()
        if (taskItem != null)
        {
            binding.taskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
/*            if(taskItem!!.dueTime != null){
                dueTimeString = String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
                updateTimeButtonText()
            }*/
        }
        else
        {
            binding.taskTitle.text = "New Task"
        }

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener {
            saveAction()
        }
        binding.CompleteTaskButton.setOnClickListener {
            setCompleted()
        }
/*        binding.completeButton.setOnClickListener{
            setCompleted()
        }*/


    }




    private fun updateTimeButtonText() {
        dueTimeString = String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
        binding.CompleteTaskButton.text = dueTimeString
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater,container,false)
        return binding.root
    }


    private fun saveAction()
    {
        //take name and desc from textboxes
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val entryDate = LocalDate.now().toString()

        if (taskItem == null) {
            // Initialize Firebase Realtime Database reference
            val dbRef = FirebaseDatabase.getInstance()
          //  val newTaskRef = dbRef.child("tasks").push() // Creates a new unique reference
            val userid = mAuth.currentUser!!.uid
            val toDoListRoute ="$userid/ToDoList"
            val newTaskRef = dbRef.getReference(toDoListRoute).push() // Creates a new unique reference

            val taskId = newTaskRef.key.toString() // Get that reference to serve as task id

            val newTask = TaskItem(name, desc, "", entryDate,taskId)

            // Save the new task to Firebase Realtime Database
            newTaskRef.setValue(newTask)
        } else {
            val updatedTask = TaskItem(name, desc, "",taskItem!!.entryDate,taskItem!!.id)
            taskViewModel.updateTaskItem(updatedTask.id, updatedTask.name, updatedTask.desc, "",updatedTask.entryDate)
        }
        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

    private fun setCompleted(){
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val task = TaskItem(name, desc, LocalDate.now().toString(),taskItem!!.entryDate,taskItem!!.id)

        taskViewModel.updateTaskItem(task.id,task.name,task.desc,task.completedDate,task.entryDate)
        dismiss()
    }


}
