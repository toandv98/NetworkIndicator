package com.toandv.networkindicator.utils

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.toandv.networkindicator.data.PreferenceHelper
import com.toandv.networkindicator.service.SpeedService
import com.toandv.networkindicator.utils.Constants.JOB_ID

object SchedulerUtils {

    @JvmStatic
    fun schedulerJob(context: Context) {
        JobInfo.Builder(JOB_ID, ComponentName(context, SpeedService::class.java)).apply {
            when {
                PreferenceHelper.isHideOnDisconnected(context) -> setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                PreferenceHelper.isStartOnBoot(context) -> setPersisted(true)
            }
            context.getSystemService(JobScheduler::class.java)?.schedule(build())
        }
    }

    @JvmStatic
    fun cancelAllJob(context: Context) {
        context.getSystemService(JobScheduler::class.java)?.cancelAll()
    }
}