package com.mason.storage

import android.content.Context
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After

import com.mason.storage.extension.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        appContext = InstrumentationRegistry.getTargetContext()
    }

    @Test
    fun getFilesDir() {
        debug("DataDirectory:${Environment.getDataDirectory()}")//这个方法是获取内部存储的根路径
        debug("getFilesDir:${appContext.filesDir}")//这个方法是获取某个应用在内部存储中的files路径
        debug("getCacheDir:${appContext.cacheDir}")//这个方法是获取某个应用在内部存储中的cache路径
        debug("getDir:${appContext.getDir("storageAA", Context.MODE_PRIVATE)}")//这个方法是获取某个应用在内部存储中的自定义路径
        debug("getExternalStorageDirectory:${Environment.getExternalStorageDirectory()}")
        debug("getExternalStoragePublicDirectory:${Environment.getExternalStoragePublicDirectory(Environment.MEDIA_MOUNTED)}")
        debug("getExternalFilesDir:${appContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}")
        debug("getExternalFilesDirs:${appContext.getExternalFilesDirs(Environment.DIRECTORY_MUSIC).size}")
        debug("getExternalFilesDir:${appContext.getExternalFilesDirs(Environment.MEDIA_MOUNTED).size}")
        debug("getExternalCacheDir:${appContext.getExternalCacheDir()}")
        debug("getExternalCacheDirs:${appContext.getExternalFilesDirs(Environment.DIRECTORY_MUSIC).size}")
        debug("getRootDirectory:${Environment.getRootDirectory()}")
        debug("getDownloadCacheDirectory:${Environment.getDownloadCacheDirectory()}")


    }
}

