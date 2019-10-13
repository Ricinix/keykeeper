package com.example.keykeeper.model.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.keykeeper.model.room.data.KeySimplify
import io.reactivex.Single

@Dao
interface KeyDao {

    @Query("SELECT name, account, password FROM key_table WHERE kind=:kind")
    fun getByKind(kind:String): Single<List<KeySimplify>>
}