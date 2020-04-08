package com.toandv.networkindicator.service

import android.app.Notification
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.os.Build.VERSION_CODES.O
import android.os.Handler
import android.widget.RemoteViews
import androidx.core.app.NotificationManagerCompat
import com.toandv.networkindicator.R
import com.toandv.networkindicator.data.PreferenceHelper.isRunning
import com.toandv.networkindicator.data.Speed
import com.toandv.networkindicator.utils.Constants.CHANNEL_INDICATOR
import com.toandv.networkindicator.utils.Constants.NOTIFICATION_ID
import com.toandv.networkindicator.utils.Constants.UNIT_KBPS
import com.toandv.networkindicator.utils.SchedulerUtils.schedulerJob

class SpeedService : JobService() {
    private var mLastTx: Long = 0
    private var mLastRx: Long = 0
    private var mLastMillis: Long = 0
    private val mHandler = Handler()
    private val mSpeed = Speed()
    private var mBuilder: Notification.Builder? = null
    private var mRemoteViews: RemoteViews? = null
    private var mConnectivityManager: ConnectivityManager? = null

    override fun onStartJob(params: JobParameters): Boolean {
        registerNetworkCallback()
        createNotification()
        mHandler.post(mRunnable)
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        mHandler.removeCallbacks(mRunnable)
        unregisterNetworkCallback()
        if (isRunning(this)) {
            schedulerJob(this)
        }
        return true
    }

    private val mRunnable = object : Runnable {
        override fun run() {
            val currentTx = TrafficStats.getTotalTxBytes()
            val currentRx = TrafficStats.getTotalRxBytes()
            val currentMillis = System.currentTimeMillis()
            val usedRx = currentRx - mLastRx
            val usedTx = currentTx - mLastTx
            val usedTime = currentMillis - mLastMillis
            mLastTx = currentTx
            mLastRx = currentRx
            mLastMillis = currentMillis
            mSpeed.setSpeed(usedTx, usedRx, usedTime)
            updateNotification()
            mHandler.postDelayed(this, 1000)
        }
    }

    private val networkCallback = object : NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            mRemoteViews?.apply {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        setTextViewText(R.id.tv_network_type, "Wifi")
                        setTextViewCompoundDrawables(R.id.tv_network_type, R.drawable.ic_wifi, 0, 0, 0)
                    }
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        setTextViewText(R.id.tv_network_type, "Cellular")
                        setTextViewCompoundDrawables(R.id.tv_network_type, R.drawable.ic_signal_cellular, 0, 0, 0)
                    }
                }
            }
        }
    }

    private fun createNotification() {
        val zero = getString(R.string.zero_unit_kbps)
        mRemoteViews = RemoteViews(packageName, R.layout.notification_speed)
        mBuilder = when {
            SDK_INT >= O -> Notification.Builder(this, CHANNEL_INDICATOR)
            else -> Notification.Builder(this).setPriority(Notification.PRIORITY_HIGH)
        }
        mRemoteViews?.apply {
            setTextViewText(R.id.tv_down, zero)
            setTextViewText(R.id.tv_up, zero)
        }

        mBuilder?.apply {
            setSmallIcon(createIconFromString("0", UNIT_KBPS))
            setOnlyAlertOnce(true)
            setLocalOnly(true)
            setOngoing(true)
            setVisibility(Notification.VISIBILITY_SECRET)
            setSound(Uri.EMPTY)
            when {
                SDK_INT >= N -> setCustomContentView(mRemoteViews)
                else -> setContent(mRemoteViews)
            }
            startForeground(NOTIFICATION_ID, build())
        }
    }

    private fun updateNotification() {
        mRemoteViews?.apply {
            setTextViewText(R.id.tv_down, mSpeed.rxWithUnit)
            setTextViewText(R.id.tv_up, mSpeed.txWithUnit)
        }
        mBuilder?.apply {
            setSmallIcon(createIconFromString(mSpeed.getTotal(), mSpeed.unitTotal))
            when {
                SDK_INT >= N -> setCustomContentView(mRemoteViews)
                else -> setContent(mRemoteViews)
            }
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, build())
        }
    }

    private fun createIconFromString(speed: String, units: String): Icon {
        val paint = Paint()
        val unitsPaint = Paint()
        paint.apply {
            isAntiAlias = true
            textSize = 65f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(getString(R.string.font_family_condensed), Typeface.BOLD)
        }
        unitsPaint.apply {
            isAntiAlias = true
            textSize = 40f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        val bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(speed, 48f, 52f, paint)
        canvas.drawText(units, 48f, 95f, unitsPaint)
        return Icon.createWithBitmap(bitmap)
    }

    private fun registerNetworkCallback() {
        val builder = NetworkRequest.Builder()
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mConnectivityManager?.registerNetworkCallback(builder.build(), networkCallback)
    }

    private fun unregisterNetworkCallback() {
        mConnectivityManager?.unregisterNetworkCallback(networkCallback)
    }
}