package com.samsvoice.roadtorecovery

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    //variables
    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    // private lateinit var btnCancel: Button
    private lateinit var mAuth: FirebaseAuth
    var btnReg: Button? = null
    //var norms=GetsAndSets()

    //variables for notification
    private val CHANNEL_ID = "channelId"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // type casting
        edUsername = findViewById(R.id.editTextTextEmailAddress)
        edPassword = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.button)
        // btnCancel = findViewById(R.id.button2)
        btnReg = findViewById(R.id.button5)
        mAuth = FirebaseAuth.getInstance()

        notificationChannel()

        btnLogin.setOnClickListener {
            loginUser()
        }

    }// end on create

    //login
    @SuppressLint("MissingPermission")
    private fun loginUser() {
        //=========================================================================================
// for notifications
        //val intent = Intent(this, NewNotification::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        }// end pending intent

        val success = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("✴ Log in successfull")
            .setContentText("You have logged in successfully")
            .setSmallIcon(R.drawable.samslogo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val unSuccessful = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("✴ Log in Un- successfull")
            .setContentText("You  have not logged in successfully")
            .setSmallIcon(R.drawable.samslogo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        //=========================================================================================

        try {
            var email = edUsername.text.toString().trim()
            var password = edPassword.text.toString().trim()

           // email="sideeka@gmail.com"
            //password="sideeka17"


            if (TextUtils.isEmpty(email)) {

                Toast.makeText(this, "Please enter an email address!", Toast.LENGTH_SHORT).show()
                edUsername.requestFocus()
                return
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show()
                edPassword.requestFocus()
                return
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val  notificationManagerCompat = NotificationManagerCompat.from(this@Login)
                    notificationManagerCompat.notify(NOTIFICATION_ID, success)

                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                    val inty = Intent(this, MainActivity::class.java)
                    startActivity(inty)
                } else {
                    val  notificationManagerCompat = NotificationManagerCompat.from(this@Login)
                    notificationManagerCompat.notify(NOTIFICATION_ID, unSuccessful)
                    Toast.makeText(this, "Login Unsuccessful! Please try again.", Toast.LENGTH_SHORT).show()
                    edUsername.setText("")
                    edPassword.setText("")
                    edUsername.requestFocus()
                }
            }
        } catch (eer: Exception) {
            Toast.makeText(this, "Error Occurred: " + eer.message, Toast.LENGTH_SHORT).show()
        }
    }// ends login

    //for cancel button
    fun cancelFields(view: View)
    {
        Toast.makeText(this, "Login option cancelled", Toast.LENGTH_SHORT).show()
        edUsername.setText("")
        edPassword.setText("")
        edUsername.requestFocus()
    }

    //to go to next screen for reg
    fun regScreen(view: View)
    {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }

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
    }
}// end class