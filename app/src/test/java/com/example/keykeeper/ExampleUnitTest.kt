package com.example.keykeeper

import com.example.keykeeper.model.room.data.TitleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun coroutine_test() = runBlocking {
        println("start")
        withContext(Dispatchers.IO){
            for (i in 0..100){
                println("$i")
            }
        }
        println("end")
    }

    @Test
    fun title_test(){
        val t1 = TitleData("a", 0)
        val t2 = TitleData("a", 0)
        println(t1==t2)
    }
}
