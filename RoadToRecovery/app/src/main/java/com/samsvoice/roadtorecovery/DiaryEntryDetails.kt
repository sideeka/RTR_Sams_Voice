package com.samsvoice.roadtorecovery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView

class DiaryEntryDetails : AppCompatActivity() {
    //variables
    private lateinit var diaryDate: TextView
    private lateinit var desc: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_entry_details)

        //type casting
        diaryDate =findViewById(R.id.detailTitle)
        desc=findViewById(R.id.detailDesc)


        val bundle = intent.extras
        if (bundle != null) {
            desc.text="   "+bundle.getString("DiaryEntry")
            diaryDate.text= "   "+bundle.getString("Date")


        }
        desc.movementMethod = ScrollingMovementMethod()

    }// end on create
}// end class