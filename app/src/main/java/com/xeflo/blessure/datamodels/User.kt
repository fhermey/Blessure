package com.xeflo.blessure.datamodels

data class User(val id: Int,
                val firstname: String,
                val lastname: String,
                val email: String
) {

    override fun equals(other: Any?): Boolean {
        if ( other is User ) {
            return id == other.id &&
                    firstname == other.firstname &&
                    lastname == other.lastname &&
                    email == other.email
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + firstname.hashCode()
        result = 31 * result + lastname.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }

}