package com.samsvoice.roadtorecovery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Goal : AppCompatActivity() {

    //variables
    //added
    var eventListener: ValueEventListener? = null
    private lateinit var dataListOfEntries: ArrayList<GoalDataClass>
    private lateinit var adapter: GoalMyAdapter
    private lateinit var databaseReference: DatabaseReference
    //design components
    private lateinit var recycleV: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var searchButtonGoal: SearchView

    private lateinit var mAuth: FirebaseAuth
   private lateinit var dataListOfEntriesPoints: ArrayList<PointsDataClass>
    private lateinit var databaseReferenceUpdate: DatabaseReference
    private lateinit var dialog:AlertDialog
    private  var points = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        recycleV=findViewById(R.id.recyclerViewGoal)
        addButton=findViewById(R.id.fab4)
        searchButtonGoal=findViewById(R.id.searchGoal)
        mAuth=FirebaseAuth.getInstance()

        val currentDate = getCurrentDateInDesiredFormat()
        val gridLayoutManager = GridLayoutManager(this@Goal, 1)

        recycleV.layoutManager= gridLayoutManager

        val builder = AlertDialog.Builder(this@Goal)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        dialog = builder.create()
        dialog.show()
        
        dataListOfEntries = ArrayList()
        dataListOfEntriesPoints = ArrayList()

        adapter = GoalMyAdapter(this@Goal, dataListOfEntries)

        recycleV.adapter=adapter
        //val userid = mAuth.currentUser!!.uid
        //val goalRoute =userid+"_Goal"
        val userid = mAuth.currentUser!!.uid
        val goalRoute ="$userid/Goal" //
        var roundedPointWeek = 0

        databaseReference=
            FirebaseDatabase.getInstance().getReference(goalRoute)

        dialog.show()
        eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataListOfEntries.clear()
                points = 0
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(GoalDataClass::class.java)
                    if (dataClass != null) {
                        dataListOfEntries.add(dataClass)
                        val value =dataClass.dataGoalEndDate.toString()
                        if (dataClass.dataGoalEndDate.toString() <= getCurrentDateInDesiredFormat())
                        {
                            val daysBetween = (calculateDaysBetweenDates(dataClass.dataGoalStartDate.toString(),dataClass.dataGoalEndDate.toString()))+1
                            val pointsDays =(daysBetween*10).toInt()
                            //val weeksBetween = calculateWeeksBetweenDates(startDate, endDate)
                            if (daysBetween>=7){
                                val pointWeek =(daysBetween/7)
                                 roundedPointWeek = (Math.round(pointWeek.toDouble()).toInt())*50
                            }
                            else{
                                val pointWeek =0
                                 roundedPointWeek = (Math.round(pointWeek.toDouble()).toInt())*50
                            }//end if week
                            points += pointsDays +roundedPointWeek
                        }//if dates >=
                        else
                        {
                            val wow= "ww"
                        }
                    }//dataclass != null
                }// end for loop
                adapter.notifyDataSetChanged()
                updatePoints()
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })// end event


        addButton.setOnClickListener {
            val intent = Intent(this@Goal, GoalEntry::class.java)
            startActivity(intent)
        }// end add

        searchButtonGoal.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })// end search

    }// end goal on create


    fun searchList(text: String) {
        val searchList = java.util.ArrayList<GoalDataClass>()
        for (dataClass in dataListOfEntries) {
            if (dataClass.dataGoalTitle?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }

    fun getCurrentDateInDesiredFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Function to calculate the number of days between two dates
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun updatePoints( )
    {
        val uid=mAuth.currentUser?.uid
        val pointsRoute="$uid/Points"

        val  goalsDataClass = PointsDataClass(points,"GoalsPoints")//

        FirebaseDatabase.getInstance().getReference(pointsRoute).child("GoalsPoints")
            .setValue(goalsDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Goal, "Loading successful", Toast.LENGTH_SHORT).show()
                }else
                {
                    Toast.makeText(this@Goal,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@Goal,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
            }// end firebase else

    /*    val databaseReference=FirebaseDatabase.getInstance().getReference(pointsRoute).child("GoalsPoints")

//Create a Map with the update data
        val updatedData = mapOf(
            "dataPoints" to points,
            "entryName" to "GoalsPoints"
        )

//Update the data in the database
        databaseReference.updateChildren(updatedData)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    Toast.makeText(this@Goal,"Loading successful",Toast.LENGTH_SHORT).show()
                    // finish()
                }else{
                    Toast.makeText(this@Goal,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{e->
                //Toast.makeText(this@Goal,e.message.toString(),Toast.LENGTH_SHORT).show()
                Toast.makeText(this@Goal,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
            }
     */
    }//end update



}// end class