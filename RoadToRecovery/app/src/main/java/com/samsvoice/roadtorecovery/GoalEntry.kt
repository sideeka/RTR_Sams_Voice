package com.samsvoice.roadtorecovery

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GoalEntry : AppCompatActivity() {

    //variables
   private lateinit var mAuth: FirebaseAuth

    private lateinit var saveButton: Button
    private lateinit var dataTile: TextView
    private lateinit var dataDesc: TextView

    //buttons for date
    private lateinit var startDateButton: Button
    private lateinit var startTimeButton: Button  //buttons for date
    private lateinit var endDateButton: Button
    private lateinit var endTimeButton: Button

    private var startDate: Date? = null
    private var startTime: Date? = null
    private var endDate: Date? = null
    private var endTime: Date? = null

    //variables for notification
    private val CHANNEL_ID = "channelId"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_entry)

        dataTile=findViewById(R.id.goalUploadTitle)
        dataDesc=findViewById(R.id.GoalUploadList)

        saveButton= findViewById(R.id.saveButtonGoal)

        startDateButton = findViewById(R.id.startDateButton2)
        startTimeButton = findViewById(R.id.startTimeButton2)
        startTimeButton.isEnabled = false
        endDateButton = findViewById(R.id.endDateButton2)
        endTimeButton = findViewById(R.id.endTimeButton2)
        endTimeButton.isEnabled = false

        startDateButton.setOnClickListener {
            showDatePicker(startDateListener)
            startTimeButton.isEnabled = true}
        startTimeButton.setOnClickListener { showTimePicker(startTimeListener) }

        endDateButton.setOnClickListener {
            showDatePicker(endDateListener)
            endTimeButton.isEnabled = true}
        endTimeButton.setOnClickListener { showTimePicker(endTimeListener) }

        notificationChannel()

        saveButton.setOnClickListener {
            uploadData()
        }// end save


    }// end on create


    @SuppressLint("MissingPermission")
    private fun uploadData(){

        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDateCompare = LocalDate.now().format(formatter)
        //getting data
        val title = dataTile.text.toString()
        val list =dataDesc.text.toString()

        val dateFormat=SimpleDateFormat("yyy-MM-dd",Locale.getDefault())
        val timeFormat=SimpleDateFormat("HH:mm",Locale.getDefault())

        //used to gt the current date

        val startDateString=startDateButton.text.toString()
        val startTimeString=startTimeButton.text.toString()

        val endDateString=endDateButton.text.toString()// 12/08/23
        val endTimeString=endTimeButton.text.toString()

        if(title=="" || list =="" || startDateString=="Start date" || startTimeString =="start time"||
                endDateString=="end date" || endTimeString =="end time")
        {
            Toast.makeText(this@GoalEntry, "You have not entered in information for 1 or 1+ fields", Toast.LENGTH_SHORT).show()
        }else
        {


            if (endDateString >= startDateString && startDateString>= currentDateCompare) {

                val dataClass = GoalDataClass(
                    title, startDateString, startTimeString,
                    endDateString, endTimeString, list, currentDate
                )//

                //=========================================================================================
// for notifications
                //val intent = Intent(this, NewNotification::class.java)
                val pendingIntent = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(intent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

                }// end pending intent

                val success = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("✴ Goal creation was successful")
                    .setContentText("You have successfully created a Goal")
                    .setSmallIcon(R.drawable.samslogo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build()

                val unSuccessful = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("✴ Goal creation Un- successful")
                    .setContentText("You  have not successfully created a Goal")
                    .setSmallIcon(R.drawable.samslogo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build()
//=========================================================================================

                mAuth = FirebaseAuth.getInstance()

                val userid = mAuth.currentUser!!.uid
                val goalRoute = "$userid/Goal/$currentDate"

                FirebaseDatabase.getInstance().getReference(goalRoute)
                    .setValue(dataClass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val notificationManagerCompat = NotificationManagerCompat.from(this)
                            notificationManagerCompat.notify(NOTIFICATION_ID, success)
                            Toast.makeText(this@GoalEntry, "Saved new task", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }.addOnFailureListener { e ->
                        val notificationManagerCompat = NotificationManagerCompat.from(this)
                        notificationManagerCompat.notify(NOTIFICATION_ID, unSuccessful)

                        Toast.makeText(
                            this@GoalEntry, e.message.toString(), Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                    this@GoalEntry,
                    "You have entered in an end date that is before the start date. re-enter",
                    Toast.LENGTH_SHORT
                ).show()
                startDateButton.text = "Start date"
                endDateButton.text = "End date"
            }// end if end date is >that start date
        }// end if == ""

    }// end method upload
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener)
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, dateSetListener, year, month, day)
        datePickerDialog.show()
    }// end show date picker

/////////uis

    private fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener)
    {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, timeSetListener, hour,
            minute, true)
        timePickerDialog.show()
    }//end show time picker

    // create start date
    private val startDateListener = DatePickerDialog.OnDateSetListener { _:
                                                                         DatePicker, year: Int, month: Int, day: Int ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(year, month, day)
        startDate = selectedCalendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateString = dateFormat.format(startDate!!)
        startDateButton.text = selectedDateString
    }// end start Date Listener

    // create start time
    private val startTimeListener = TimePickerDialog.OnTimeSetListener { _:
                                                                         TimePicker, hourOfDay: Int, minute: Int ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = startDate
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        selectedCalendar.set(Calendar.MINUTE, minute)
        startTime = selectedCalendar.time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val selectedTimeString = timeFormat.format(startTime!!)
        startTimeButton.text = selectedTimeString
    }// end start time listener

    //create end date
    private val endDateListener = DatePickerDialog.OnDateSetListener { _:
                                                                       DatePicker, year: Int, month: Int, day: Int ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(year, month, day)
        endDate = selectedCalendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateString = dateFormat.format(endDate!!)
        endDateButton.text = selectedDateString
    }// end  end Date Listener

    ///last
    //create end time
    private val endTimeListener = TimePickerDialog.OnTimeSetListener { _:
                                                                       TimePicker, hourOfDay: Int, minute: Int ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = endDate
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        selectedCalendar.set(Calendar.MINUTE, minute)
        endTime = selectedCalendar.time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val selectedTimeString = timeFormat.format(endTime!!)
        endTimeButton.text = selectedTimeString
    }// end end Time Listener

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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