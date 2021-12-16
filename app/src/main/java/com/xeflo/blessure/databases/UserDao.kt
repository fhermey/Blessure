package com.xeflo.blessure.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.xeflo.blessure.entitymodels.UserEntity

@Dao
interface UserDao {
    @Query("select * from user")
    fun getList(): List<UserEntity>

    @Query("select * from user where id=:id")
    fun getById(id: Int): UserEntity

    @Query("delete from user")
    fun deleteAll()

    @Insert
    fun insert(user: UserEntity)

    @Delete
    fun delete(user: UserEntity)
}