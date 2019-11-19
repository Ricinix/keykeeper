package com.example.keykeeper

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.keykeeper", appContext.packageName)
//        val mSharePreference = PreferenceManager.getDefaultSharedPreferences(appContext)
        appContext.getSharedPreferences("title", Context.MODE_PRIVATE)

        val mMockSharePreferences = Mockito.mock(SharedPreferences::class.java)
        val mMockEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        `when`(mMockSharePreferences.edit()).thenReturn(mMockEditor)
        `when`(mMockEditor.commit()).thenReturn(false)
    }
}
