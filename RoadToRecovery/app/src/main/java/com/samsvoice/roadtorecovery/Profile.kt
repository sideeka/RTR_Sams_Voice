package com.samsvoice.roadtorecovery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Profile : AppCompatActivity() {

    //added
    var eventListener: ValueEventListener? = null
    private lateinit var dataListOfEnteries: ArrayList<PointsDataClass>
    private lateinit var adapter: PointsMyAdapter
    private lateinit var database: DatabaseReference
    //design components
    private lateinit var recycleV: RecyclerView

    private lateinit var mAuth: FirebaseAuth
private lateinit var totalpoi:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//typecasting
        recycleV=findViewById(R.id.recyclerViewPoints)
        mAuth=FirebaseAuth.getInstance()
            totalpoi=findViewById(R.id.totalpoints)
        //added
        val gridLayoutManager = GridLayoutManager(this@Profile, 1)

        recycleV.layoutManager= gridLayoutManager


        val builder = AlertDialog.Builder(this@Profile)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        dataListOfEnteries = ArrayList()
        adapter = PointsMyAdapter(this@Profile,dataListOfEnteries)
        recycleV.adapter = adapter

        val userid = mAuth.currentUser!!.uid
        val pointsRoute="$userid/Points"
        database=
            FirebaseDatabase.getInstance().getReference(pointsRoute)
        var count = 0
        eventListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataListOfEnteries.clear()
                count = 0
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(PointsDataClass::class.java)
                    if (dataClass != null) {
                        dataListOfEnteries.add(dataClass)
                        var amount = 0
                         if (dataClass.dataPoints==0) {
                             amount =0
                        } else {
                             amount = dataClass.dataPoints!!
                        }
                        count+= amount
                    }
                }
                adapter.notifyDataSetChanged()
                val text = totalpoi.text.toString()
totalpoi.text=text+""+count.toString()
                dialog.dismiss()

            }// end onDataChange

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Profile, "no enteries", Toast.LENGTH_LONG).show()
            }// end onCancelled

        })// end  eventListener


    }// end on create
}// end class