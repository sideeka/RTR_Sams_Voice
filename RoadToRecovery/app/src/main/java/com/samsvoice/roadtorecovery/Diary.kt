package com.samsvoice.roadtorecovery


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.appcompat.widget.SearchView

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


import java.util.*
import kotlin.collections.ArrayList


class Diary : AppCompatActivity() {
//variables

    //added
    var eventListener: ValueEventListener? = null
    private lateinit var dataListOfEnteries: ArrayList<DiaryDataClass>
    private lateinit var adapter: DiaryMyAdapter
    private lateinit var database: DatabaseReference
    //design components
    private lateinit var recycleV:RecyclerView
    private lateinit var addButton:FloatingActionButton
    private lateinit var searchbutton:SearchView

    private lateinit var mAuth: FirebaseAuth

    private var countpoints = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        //typecasting
        recycleV=findViewById(R.id.recyclerView)
        addButton=findViewById(R.id.fab2)
        mAuth=FirebaseAuth.getInstance()


        searchbutton=findViewById(R.id.search)

        //added
        val gridLayoutManager = GridLayoutManager(this@Diary, 1)

        recycleV.layoutManager= gridLayoutManager


        val builder = AlertDialog.Builder(this@Diary)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        dataListOfEnteries = ArrayList()
        adapter = DiaryMyAdapter(this@Diary,dataListOfEnteries)

        recycleV.adapter = adapter


        val userid = mAuth.currentUser!!.uid
        val diaryRoute ="$userid/Diary" //
        database=
           FirebaseDatabase.getInstance().getReference(diaryRoute)

        eventListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataListOfEnteries.clear()
                 countpoints=0
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(DiaryDataClass::class.java)

                    if (dataClass != null) {
                       dataListOfEnteries.add(dataClass)
                        countpoints +=10
                    }
                    else
                    {
                     //   Toast.makeText(this@Diary, "not working", Toast.LENGTH_SHORT).show()
                    }
                }
                adapter.notifyDataSetChanged()
                updatePoints()
                dialog.dismiss()
               //Toast.makeText(this@Diary, "the points "+i, Toast.LENGTH_SHORT).show()
              }// end onDataChange

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Diary, "no enteries", Toast.LENGTH_LONG).show()
            }// end onCancelled

        })// end  eventListener


        addButton.setOnClickListener{
            val intent = Intent(this@Diary, DiaryEntry::class.java)
            startActivity(intent)
        }

      searchbutton.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
       })// end search button

    }// end on create

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<DiaryDataClass>()
        for (dataClass in dataListOfEnteries) {
            if (dataClass.dataDate?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }

    private fun updatePoints( )
    {

        val uid=mAuth.currentUser?.uid
            val pointsRoute="$uid/Points"

            val databaseReference=FirebaseDatabase.getInstance().getReference(pointsRoute).child("DiaryPoints")

        val diaryDataClass = PointsDataClass(countpoints, "DiaryPoints")//
        //val updatedDataDiary = mapOf(
        //  "dataPoints" to 0,
        //"entryName" to "DiaryPoints"
        //)
        FirebaseDatabase.getInstance().getReference(pointsRoute).child("DiaryPoints")
            .setValue(diaryDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Diary,"Loading successful",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@Diary,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()

            }// end firebase else



        /*
//Create a Map with the update data
            val updatedData = mapOf(
                "dataPoints" to countpoints,
            "entryName" to "DiaryPoints"
            )

//Update the data in the database
            databaseReference.updateChildren(updatedData)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        Toast.makeText(this@Diary,"points updated successfully",Toast.LENGTH_SHORT).show()
                       // finish()
                    }else{
                        //Toast.makeText(this@Diary,"Update failed",Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{e->
                    //Toast.makeText(this@Diary,e.message.toString(),Toast.LENGTH_SHORT).show()
                }

         */


    }//endupdate





}// end class