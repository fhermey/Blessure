package com.xeflo.blessure.databases

import androidx.room.TypeConverter
import java.sql.Time
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toTime(timeString: String?): Time? {
        return if (timeString == null) {
            null
        } else {
            Time.valueOf(timeString)
        }
    }

    @TypeConverter
    fun toTimeString(time: Time?): String? {
        return time?.toString()
    }
}