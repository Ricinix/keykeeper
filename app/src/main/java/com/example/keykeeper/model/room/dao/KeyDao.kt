package com.example.keykeeper.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import io.reactivex.Single

@Dao
interface KeyDao {

    @Query("SELECT id, name, account, password, kind FROM key_table WHERE category=:category")
    fun getByKind(category:String): Single<List<KeySimplify>>

    @Query("SELECT id, key_table.name, account, password, kind" +
            " FROM key_table INNER JOIN title_table ON key_table.category=title_table.name " +
            "WHERE title_order=:order")
    fun getByOrder(order: Int): Single<List<KeySimplify>>

    @Insert
    fun insertKeyData(keyData: KeyData): Single<Long>

    @Query("DELETE FROM key_table WHERE id=:id")
    fun deleteById(id: Int): Single<Int>

    @Update
    fun updateKeyData(keyData: KeyData): Single<Int>
}