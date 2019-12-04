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
    suspend fun getByKind(category: String): List<KeySimplify>

    @Query(
        "SELECT id, key_table.name, account, password, kind" +
                " FROM key_table INNER JOIN title_table ON key_table.category=title_table.title_name " +
                "WHERE title_order=:order"
    )
    suspend fun getByOrder(order: Int): List<KeySimplify>

    @Insert
    suspend fun insertKeyData(keyData: KeyData): Long

    @Query("DELETE FROM key_table WHERE id=:id")
    suspend fun deleteById(id: Int): Int

    @Update
    suspend fun updateKeyData(keyData: KeyData): Int
}