package com.samsvoice.roadtorecovery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.samsvoice.roadtorecovery.databinding.ActivityToDoListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.samsvoice.roadtorecovery.databinding.TaskItemCellBinding
import java.text.DateFormat
 import java.text.SimpleDateFormat
import java.util.*


class ToDoList : AppCompatActivity(), TaskItemClickListener{

//variables
    private lateinit var binding: ActivityToDoListBinding

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference // Firebase Realtime Database reference
    private lateinit var dialog:AlertDialog
    private var points =0

    private lateinit var complete:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_to_do_list)
        binding = ActivityToDoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        mAuth= FirebaseAuth.getInstance()

        val builder = AlertDialog.Builder(this@ToDoList)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        dialog = builder.create()
        dialog.show()
        // Initialize Firebase Realtime Database reference
        val userid = mAuth.currentUser!!.uid
        val toDoListRoute ="$userid/ToDoList" //
        databaseReference = FirebaseDatabase.getInstance().getReference(toDoListRoute)



        binding.newTaskButton.setOnClickListener {

            NewTaskSheet(null).show(supportFragmentManager, "newTaskTag")
        }
        setRecyclerView()
    }// end on create

    private fun setRecyclerView() {
        val mainActivity = this

        // Fetch tasks from Firebase Realtime Database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskItems = mutableListOf<TaskItem>()
                    points = 0
                for (taskSnapshot in snapshot.children) {
                    val taskItem = taskSnapshot.getValue(TaskItem::class.java)// task snapshot is item snapshot
                    taskItem?.let {
                        taskItems.add(taskItem)
                        if(taskItem.isCompleted())
                        {
                            val daysBetween = (calculateDaysBetweenDates(taskItem.entryDate.toString(),taskItem.completedDate.toString()))+1

                            points += if (daysBetween<8){
                                ((8-daysBetween)*10).toInt()

                            } else{
                                0
                            }//end if week
                            val pie = "yay $points"
                          // points+=10
                        }//end if points
                    }//end task items?
                }// end for loop

                binding.todoListRecyclerView.apply {
                    layoutManager = LinearLayoutManager(applicationContext)
                    adapter = TaskItemAdapter(taskItems, mainActivity)
                    updatePoints()
                    dialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ToDoList, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }//end set recycler

    override fun completeTaskItem(taskItem: TaskItem)
    {
        NewTaskSheet(taskItem).show(supportFragmentManager,"newTaskTag")
    }

    private fun updatePoints( )
    {
        val uid=mAuth.currentUser?.uid
        val pointsRoute="$uid/Points"

        val toDoListDataClass= PointsDataClass(points,"ToDoListPoints")//

        FirebaseDatabase.getInstance().getReference(pointsRoute).child("ToDoListPoints")
            .setValue(toDoListDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ToDoList, "Loading successful", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this@ToDoList,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@ToDoList,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
            }// end firebase else


       /* val databaseReference=FirebaseDatabase.getInstance().getReference(pointsRoute).child("ToDoListPoints")

//Create a Map with the update data
        val updatedData = mapOf(
            "dataPoints" to points,
            "entryName" to "ToDoListPoints"
        )

//Update the data in the database
        databaseReference.updateChildren(updatedData)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    Toast.makeText(this@ToDoList,"points updated successfully",Toast.LENGTH_SHORT).show()
                    // finish()
                }else{
                    //Toast.makeText(this@Goal,"Update failed",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{e->
                //Toast.makeText(this@Goal,e.message.toString(),Toast.LENGTH_SHORT).show()
            }

        */

    }//end update


    fun calculateDaysBetweenDates(startDate: String, endDate: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateObj = dateFormat.parse(startDate)
        val endDateObj = dateFormat.parse(endDate)

        if (startDateObj != null && endDateObj != null) {
            val diff = endDateObj.time - startDateObj.time
            // 1 day = 24 * 60 * 60 * 1000 milliseconds
            return diff / (24 * 60 * 60 * 1000)
        }

        return 0
    }

}// end class


