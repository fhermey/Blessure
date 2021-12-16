package com.xeflo.blessure.datamodels

data class Settings(val id: Int,
                    var dashboardDays: Int) {

    override fun equals(other: Any?): Boolean {
        if ( other is Settings ) {
            return id == other.id &&
                    dashboardDays == other.dashboardDays
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + dashboardDays
        return result
    }

}