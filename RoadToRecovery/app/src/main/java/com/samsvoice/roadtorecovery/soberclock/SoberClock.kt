package com.samsvoice.roadtorecovery.soberclock

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.samsvoice.roadtorecovery.PointsDataClass
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

import com.samsvoice.roadtorecovery.R
import kotlin.time.Duration.Companion.hours

class SoberClock : AppCompatActivity() {
    //variables
    private lateinit var countdownTextView: TextView
    private lateinit var historyButton: FloatingActionButton
    private lateinit var addEntryButton: FloatingActionButton
    private lateinit var resetButton: FloatingActionButton
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button
    private lateinit var selectDueDateButton: Button
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheet: LinearLayout
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var countdownAttemptsRef: DatabaseReference

    var eventListener: ValueEventListener? = null
    private lateinit var dialog:AlertDialog

    private var formattedDate: String? = null
    private var dueDate: Date? = null
    private lateinit var userId: String
    private  var reason:String?=null
    private  var endDate:String?=null

    private var startTime:String?=null
    var previousePoints = 0

    //variables for notification
    private val CHANNEL_ID = "channelId"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sober_clock)
        // Save countdown attempt data to Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid

        //type casting
        historyButton = findViewById(R.id.btnHistory)
        bottomSheet = findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 0 //max size when collapsed
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        addEntryButton = findViewById(R.id.btnAddEntry)
        countdownTextView = findViewById(R.id.countdownTextView)
        resetButton = findViewById(R.id.btnReset)
        cancelButton = findViewById(R.id.btnCancel)
        saveButton = findViewById(R.id.btnSave)
        selectDueDateButton = findViewById(R.id.btnSelectDueDate)


        val builder = AlertDialog.Builder(this@SoberClock)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        dialog = builder.create()
        dialog.show()

        notificationChannel()

        //select goal date
        selectDueDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        //show bottom sheet
        addEntryButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        //close bottom sheet
        cancelButton.setOnClickListener {
            selectDueDateButton.text = "Select due date"
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        saveButton.setOnClickListener {
          //  startCountdown()
            createCurrentSoberclock()
            loadupdate()
            getData()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        resetButton.setOnClickListener {
           // addEntryButton.isEnabled = true
            resetCountdown()
        }

        historyButton.setOnClickListener {
            startActivity(Intent(this,HistoryDetails::class.java))
        }
        loadupdate()
        getData()
    }

    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Update the dueDate variable with the selected date
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                dueDate = selectedCalendar.time

                // Format the selected date and display it
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                formattedDate = dateFormat.format(dueDate)
                selectDueDateButton.text = formattedDate


            }, year, month, day)

        datePickerDialog.show()
    }

    fun startCountdown() {
        if (dueDate != null) {
            val currentTime = System.currentTimeMillis()
           val timeRemaining = dueDate!!.time - currentTime

            if (timeRemaining > 0) {
                countdownTimer = object : CountDownTimer(timeRemaining, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Calculate time remaining
                        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                        val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining.hours.inWholeHours) % 24
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60


                        // Format all components with leading zeros

                        val formattedHours =
                            String.format("%02d", hours)// Ensure 2-digit formatting
                        val formattedMinutes =
                            String.format("%02d", minutes)// Ensure 2-digit formatting
                        val formattedSeconds =
                            String.format("%02d", seconds) // Ensure 2-digit formatting

                        // Update the countdown TextView
                        countdownTextView.text =
                            " $days:$formattedHours:$formattedMinutes:$formattedSeconds"

                    }

                    override fun onFinish() {
                        // Countdown has finished
                        countdownTextView.text = "dd:00:00:00"
//
                    }
                }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = dateFormat.format(Calendar.getInstance().time)

                val countdownAttempt = Timer(currentDate, formattedDate)
                val soberClockRoute = "$userId/Sober Clock/Current"
                FirebaseDatabase.getInstance().getReference(soberClockRoute).child("Temp")
                    .setValue(countdownAttempt).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@SoberClock, "Saved", Toast.LENGTH_SHORT).show()
                            // updatePoints(dataClass)
                            //  dialog.dismiss()

                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this@SoberClock, e.message.toString(), Toast.LENGTH_SHORT
                        ).show()
                        //dialog.dismiss()
                    }


                countdownTimer.start()
                addEntryButton.isEnabled = false

            } else {
                // When the due date is in the past
                addEntryButton.isEnabled = true
                countdownTextView.text = "Due date has already passed."

            }
        } else {
            // When a due date has not been selected
            countdownTextView.text = "Please select a due date."
//            daysLeftTextView.text = "Days Left: 0" // Ensure 2-digit formatting
        }
    }

fun createCurrentSoberclock()
{
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = dateFormat.format(Calendar.getInstance().time)

    val countdownAttempt = Timer(currentDate, formattedDate)
    val soberClockRoute = "$userId/Sober Clock/Current"
    FirebaseDatabase.getInstance().getReference(soberClockRoute).child("Temp")
        .setValue(countdownAttempt).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@SoberClock, "Saved", Toast.LENGTH_SHORT).show()
                // updatePoints(dataClass)
                //  dialog.dismiss()

            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@SoberClock, e.message.toString(), Toast.LENGTH_SHORT
            ).show()
        }
}

    fun resetCountdown() {
        if (userId != null) {
            val soberClockRoute = "$userId/Sober Clock/Current"
            countdownAttemptsRef = FirebaseDatabase.getInstance().getReference(soberClockRoute)

            // Initialize Firebase Realtime Database reference
            countdownAttemptsRef.child("Temp")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val timerClass =
                                dataSnapshot.getValue(Timer::class.java)

                            if (timerClass != null) {
                                 startTime = timerClass.startTime
                                val dueDate = timerClass.dueDate
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                endDate = dateFormat.format(Calendar.getInstance().time)
                                reason="Cancelled"
                                val dbRef = FirebaseDatabase.getInstance()

                                val soberClockRoute ="$userId/Sober Clock/History"
                                val clockHistRef = dbRef.getReference(soberClockRoute).push() // Creates a new unique reference

                                val attempt =  HistoryDataClass(startTime,dueDate,endDate.toString(),reason)

                                // Save the new task to Firebase Realtime Database
                                clockHistRef.setValue(attempt)
                                updatePoints()
                                addEntryButton.isEnabled = true
                                countdownAttemptsRef.removeValue()
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("SoberClock", "Database error: ${databaseError.message}")
                    }
                })
        }

        // Cancel the current countdown timer, if running
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }

        // Clear the countdown TextView and days left TextView
        countdownTextView.text = "00:00:00"

        selectDueDateButton.text = "Select Due Date"


    }

    private fun getData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val soberClockRoute = "$userId/Sober Clock/Current"
            countdownAttemptsRef = FirebaseDatabase.getInstance().getReference(soberClockRoute)

            countdownAttemptsRef.child("Temp")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val timerClass = dataSnapshot.getValue(Timer::class.java)

                            if (timerClass != null) {
                                val startTime = timerClass.startTime
                                val dueDate = timerClass.dueDate

                                if (startTime != null && dueDate != null) {
                                    try {
                                        // Check if the date strings are valid
                                        if (isValidDate(startTime) && isValidDate(dueDate)) {
                                            // Parse startTime and dueDate into Date objects
                                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            val startTimeDate = dateFormat.parse(startTime)
                                            val dueDateDate = dateFormat.parse(dueDate)

                                            if (startTimeDate != null && dueDateDate != null) {
                                                val currentTime = System.currentTimeMillis()
                                                val timeRemaining = startTimeDate.time - currentTime

                                                // Now you can use timeRemaining and dueDateDate as needed
                                                restartCountDown(dueDateDate)
                                            }
                                        }
                                    } catch (e: ParseException) {
                                        Log.e("SoberClock", "Error parsing dates: $e")
                                    }
                                } else {
                                    Log.e("SoberClock", "Invalid startTime or dueDate")
                                }
                            } else {
                                Log.e("SoberClock", "CountdownAttempt is null")
                            }
                        } else {
                            Log.e("SoberClock", "Key 'Temp' does not exist in Firebase")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("SoberClock", "Database error: ${databaseError.message}")
                    }
                })
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            dateFormat.parse(dateStr)
            true
        } catch (e: ParseException) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun restartCountDown(date:Date) {
        //=========================================================================================
// for notifications
        //val intent = Intent(this, NewNotification::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        }// end pending intent

        val success = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("✴ Sober Timer successful")
            .setContentText("You have Finshed your goal successfully")
            .setSmallIcon(R.drawable.samslogo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        //=========================================================================================

        val currentDate = Date()// current date and time

        if (date != null ) {
            val currentTime = System.currentTimeMillis()
            val timeRemaining = date.time - currentTime

            if (timeRemaining > 0) {

                addEntryButton.isEnabled = false
                countdownTimer = object : CountDownTimer(timeRemaining, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Calculate time remaining
                        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                        // Format all components with leading zeros
                        val formattedHours = String.format("%02d", hours)
                        val formattedMinutes = String.format("%02d", minutes)
                        val formattedSeconds = String.format("%02d", seconds)

                        // Update the countdown TextView
                        countdownTextView.text =
                            "$days:$formattedHours:$formattedMinutes:$formattedSeconds"
                    }

                    override fun onFinish() {
                        // Countdown has finished
                        countdownTextView.text = "00:00:00"
                    }
                }

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //when the time remaining is <=0
                countdownTimer.start()
            } else {
                if (userId != null) {
                    updatePoints()
                    val soberClockRoute = "$userId/Sober Clock/Current"
                    countdownAttemptsRef = FirebaseDatabase.getInstance().getReference(soberClockRoute)

                    // Initialize Firebase Realtime Database reference
                    countdownAttemptsRef.child("Temp")
                        .addListenerForSingleValueEvent(object : ValueEventListener {

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val countdownAttempt =
                                        dataSnapshot.getValue(Timer::class.java)

                                    if (countdownAttempt != null) {
                                        val startTime = countdownAttempt.startTime
                                        val dueDate = countdownAttempt.dueDate
                                        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                        endDate = dateFormat.format(Calendar.getInstance().time)
                                        reason="Completed"
                                        val dbRef = FirebaseDatabase.getInstance()

                                        val soberClockRoute ="$userId/Sober Clock/History"
                                        val clockHistRef = dbRef.getReference(soberClockRoute).push() // Creates a new unique reference

                                        val attempt =  HistoryDataClass(startTime,dueDate,endDate.toString(),reason)

                                        // Save the new task to Firebase Realtime Database
                                        clockHistRef.setValue(attempt)
                                        Toast.makeText(this@SoberClock, "You have completed you goal", Toast.LENGTH_SHORT).show()
                                        val  notificationManagerCompat = NotificationManagerCompat.from(this@SoberClock)
                                        notificationManagerCompat.notify(NOTIFICATION_ID, success)
                                        countdownAttemptsRef.removeValue()
                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("SoberClock", "Database error: ${databaseError.message}")
                            }
                        })
                }

            }
        } else {
            Toast.makeText(this, "date is null", Toast.LENGTH_SHORT).show()
        }
    }// end method

    private fun updatePoints() {
        // enables
        addEntryButton.isEnabled = true
        // variables
        var points = 0
        var roundedPointWeek = 0

        // calculations
        val daysBetween = (calculateDaysBetweenDates(startTime.toString(),getCurrentDateInDesiredFormat()))+1
        val pointsDays =(daysBetween*10).toInt()
        //val weeksBetween = calculateWeeksBetweenDates(startDate, endDate)
        roundedPointWeek = if (daysBetween>=7){
            val pointWeek =(daysBetween/7)
            (Math.round(pointWeek.toDouble()).toInt())*50
        } else{
            val pointWeek =0
            (Math.round(pointWeek.toDouble()).toInt())*50
        }//end if week

        points += pointsDays +roundedPointWeek

        //--------------------------------------------------------------------------------------------
        val newpoints = previousePoints +points
        val pointsRoute = "$userId/Points"

        val soberClockDataClass= PointsDataClass(newpoints,"soberClockPoints")//

        FirebaseDatabase.getInstance().getReference(pointsRoute).child("soberClockPoints")
            .setValue(soberClockDataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SoberClock, "Loading successful", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this@SoberClock,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@SoberClock,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()

            }// end firebase else

      /*  val databaseReference =
            FirebaseDatabase.getInstance().getReference(pointsRoute).child("soberClockPoints")

//Create a Map with the update data
        val updatedData = mapOf(
            "dataPoints" to previousePoints +points,
            "entryName" to "soberClockPoints"
        )

//Update the data in the database
        databaseReference.updateChildren(updatedData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SoberClock, "points updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    // finish()
                } else {
                    Toast.makeText(this@SoberClock,"Update failed",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@SoberClock,e.message.toString(),Toast.LENGTH_SHORT).show()
            }

       */

    }//endupdate

    private fun loadupdate(){

    val diaryRoute ="$userId/Points" //

    val database=
        FirebaseDatabase.getInstance().getReference(diaryRoute)

    eventListener = database.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            for (itemSnapshot in snapshot.children) {
                val dataClass = itemSnapshot.getValue(PointsDataClass::class.java)

                if (dataClass != null) {

                    if(dataClass.entryName.toString() == "soberClockPoints"){
                        previousePoints = dataClass.dataPoints.toString().toInt()
                        dialog.dismiss()
                    }
                }
                else
                {
                    dialog.dismiss()
                    Toast.makeText(this@SoberClock,"Loading un - successful, Pleas contact Admin",Toast.LENGTH_SHORT).show()
                }
            }

        }// end onDataChange

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(this@SoberClock, "no enteries", Toast.LENGTH_LONG).show()
        }// end onCancelled

    })// end  eventListener
}// end method load

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

    fun getCurrentDateInDesiredFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        return dateFormat.format(currentDate)
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
    }// end method

}// end class