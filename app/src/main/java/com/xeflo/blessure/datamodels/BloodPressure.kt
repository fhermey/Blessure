package com.xeflo.blessure.datamodels

import java.time.LocalDateTime

data class BloodPressure(val id: Int,
                         val userId: Int,
                         val sys: Int,
                         val dia: Int,
                         val datetime: LocalDateTime
) {

    override fun equals(other: Any?): Boolean {
        if ( other is BloodPressure ) {
            return id == other.id &&
                    userId == other.userId &&
                    sys == other.sys &&
                    dia == other.dia &&
                    datetime == other.datetime
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + userId
        result = 31 * result + sys
        result = 31 * result + dia
        result = 31 * result + datetime.hashCode()
        return result
    }
}