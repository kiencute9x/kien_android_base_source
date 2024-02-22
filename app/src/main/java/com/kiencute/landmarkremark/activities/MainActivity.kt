package com.kiencute.landmarkremark.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.kiencute.landmarkremark.R
import com.kiencute.landmarkremark.databinding.ActivityMainBinding
import com.kiencute.landmarkremark.datastore.DataStoreManager
import com.kiencute.landmarkremark.extentions.allowReads
import com.kiencute.landmarkremark.extentions.collectIn
import com.kiencute.landmarkremark.extentions.setStatusBarColor
import com.kiencute.landmarkremark.preference.Settings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var uiStateJob: Job? = null

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        setSupportActionBar(binding.toolbar)
        getFirebaseFCMToken()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroy() {
        if (isTaskRoot && isFinishing) {
            finishAfterTransition()
        }
        super.onDestroy()
    }

    override fun onStop() {
        uiStateJob?.cancel()
        super.onStop()
    }

    private fun setupUI() {
        lifecycleScope.launch {
            dataStoreManager.themeMode.collectIn(this@MainActivity) { mode ->
                setNightMode(mode)
            }
        }
    }

    private fun setNightMode(mode: Int) {
        allowReads {
            uiStateJob = lifecycleScope.launchWhenStarted {
                dataStoreManager.setThemeMode(mode)
            }
        }
        when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO -> applyThemeMode(
                AppCompatDelegate.MODE_NIGHT_YES,
                R.drawable.ic_mode_night_default_black
            )

            AppCompatDelegate.MODE_NIGHT_YES -> applyThemeMode(
                Settings.MODE_NIGHT_DEFAULT,
                R.drawable.ic_mode_night_no_black
            )

            else -> applyThemeMode(
                AppCompatDelegate.MODE_NIGHT_NO,
                R.drawable.ic_mode_night_yes_black
            )
        }
    }

    private fun applyThemeMode(themeMode: Int, @DrawableRes icon: Int) {
        setStatusBarColor(R.color.status_bar)
        binding.fab.setImageResource(icon)
        binding.fab.setOnClickListener {
            setNightMode(themeMode)
        }
        if (AppCompatDelegate.getDefaultNightMode() != themeMode) {
            AppCompatDelegate.setDefaultNightMode(themeMode)
            window?.setWindowAnimations(R.style.WindowAnimationFadeInOut)
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getFirebaseFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("getFirebaseFCMToken: %s", token)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_userInfo -> {
                openInformationFragment()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openInformationFragment() {
        findNavController(R.id.nav_host_fragment_content_main).navigate(
            R.id.action_FirstFragment_to_userInformationFragment
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}