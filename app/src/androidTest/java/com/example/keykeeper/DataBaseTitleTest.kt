package com.example.keykeeper

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataBaseTitleTest {
    private val keyDataBase = Room.databaseBuilder(
        ApplicationProvider.getApplicationContext(),
        KeyDataBase::class.java, "key_database"
    ).build()

    private val titleDao = keyDataBase.titleDao()
    private val testCorrectItemList =
        listOf(TitleData("name", 0), TitleData("name1", 1), TitleData("name2", 2))
    private val testWrongItemList1 =
        listOf(TitleData("name", 0), TitleData("name1", 0), TitleData("name2", 2))
    private val testWrongItemList2 =
        listOf(TitleData("name1", 0), TitleData("name1", 1), TitleData("name2", 2))
    private var alreadyExistList = listOf<TitleData>()

    @Test
    fun insert_one_piece_test(){
        runBlocking {
            val indexes = mutableListOf<Long>()
            for (item in testWrongItemList2){
                indexes += titleDao.insertTitle(item)
            }
            val result = titleDao.getAllTitle()
            Assert.assertEquals(testWrongItemList2.size-1, result.size)
            Assert.assertEquals(-1, indexes[1])
        }
    }

    @Test
    fun insert_test_1() {
        runBlocking {
            var t = listOf<Long>()
            try {
                t = titleDao.insertAll(testWrongItemList1)
            } catch (e: SQLiteConstraintException) {
                println("Unique 问题:$t")
            }
            val num = titleDao.getAllTitle().size
            Assert.assertEquals("insert fail:$t", 3, num)
        }
    }

    @Test
    fun insert_test_2() {
        runBlocking {
            try {
                titleDao.insertAll(testWrongItemList2)
            } catch (e: SQLiteConstraintException) {
                println("Unique 问题")
            }
            val num = titleDao.getAllTitle().size
            Assert.assertEquals("insert fail", 0, num)
        }
    }

    @Test
    fun insert_test_3() {
        runBlocking {
            try {
                titleDao.insertAll(testCorrectItemList)
            } catch (e: SQLiteConstraintException) {
                println("重复")
            }
            val num = titleDao.getAllTitle().size
            Assert.assertEquals("insert fail", 3, num)
        }
    }

    @Test
    fun getByOrder_test() {
        val mList = mutableListOf<TitleData>()
        runBlocking {
            titleDao.insertAll(testCorrectItemList)
            for (item in testCorrectItemList) {
                mList += titleDao.getTitleByOrder(item.order)
            }
        }
        Assert.assertEquals("get fail", testCorrectItemList, mList)
    }

    @Test
    fun get_all_test() {
        runBlocking {
            titleDao.insertAll(testCorrectItemList)
            val result = titleDao.getAllTitle()
//            val result = mutableListOf<TitleData>()
            Assert.assertEquals("get all fail", testCorrectItemList, result)
        }
    }

    @Test
    fun updateNameByOrder_test() {
        runBlocking {
            titleDao.insertAll(testCorrectItemList)
            for (item in testCorrectItemList) {
                titleDao.updateNameByOrder("update_${item.name}", item.order)
            }
            val result = titleDao.getAllTitle()
            Assert.assertEquals(
                "update fail",
                testCorrectItemList.map { "update_${it.name}" },
                result.map { it.name })
        }
    }

    @Test
    fun updateNameByOrder_conflict_test(){
        runBlocking {
            titleDao.insertAll(testCorrectItemList)
            try {
                titleDao.updateNameByOrder("name", 1)
            }catch (e : SQLiteConstraintException){
                val result = titleDao.getTitleByOrder(1)
                Assert.assertEquals(testCorrectItemList[1], result)
            }
        }
    }

    @Test
    fun updateOrderByName_test() {
        runBlocking {
            titleDao.insertAll(testCorrectItemList)
            for (item in testCorrectItemList) {
                titleDao.updateOrderByName(item.name, item.order + 1)
            }
            val result = titleDao.getAllTitle()
            Assert.assertEquals(
                "update fail",
                testCorrectItemList.map { it.order + 1 },
                result.map { it.order })
        }
    }

    @Test
    fun insert_one_test(){
        runBlocking {
            for (item in testCorrectItemList){
                titleDao.insertTitle(item)
            }
            val result = titleDao.getAllTitle()
            Assert.assertEquals("insert all fail", testCorrectItemList, result)
        }
    }

    @Test
    fun delete_test(){
        runBlocking {
            titleDao.insertAll(testCorrectItemList)
            for (item in testCorrectItemList){
                titleDao.deleteByName(item.name)
            }
            val result = titleDao.getAllTitle().size
            Assert.assertEquals("delete fail", 0, result)

        }
    }

    @Before
    fun setup() {
        runBlocking {
            alreadyExistList = titleDao.getAllTitle()
            for (item in alreadyExistList) {
                titleDao.deleteByName(item.name)
            }
        }
    }

    @After
    fun clean() {
        runBlocking {
            val whatInNow = titleDao.getAllTitle()
            for (item in whatInNow) {
                titleDao.deleteByName(item.name)
            }
            titleDao.insertAll(alreadyExistList)
        }
    }

}