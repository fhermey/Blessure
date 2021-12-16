package com.xeflo.blessure.datamodels

import java.sql.Time

data class Alarm(val id: Int,
                 val time: Time,
                 val user: Int,
                 var token: String
) {

    override fun equals(other: Any?): Boolean {
        if ( other is Alarm ) {
            return id == other.id &&
                    time == other.time &&
                    user == other.user &&
                    token == other.token
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + time.hashCode()
        result = 31 * result + user
        result = 31 * result + token.hashCode()
        return result
    }


}