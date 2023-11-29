package com.samsvoice.roadtorecovery

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.Calendar

class DiaryEntry : AppCompatActivity() {
    //variables
    private lateinit var saveButton: Button
    private lateinit var dataDate: TextView
    private lateinit var dataDesc: TextView
    private lateinit var mAuth: FirebaseAuth

    //variables for notification
    private val CHANNEL_ID = "channelId"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_entry)

        //type casting
        dataDate = findViewById(R.id.uploadTitle)
        dataDesc = findViewById(R.id.uploadDesc)
        saveButton= findViewById(R.id.saveButton)
        mAuth=FirebaseAuth.getInstance()
        notificationChannel()

        val currentDate = DateFormat.getDateInstance().format(Calendar.getInstance().time)
        dataDate.text=currentDate.toString()

        saveButton.setOnClickListener {
            uploadData()
        }// end save



    }// end on create

    @SuppressLint("MissingPermission")
    private fun uploadData(){

        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        //=========================================================================================
// for notifications
        //val intent = Intent(this, NewNotification::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        }// end pending intent

        val success = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("✴ Diary entry successful")
            .setContentText("The creation of your diary entry for "+currentDate+" successfully")
            .setSmallIcon(R.drawable.samslogo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val unSuccessful = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("✴ Diary entry unsuccessful")
            .setContentText("The creation of your diary entry for "+currentDate+" was unsuccessful")
            .setSmallIcon(R.drawable.samslogo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
//=========================================================================================


        val builder = AlertDialog.Builder(this@DiaryEntry)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        val title = dataDate.text.toString()
        val desc =dataDesc.text.toString()

      if( desc == "")
      {
          Toast.makeText(this@DiaryEntry, "You have not entered in a description", Toast.LENGTH_SHORT).show()
          dataDesc.text= ""
          dialog.dismiss()

      }else {
          val dataClass = DiaryDataClass(title, desc, currentDate)//

          // val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
          //val userid = mAuth.currentUser!!.uid
          // val diaryRoute =userid+"_Diary"
          val userid = mAuth.currentUser!!.uid
          val diaryRoute = "$userid/Diary" //

          FirebaseDatabase.getInstance().getReference(diaryRoute).child(currentDate)
              .setValue(dataClass).addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      val notificationManagerCompat = NotificationManagerCompat.from(this)
                      notificationManagerCompat.notify(NOTIFICATION_ID, success)

                      Toast.makeText(this@DiaryEntry, "Saved", Toast.LENGTH_SHORT).show()
                      // updatePoints(dataClass)
                      dialog.dismiss()
                      finish()
                  }
              }.addOnFailureListener { e ->
                  val notificationManagerCompat = NotificationManagerCompat.from(this)
                  notificationManagerCompat.notify(NOTIFICATION_ID, unSuccessful)
                  Toast.makeText(
                      this@DiaryEntry, e.message.toString(), Toast.LENGTH_SHORT
                  ).show()
                  dialog.dismiss()
              }// end creation
      }// end if

    }// end upload

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

    private fun updatePoints(Diarydata :DiaryDataClass)
    {

           val userId = FirebaseAuth.getInstance().currentUser?.uid
           if (userId != null) {
               val goalRoute = userId + "_Diary"
               //val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

               val databaseReference = FirebaseDatabase.getInstance().getReference(goalRoute).child("sideeka")

               // Create a Map with the updated data
               val updatedData = mapOf(
                   "dataDesc" to "wow",
                   "dataDate" to "why hello"
               )

               // Update the data in the database
               databaseReference.updateChildren(updatedData)
                   .addOnCompleteListener { task ->
                       if (task.isSuccessful) {
                           Toast.makeText(this@DiaryEntry, "Data updated successfully", Toast.LENGTH_SHORT).show()
                           finish()
                       } else {
                           Toast.makeText(this@DiaryEntry, "Update failed", Toast.LENGTH_SHORT).show()
                       }
                   }
                   .addOnFailureListener { e ->
                       Toast.makeText(this@DiaryEntry, e.message.toString(), Toast.LENGTH_SHORT).show()
                   }
           }

    }// end update

}// end class