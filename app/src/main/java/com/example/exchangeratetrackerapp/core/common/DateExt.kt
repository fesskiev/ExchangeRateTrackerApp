package com.example.exchangeratetrackerapp.core.common

private const val JUST_NOW_THRESHOLD_SECONDS = 30
private const val MINUTE_IN_SECONDS = 60
private const val HOUR_IN_SECONDS = 60 * MINUTE_IN_SECONDS

fun Long?.format(): String {
    if (this == null) {
        return "Unknown"
    }
    val now = System.currentTimeMillis()
    val diffSeconds = maxOf(0, (now - this) / 1000)
    return when {
        diffSeconds < JUST_NOW_THRESHOLD_SECONDS -> "Just now"
        diffSeconds < MINUTE_IN_SECONDS -> "$diffSeconds seconds ago"
        diffSeconds < MINUTE_IN_SECONDS * 2 -> "1 minute ago"
        diffSeconds < HOUR_IN_SECONDS -> "${diffSeconds / MINUTE_IN_SECONDS} minutes ago"
        else -> "Unknown"
    }
}