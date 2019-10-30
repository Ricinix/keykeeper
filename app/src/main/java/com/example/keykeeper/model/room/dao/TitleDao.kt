package com.example.keykeeper.model.room.dao

import androidx.room.*
import com.example.keykeeper.model.room.data.TitleData
import io.reactivex.Single

@Dao
interface TitleDao {

    @Query("SELECT * from title_table WHERE title_order=:order")
    fun getTitleByOrder(order: Int): Single<TitleData>

    @Query("SELECT * FROM title_table ORDER BY title_order ASC")
    fun getAllTitle(): Single<List<TitleData>>

    @Query("UPDATE title_table SET name = :newName WHERE title_order = :order")
    fun updateNameByOrder(newName: String, order: Int): Single<Int>

    @Query("DELETE FROM title_table WHERE name = :name ")
    fun deleteByName(name: String): Single<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTitle(titleData: TitleData): Single<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(titleList: List<TitleData>): Single<List<Long>>

    @Query("UPDATE title_table SET title_order = :order WHERE name = :name ")
    fun updateOrderByName(name: String, order: Int): Single<Int>

}