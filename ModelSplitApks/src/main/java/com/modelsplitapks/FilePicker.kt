package com.modelsplitapks

import android.app.Activity
import java.io.File
import java.util.*
/**
 *    author : chenyunan
 *    date   : 2022/6/28
 *    desc   : 文件读取类
 */

object FilePicker {

    fun getData(): List<String> {
        return getApk(Common.mPath)
    }
    private fun getApk(path: String): MutableList<String> {
        val mDir = mutableListOf<String>()
        val file = File(path)
        val tempList = file.listFiles() ?: return mDir
        for(tempFile in tempList){
            if (tempFile.isDirectory) {
                mDir.addAll(getApk(tempFile.path))
            }
            if (tempFile.isFile && tempFile.name.endsWith(".apk")) {
                mDir.add(tempFile.absolutePath)
            }
        }
        return mDir
    }


}