package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodrunner.R
import com.example.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var username:TextView
    lateinit var usermobilenumber:TextView
    lateinit var frameLayout: FrameLayout
    lateinit var sharedPreferences:SharedPreferences
     var premenuItem:MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawerlayout)
        coordinatorLayout=findViewById(R.id.coordinatorlayout)
        navigationView=findViewById(R.id.navview)
        val header=navigationView.getHeaderView(0)
        username=header.findViewById(R.id.username)
        usermobilenumber=header.findViewById(R.id.usermobilenumber)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.framelayout)
        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
        val fullname:String?=sharedPreferences.getString("username",null)
        val mobilenumber:String?=sharedPreferences.getString("usermobilenumber",null)
        if(fullname?.contains(" ")!!)
        {
            val name =" ${fullname.substring(0, fullname.indexOf(" "))}"
            username.text=name
            usermobilenumber.append("$mobilenumber")

        }
        else
        {
            val name= "$fullname"
            username.text=name
            usermobilenumber.append("$mobilenumber")

        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val actionBarDrawerToggle= ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        this.openHome()
        navigationView.setNavigationItemSelectedListener {
            if(premenuItem!=null)
            {
                premenuItem?.isChecked=false
            }
            it.isChecked=true
            it.isCheckable=true
            premenuItem=it
            when(it.itemId)
            {
                R.id.home ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        HomeFragment()
                    ).commit()
                    this.supportActionBar?.title="All Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.favorites ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        FavoritesFragment()
                    ).commit()
                    this.supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        ProfileFragment()
                    ).commit()
                    this.supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()

                }
                R.id.history ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        HistoryFragment()
                    ).commit()
                    this.supportActionBar?.title="My Previous Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        FAQSFragment()
                    ).commit()
                    this.supportActionBar?.title="Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }

            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun openHome() {
        this.supportActionBar?.title="All Restaurants"
        supportFragmentManager.beginTransaction().replace(
            R.id.framelayout,
            HomeFragment()
        ).commit()

       drawerLayout.closeDrawers()
        with(navigationView) { setCheckedItem(R.id.home) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)

        }
        when(item.itemId)
        {
          android.R.id.home-> {drawerLayout.openDrawer(GravityCompat.START)}

            R.id.logout ->{
                drawerLayout.closeDrawers()
                val dialog= AlertDialog.Builder(this)
                dialog.setMessage("Are you sure want to Logout?")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Yes"){ text,listener->
                    text.cancel()
                    sharedPreferences.edit().putBoolean("isloggedin",false).apply()
                    sharedPreferences.edit().putString("username",null).apply()
                    sharedPreferences.edit().putString("userphonenumber",null).apply()
                    val intent=Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.setNegativeButton("No"){ text,listener->
                    text.cancel()
                    navigationView.setCheckedItem(R.id.home)

                }
                dialog.create()
                dialog.show()

            }

        }
        return super.onOptionsItemSelected(item)

    }
    override fun onBackPressed() {
        val currentfragment=supportFragmentManager.findFragmentById(R.id.framelayout);
        when(currentfragment)
        {
            !is HomeFragment ->openHome()
            else-> ActivityCompat.finishAffinity(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
