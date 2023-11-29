package com.samsvoice.roadtorecovery.soberclock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.samsvoice.roadtorecovery.R


class HistoryDetails : AppCompatActivity() {
    //variables
    var eventListener: ValueEventListener? = null
    private lateinit var dataListOfEnteries: ArrayList<HistoryDataClass>
    private lateinit var adapter: HistoryAdapter
    private lateinit var database: DatabaseReference
    private lateinit var recycleV: RecyclerView
    private lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_details)

        mAuth=FirebaseAuth.getInstance()

        recycleV=findViewById(R.id.recyclerHistory)
        //added
        val gridLayoutManager = GridLayoutManager(this@HistoryDetails, 1)

        recycleV.layoutManager= gridLayoutManager


        val builder = AlertDialog.Builder(this@HistoryDetails)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        dataListOfEnteries = ArrayList()
        adapter = HistoryAdapter(dataListOfEnteries)

        recycleV.adapter = adapter


        val userid = mAuth.currentUser!!.uid
        val diaryRoute ="$userid/Sober Clock/History" //
        database=
            FirebaseDatabase.getInstance().getReference(diaryRoute)

        eventListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataListOfEnteries.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(HistoryDataClass::class.java)

                    if (dataClass != null) {
                        dataListOfEnteries.add(dataClass)

                    }
                    else
                    {
                        //   Toast.makeText(this@Diary, "not working", Toast.LENGTH_SHORT).show()
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }// end onDataChange

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}