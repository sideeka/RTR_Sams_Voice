package com.samsvoice.roadtorecovery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class About : AppCompatActivity() {

    // variables
    // image buttons
    private lateinit var btnFlower1:ImageView
    private lateinit var btnFlower2:ImageView
    private lateinit var btnFlower3:ImageView
    private lateinit var btnFlower4:ImageView
    private lateinit var btnFacebook:ImageView

    private lateinit var rootLayout :View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        //type casting
        btnFlower1 = findViewById(R.id.btnflower1)
        btnFlower2 = findViewById(R.id.btnflower2)
        btnFlower3 = findViewById(R.id.btnflower3)
        btnFlower4 = findViewById(R.id.btnflower4)
        btnFacebook = findViewById(R.id.btnFacebook)

        rootLayout = findViewById(R.id.rootLayout)

        btnFacebook.isEnabled = false
        btnFacebook.visibility = View.INVISIBLE

        // on click events
        btnFlower1.setOnClickListener {
            rootLayout.setBackgroundResource(R.drawable.about1)
            btnFacebook.isEnabled = false
            btnFacebook.visibility = View.INVISIBLE
        }

        btnFlower2.setOnClickListener {
            rootLayout.setBackgroundResource(R.drawable.about2)
            //   displayFlower.setBackgroundResource(R.drawable.flower2)
            // val intent = Intent(this@MainActivity, chatStopWatch::class.java)
            // startActivity(intent)
            btnFacebook.isEnabled = false
            btnFacebook.visibility = View.INVISIBLE
        }

        btnFlower3.setOnClickListener {
            rootLayout.setBackgroundResource(R.drawable.about3)
            // displayFlower.setBackgroundResource(R.drawable.flower3)
            // val intent = Intent(this@MainActivity, chatStopWatch::class.java)
            //  startActivity(intent)
            btnFacebook.isEnabled = false
            btnFacebook.visibility = View.INVISIBLE
        }

        btnFlower4.setOnClickListener {
            rootLayout.setBackgroundResource(R.drawable.about4)
            // displayFlower.setBackgroundResource(R.drawable.flower4)
            // val intent = Intent(this@MainActivity, soberclock::class.java)
            //  startActivity(intent)

            btnFacebook.isEnabled = true
            btnFacebook.visibility = View.VISIBLE
        }

        btnFacebook.setOnClickListener {
            val intent = Intent(this@About,faceBookWebview::class.java)
            startActivity(intent)

        }



    }// end on create
}// end class