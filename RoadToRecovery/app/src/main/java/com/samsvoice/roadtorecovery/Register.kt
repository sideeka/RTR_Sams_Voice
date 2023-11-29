package com.samsvoice.roadtorecovery

import android.app.NotificationChannel
import android.app.NotificationManager

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Register : AppCompatActivity() {
    //variables

   /* private lateinit var regUsername:EditText
    private lateinit var regPassword:EditText
    private lateinit var regConfirmPassword:EditText
    private lateinit var regSamPasswordEntered:EditText
    private lateinit var btnRegister:Button
    private lateinit var mAuth: FirebaseAuth

    */

    //added
    //variables
    private lateinit var tvTitle: TextView
    private lateinit var edUserName: EditText
    private lateinit var edPassword: EditText
    private lateinit var edConfirmPassword: EditText
    private lateinit var samPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnCancelReg: Button
    private lateinit var mAuth: FirebaseAuth


    //variables for notification
    private val CHANNEL_ID = "channelId"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0

     var finalSamPassword =""

    private lateinit var database: DatabaseReference
    var eventListener: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //////////typecast
        tvTitle = findViewById(R.id.textView2)
        edUserName = findViewById(R.id.editTextTextEmailAddress2)
        edPassword = findViewById(R.id.editTextTextPassword2)
        edConfirmPassword = findViewById(R.id.editTextTextPassword3)
        btnRegister = findViewById(R.id.button3)
        btnCancelReg = findViewById(R.id.button4)

        samPassword = findViewById(R.id.SamsVoicePasswordET)

        mAuth = FirebaseAuth.getInstance()

        notificationChannel()

        mAuth = FirebaseAuth.getInstance()
       // setSamPassword()
        getSamPassword()

    }// end on create



    fun regUser(view: View) {
        if (view.id == R.id.button3) {
            val email = edUserName.text.toString().trim()
            val passwordReg = edPassword.text.toString().trim()
            val conPassword = edConfirmPassword.text.toString().trim()
            val samPassword = samPassword.text.toString().trim()


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email can't be blank", Toast.LENGTH_SHORT).show()
                return
            }
            if (TextUtils.isEmpty(passwordReg)) {
                Toast.makeText(this, "Password can't be blank", Toast.LENGTH_SHORT).show()
                return
            }
            if (TextUtils.isEmpty(conPassword)) {
                Toast.makeText(this, "Confirm password can't be blank", Toast.LENGTH_SHORT).show()
                return
            }
            if (TextUtils.isEmpty(samPassword)) {
                Toast.makeText(this, "Confirm password can't be blank", Toast.LENGTH_SHORT).show()
                return
            }

            if (conPassword == passwordReg && samPassword == finalSamPassword) {
                mAuth.createUserWithEmailAndPassword(email,passwordReg).addOnCompleteListener(this,
                    OnCompleteListener<AuthResult> {task ->
                        if (task.isSuccessful) {

                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                            mAuth = FirebaseAuth.getInstance()
                            val uid = mAuth.uid.toString()
                            if (uid != null) {
                                createUserPointsProfiles(uid)
                            } else {
                                Toast.makeText(this@Register, "Register not successful", Toast.LENGTH_SHORT)
                                    .show()

                            }// end if

                            finish()
                        } else {
                            Toast.makeText(this@Register, "FAILED TO REG", Toast.LENGTH_SHORT).show()
                        }
                    })
            }// end if password == confirm add else
        }//end if
    }// end reg

    private fun notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)

            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }// end if
    }// end method

    private fun createUserPointsProfiles( uid :String?)
    {
        // diary
        val uidpoints = "$uid/Points"
        val diaryDataClass = PointsDataClass(0, "DiaryPoints")//
        //val updatedDataDiary = mapOf(
          //  "dataPoints" to 0,
            //"entryName" to "DiaryPoints"
        //)
        FirebaseDatabase.getInstance().getReference(uidpoints).child("DiaryPoints")
            .setValue(diaryDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }.addOnFailureListener { e ->

            }// end firebase else

        //Goals
        val  goalsDataClass = PointsDataClass(0,"GoalsPoints")//
       /* val goalUploadData = mapOf(
            "dataPoints" to 0,
            "entryName" to "GoalsPoints"
        )

        */
        FirebaseDatabase.getInstance().getReference(uidpoints).child("GoalsPoints")
            .setValue(goalsDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }.addOnFailureListener { e ->

            }// end firebase else

        //to do list
        val toDoListDataClass= PointsDataClass(0,"ToDoListPoints")//
       /* val toDoListUploadData = mapOf(
            "dataPoints" to 0,
            "entryName" to "ToDoListPoints"
        )
        */
        FirebaseDatabase.getInstance().getReference(uidpoints).child("ToDoListPoints")
            .setValue(toDoListDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }.addOnFailureListener { e ->

            }// end firebase else

        // sober clock
        val soberClockDataClass= PointsDataClass(0,"soberClockPoints")//
       /* val soberClockUploadData = mapOf(
            "dataPoints" to 0,
            "entryName" to "soberClockPoints"
        )
        */
        FirebaseDatabase.getInstance().getReference(uidpoints).child("soberClockPoints")
            .setValue(soberClockDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    finish()
                }
            }.addOnFailureListener { e ->

            }// end firebase else

    }

    private fun getSamPasswordold()
    {
        database=
            FirebaseDatabase.getInstance().getReference("Sams Voice password")

        eventListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(SamVoicePasswordDataClass::class.java)
                    if (dataClass != null) {
                        finalSamPassword = dataClass.Password.toString()
                        val pie = "yay"
                    }
                    else
                    {
                        Toast.makeText(this@Register, "Failed to get ", Toast.LENGTH_SHORT).show()
                    }
                }
                Toast.makeText(this@Register, "Password", Toast.LENGTH_SHORT).show()
            }// end onDataChange

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Register, "no enteries", Toast.LENGTH_LONG).show()
            }// end onCancelled

        })// end  eventListener


    }

    private fun getSamPassword()
    {
        database=
            FirebaseDatabase.getInstance().getReference("Sams Voice password")

        eventListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(SamVoicePasswordDataClass::class.java)
                    if (dataClass != null) {
                        finalSamPassword = dataClass.Password.toString()
                        val pie = "yay"
                    }
                    else
                    {
                        Toast.makeText(this@Register, "Failed to get ", Toast.LENGTH_SHORT).show()
                    }
                }
               // Toast.makeText(this@Register, "the points are working", Toast.LENGTH_SHORT).show()
            }// end onDataChange

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Register, "no enteries", Toast.LENGTH_LONG).show()
            }// end onCancelled

        })// end  eventListener


    }


    private fun setSamPassword()
    {
        val dataClass = SamVoicePasswordDataClass("sam")//

        FirebaseDatabase.getInstance().getReference("Sams Voice password").child("pass")
            .setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this@Register, "Saved", Toast.LENGTH_SHORT).show()

                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@Register, e.message.toString(), Toast.LENGTH_SHORT).show()

            }
    }



}// end register class


