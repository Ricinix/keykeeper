package com.example.keykeeper.model.room.dao

import androidx.room.*
import com.example.keykeeper.model.room.data.TitleData
import io.reactivex.Single

@Dao
interface TitleDao {

    @Query("SELECT * from title_table WHERE title_order=:order")
    suspend fun getTitleByOrder(order: Int): TitleData

    @Query("SELECT * FROM title_table ORDER BY title_order ASC")
    suspend fun getAllTitle(): List<TitleData>

    @Query("UPDATE title_table SET title_name = :newName WHERE title_order = :order")
    suspend fun updateNameByOrder(newName: String, order: Int): Int

    @Query("DELETE FROM title_table WHERE title_name = :name ")
    suspend fun deleteByName(name: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTitle(titleData: TitleData): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(titleList: List<TitleData>): List<Long>

    @Query("UPDATE title_table SET title_order = :order WHERE title_name = :name ")
    suspend fun updateOrderByName(name: String, order: Int): Int

}