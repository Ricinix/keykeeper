package com.example.keykeeper

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.TitleData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataBaseKeyTest {
    private val keyDataBase = Room.databaseBuilder(
        ApplicationProvider.getApplicationContext(),
        KeyDataBase::class.java, "key_database"
    ).build()
    private val keyDao = keyDataBase.keyDao()
    private val titleDao = keyDataBase.titleDao()
    private var alreadyExistKeys = listOf<KeyData>()
    private var alreadyExistTitles = listOf<TitleData>()

    private val titleDataCorrect =
        listOf(TitleData("category0", 0), TitleData("category1", 1))
    private val keyDataWrong1 =
        listOf(
            KeyData(0, "name0", "account0", "password0", "kind0", "category0"),
            KeyData(1, "name1", "account1", "password1", "kind1", "category1"),
            KeyData(1, "name1", "account1", "password1", "kind1", "category1")
        )
    private val keyDataWrong2 =
        listOf(
            KeyData(0, "name0", "account0", "password0", "kind0", "category0"),
            KeyData(2, "name2", "account2", "password2", "kind2", "category2")
        )
    private val keyDataCorrect =
        listOf(
            KeyData(1, "name0", "account0", "password0", "kind0", "category0"),
            KeyData(2, "name0", "account1", "password1", "kind1", "category0"),
            KeyData(3, "name2", "account1", "password2", "kind0", "category0"),
            KeyData(4, "name3", "account3", "password2", "kind1", "category0"),
            KeyData(5, "name4", "account4", "password4", "kind0", "category1")
        )

    @Test
    fun insert_wrong_1_test() {
        runBlocking {
            titleDao.insertAll(titleDataCorrect)
            try {
                keyDataWrong1.forEach {
                    keyDao.insertKeyData(it)
                }
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
            }

            val result = getAll()
            Assert.assertEquals(keyDataWrong1.size - 1, result.size)
        }

    }

    @Test
    fun insert_wrong_2_test() {
        runBlocking {
            titleDao.insertAll(titleDataCorrect)
            try {
                keyDataWrong2.forEach {
                    keyDao.insertKeyData(it)
                }
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
            }

            val result = getAll()
            Assert.assertEquals(1, result.size)
        }
    }

    @Test
    fun insert_correct_test() {
        runBlocking {
            prepare_data()
            val result = getAll()
            Assert.assertEquals(keyDataCorrect, result)
        }
    }

    @Test
    fun getByOrder_test() {
        runBlocking {
            prepare_data()
            val result = keyDao.getByOrder(1)
            Assert.assertEquals(
                keyDataCorrect.last(),
                result.map { it.toKeyData(titleDataCorrect[1].name) }[0]
            )
        }
    }

    @Test
    fun deleteById_test() {
        runBlocking {
            prepare_data()
            val r = keyDao.deleteById(1)
            val result = getAll()
            result.forEach {
                Assert.assertNotEquals(1, it.id)
            }
            Assert.assertEquals(keyDataCorrect.size - 1, result.size)
            Assert.assertEquals(1, r)
        }
    }

    @Test
    fun update_test() {
        runBlocking {
            prepare_data()
            val newOne = KeyData(
                2,
                "update_name",
                "account0",
                "password0",
                "kind0",
                "category0"
            )
            val r = keyDao.updateKeyData(newOne)
            val result = getAll()[1]
            Assert.assertEquals(1, r)
            Assert.assertEquals(newOne, result)
        }
    }

    @Before
    fun setup() {
        runBlocking {
            alreadyExistTitles = titleDao.getAllTitle()
            val t = mutableListOf<KeyData>()
            alreadyExistTitles.forEach { itTitle ->
                t += keyDao.getByKind(itTitle.name).map { it.toKeyData(itTitle.name) }
            }
            alreadyExistKeys = t
            for (item in alreadyExistTitles) {
                titleDao.deleteByName(item.name)
            }
        }

    }

    @After
    fun clean() {
        runBlocking {
            alreadyExistTitles.forEach {
                titleDao.deleteByName(it.name)
            }
            titleDao.insertAll(alreadyExistTitles)
            alreadyExistKeys.forEach {
                keyDao.insertKeyData(it)
            }

        }
    }

    private suspend fun prepare_data() {
        titleDao.insertAll(titleDataCorrect)
        keyDataCorrect.forEach {
            keyDao.insertKeyData(it)
        }

    }

    private suspend fun getAll(): List<KeyData> {
        val title = titleDao.getAllTitle()
        val result = mutableListOf<KeyData>()
        title.forEach { td ->
            result += keyDao.getByKind(td.name).map { it.toKeyData(td.name) }
        }
        return result
    }
}