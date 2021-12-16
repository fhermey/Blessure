package com.xeflo.blessure

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view.*
import android.widget.Toast

import com.xeflo.blessure.R

import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.messaging.FirebaseMessaging


class BlessureActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val navController by lazy { findNavController(R.id.mainContent) }

    private var bloodPressureService: BloodPressureService? = null
    private val serviceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("DEBUGFH22: ", "onServiceConnected");
                bloodPressureService = (service as? BloodPressureService.Binder)?.service ?: run {
                    unbindService(this)
                    return
                }

                setupFirebase()
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(
            Intent(this, BloodPressureService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        setupNavigation()
    }

    // Setting Up One Time Navigation
    @SuppressLint("ClickableViewAccessibility")
    private fun setupNavigation() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)

        NavigationUI.setupActionBarWithNavController(this, navController, mainDrawerLayout)

        NavigationUI.setupWithNavController(navigationView, navController)

        navigationView.setNavigationItemSelectedListener(this)

        val navLogout = navigationView.requireViewById<LinearLayout>(R.id.nav_logout)
        Log.e("DEBUGFH22: ", "navLogout: " + navLogout)
        navLogout.setOnTouchListener { view, motionEvent ->
            Log.e("DEBUGFH22: ", "EVENT")
            if (MotionEvent.ACTION_UP == motionEvent.action) {
                bloodPressureService!!.logout()
                navigateTo(R.id.LoginFragment)
            }
            true
        }
    }

    fun hideNavigation() {
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    fun showNavigation() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.mainContent),
            mainDrawerLayout
        )
    }

    override fun onBackPressed() {
        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        menuItem.isChecked = true

        when (menuItem.itemId) {
            R.id.nav_list -> navigateTo(R.id.ListFragment)
            R.id.nav_alarm -> navigateTo(R.id.AlarmFragment)
            R.id.nav_settings -> navigateTo(R.id.SettingsFragment)
        }

        return true
    }

    private fun navigateTo(resId: Int, withAnimation: Boolean = true) {
        mainDrawerLayout.closeDrawers()

        val optionsBuilder = NavOptions.Builder()
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)

        navController.navigate(resId, null, if (withAnimation) optionsBuilder.build() else null)
    }

    private fun setupFirebase() {
        val intent = Intent(this, BlessureFirebaseMessagingService::class.java)
        startService(intent)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("BlessureActivity", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token: String? = task.result
                Log.e("BlessureActivity", "received token '" + token + "'");
                bloodPressureService!!.setFirebaseToken(token)
            })

    }
}