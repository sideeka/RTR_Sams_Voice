package com.samsvoice.roadtorecovery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView

class GoalEntryDetails : AppCompatActivity() {

    //variables

    private lateinit var title: TextView
    private lateinit var startDate: TextView
    private lateinit var startTime: TextView
    private lateinit var description: TextView
    private lateinit var endDate: TextView
    private lateinit var endTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_entry_details)

        //type casting
        title= findViewById(R.id.detailTitlGoal)
        startDate= findViewById(R.id.detailStartDateGoal)
        startTime= findViewById(R.id.detailStartTimeGoal)
        endDate= findViewById(R.id.detailenddateGoal)
        endTime= findViewById(R.id.detailendTimeGoal)
        description= findViewById(R.id.detailGoal)

        val bundle = intent.extras
        if (bundle != null) {
            title.text="  "+bundle.getString("Title")
            startDate.text="  start :"+bundle.getString("Start Date")
            endDate.text="  End :"+bundle.getString("End Date")
            startTime.text="  Time :"+bundle.getString("Start Time")
            endTime.text="  Time :"+bundle.getString("End Time")
            description.text=bundle.getString("Description")


        }
        description.movementMethod = ScrollingMovementMethod()




    }// end on create
}// end class