package com.samsvoice.roadtorecovery

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.samsvoice.roadtorecovery.databinding.ActivityMainBinding
import com.samsvoice.roadtorecovery.soberclock.SoberClock

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //added
    private lateinit var tutorialButton: FloatingActionButton
    private lateinit var profileButton: FloatingActionButton
    private lateinit var todolistButton:ImageButton
    private lateinit var diaryButton:ImageButton
    private lateinit var goalsButton:ImageButton
    private lateinit var soberClockButton:ImageButton

    private lateinit var adminButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        var userEmail =""

        if (currentUser != null) {
            userEmail = currentUser.email.toString()
        }


        //fabs
        profileButton=findViewById(R.id.profilefab)
        tutorialButton=findViewById(R.id.Tutorialfab)
        diaryButton = findViewById(R.id.DiaryButton)
        todolistButton = findViewById(R.id.ToDoListButton)
        goalsButton = findViewById(R.id.GoalsButton)
        soberClockButton = findViewById(R.id.SoberClockButton)

        adminButton= findViewById(R.id.adminfab)

        if (userEmail == "roadtorecoverysamsvoice@gmail.com")
        {
            adminButton.isEnabled= true
            adminButton.visibility = View.VISIBLE
        }
        else
        {
            adminButton.isEnabled= false
            adminButton.visibility = View.INVISIBLE
        }

        adminButton.setOnClickListener{
            val intent= Intent(this@MainActivity,adminPassword::class.java)
            startActivity(intent)

        }


        profileButton.setOnClickListener{
            val intent= Intent(this@MainActivity,Profile::class.java)
            startActivity(intent)
        }

        tutorialButton.setOnClickListener{
            val intent= Intent(this@MainActivity,tutorial::class.java)
            startActivity(intent)
        }

        diaryButton.setOnClickListener{
            val intent= Intent(this@MainActivity,Diary::class.java)
            startActivity(intent)
        }

        todolistButton.setOnClickListener{
            val intent= Intent(this@MainActivity,ToDoList::class.java)
            startActivity(intent)
        }

        goalsButton.setOnClickListener{
            val intent= Intent(this@MainActivity,Goal::class.java)
            startActivity(intent)
        }

        soberClockButton.setOnClickListener{
            val intent= Intent(this@MainActivity,SoberClock::class.java)
            startActivity(intent)
        }

//////////////////////////////////////////////////////////


        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_home -> {
                    val intent= Intent(this@MainActivity,MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                /* R.id.nav_Login -> {
                     val intent= Intent(this@HomeActivity,Loading::class.java)
                     startActivity(intent)
                     true
                 }*/
                R.id.nav_Diary -> {
                    val intent= Intent(this@MainActivity,Diary::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_Goals -> {
                    val intent= Intent(this@MainActivity,Goal::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_About -> {
                    val intent= Intent(this@MainActivity,About::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_ToDoList-> {
                    val intent= Intent(this@MainActivity,ToDoList::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile-> {
                    val intent= Intent(this@MainActivity,Profile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_SoberClock-> {
                    val intent= Intent(this@MainActivity,SoberClock::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_Tutorial-> {
                    val intent= Intent(this@MainActivity,tutorial::class.java)
                    startActivity(intent)
                    true
                }



            }
            true

        }



    }// end on create

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}