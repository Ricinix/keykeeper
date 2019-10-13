package com.example.keykeeper.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import io.reactivex.Single

@Dao
interface KeyDao {

    @Query("SELECT id, name, account, password, kind FROM key_table WHERE category=:category")
    fun getByKind(category:String): Single<List<KeySimplify>>

    @Insert
    fun insertKeyData(keyData: KeyData): Single<Long>

    @Query("DELETE FROM key_table WHERE id=:id")
    fun deleteById(id: Int): Single<Int>
}