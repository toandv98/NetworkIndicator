package com.toandv.networkindicator.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.toandv.networkindicator.R
import com.toandv.networkindicator.data.PreferenceHelper.isRunning
import com.toandv.networkindicator.data.PreferenceHelper.running
import com.toandv.networkindicator.ui.AboutBottomSheet.Companion.newInstance
import com.toandv.networkindicator.utils.Constants.CHANNEL_INDICATOR
import com.toandv.networkindicator.utils.SchedulerUtils.cancelAllJob
import com.toandv.networkindicator.utils.SchedulerUtils.schedulerJob
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        setupView(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_about) {
            newInstance().show(supportFragmentManager, "about")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView(s: Bundle?) {
        toolbar_main.overflowIcon?.setTint(Color.WHITE)
        setSupportActionBar(toolbar_main)
        if (s == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_settings, SettingsFragment.newInstance())
                    .commit()
        }

        val isRunning = isRunning(this)
        sw_btn_on.isChecked = isRunning
        setTextRun(isRunning)

        sw_btn_on.setOnCheckedChangeListener { _, isChecked ->
            running(this, isChecked)
            setTextRun(isChecked)
        }
    }

    private fun setTextRun(b: Boolean) = when {
        b -> {
            schedulerJob(this)
            tv_run.setText(R.string.apps_is_running)
        }
        else -> {
            cancelAllJob(this)
            tv_run.setText(R.string.enable_indicator)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_INDICATOR,
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH)
            channel.apply {
                description = getString(R.string.channel_description)
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }
}