package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

fun Date.todayFormatted(): String {
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.futureDateFormatted(timePeriod: Int): String {
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, timePeriod)
    return dateFormat.format(calendar.time)
}
