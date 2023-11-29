package com.samsvoice.roadtorecovery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.Timer
import java.util.TimerTask

class Loading : AppCompatActivity() {

    // Variables
    //private lateinit var tvName: TextView
    private lateinit var ivGif: ImageView
    private val delay: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)


        //type casting
        //tvName = findViewById(R.id.textView11)
        ivGif = findViewById(R.id.imageView)

        Glide.with(this).load(R.drawable.samsgif).into(ivGif)

        val timer = Timer()

        val timerTask = object : TimerTask() {
            override fun run() {
                finish()

                //val inty= Intent(this@Loading,Login::class.java)
                //startActivity(inty)
                val inty =Intent(this@Loading,Login::class.java)
                startActivity(inty)
            }
        }

        timer.schedule(timerTask, delay) // start timer

    }
}