package com.toandv.networkindicator.data

import com.toandv.networkindicator.utils.Constants.SPEED_WITH_UNIT
import com.toandv.networkindicator.utils.Constants.UNIT_KBPS
import com.toandv.networkindicator.utils.Constants.UNIT_MBPS
import java.util.*

class Speed {

    private var tx: Long = 0
    private var rx: Long = 0
    private var total: Long = 0

    val unitTotal: String
        get() = getUnit(total)

    val txWithUnit: String
        get() = formatSpeed(tx)

    val rxWithUnit: String
        get() = formatSpeed(rx)

    fun setSpeed(tx: Long, rx: Long, time: Long) {
        this.tx = tx * 1000 / (time * 1024)
        this.rx = rx * 1000 / (time * 1024)
        total = (tx + rx) * 1000 / (time * 1024)
    }

    fun getTotal(): String {
        return getSpeed(total)
    }

    private fun formatSpeed(speed: Long): String {
        return String.format(SPEED_WITH_UNIT, getSpeed(speed), getUnit(speed))
    }

    private fun getSpeed(speed: Long): String {
        return if (speed < 1024) speed.toString() else String.format(Locale.getDefault(), "%.1f", speed / 1024.0)
    }

    private fun getUnit(speed: Long): String {
        return if (speed < 1024) UNIT_KBPS else UNIT_MBPS
    }
}