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
        val builder = JobInfo.Builder(JOB_ID, ComponentName(context, SpeedService::class.java))
        if (PreferenceHelper.isHideOnDisconnected(context)) builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        if (PreferenceHelper.isStartOnBoot(context)) builder.setPersisted(true)
        context.getSystemService(JobScheduler::class.java)?.schedule(builder.build())
    }

    @JvmStatic
    fun cancelAllJob(context: Context) {
        context.getSystemService(JobScheduler::class.java)?.cancelAll()
    }
}