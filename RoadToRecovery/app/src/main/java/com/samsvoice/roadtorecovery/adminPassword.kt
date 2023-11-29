package com.samsvoice.roadtorecovery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class adminPassword : AppCompatActivity() {

    //variables
    private lateinit var saveButton: Button
    private lateinit var dataemail: TextView
    private lateinit var datapassword: EditText

    private lateinit var database: DatabaseReference
    var eventListener: ValueEventListener? = null

    var finalSamPassword =""

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_password)

        // type casting
        saveButton= findViewById(R.id.saveAdminPassButton)
        dataemail = findViewById(R.id.adminEmail)
        datapassword= findViewById(R.id.adminNewPass)


        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        var userEmail =""
        mAuth=FirebaseAuth.getInstance()

        if (currentUser != null) {
            userEmail = currentUser.email.toString()
            dataemail.text = userEmail
        }


        saveButton.setOnClickListener{
            if (datapassword.text.toString() == "")
            {
                Toast.makeText(this@adminPassword, "Please enter in a new password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                setSamPassword()
            }
        }
    }// end on create




    private fun setSamPasswordold()
    {
        val newPass = datapassword.text.toString()
        val samPasswordDataclass=SamVoicePasswordDataClass(newPass)

        FirebaseDatabase.getInstance().getReference("Sams Voice password").child("pass")
            .setValue(samPasswordDataclass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@adminPassword,"Updated Successfully",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.addOnFailureListener { e ->
                    Toast.makeText(this@adminPassword,"update un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
            }
    }// set sams


    private fun setSamPassword()
    {
        val newPass = datapassword.text.toString()
        val dataClass = SamVoicePasswordDataClass(newPass)//

        FirebaseDatabase.getInstance().getReference("Sams Voice password").child("pass")
            .setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@adminPassword, "updated password", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@adminPassword, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }



}// end