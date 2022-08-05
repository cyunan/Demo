package com.bundletool.modelprotocol

import android.os.Environment
import com.example.tutorial.apex.ApexManifestProto
import com.example.tutorial.dependencies.AppDependenciesOuterClass
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 *    author : ChenYuNan
 *    date   : 2022/7/21
 *    desc   :
 */

fun handleParse(){
}

fun parseDependencies() {
    val path = "/storage/emulated/0/aab/dependencies.pb"
    val file = File(path)
    val fileInputStream = FileInputStream(file)
    val dependencies = AppDependenciesOuterClass.AppDependencies.parseFrom(fileInputStream)
    val list = dependencies.libraryList
    list.forEach {
        println(it.mavenLibrary)
    }
    saveLog(dependencies.toString(), "dependencies")
}

fun parseManifests() {
    val path = "/storage/emulated/0/aab/resources.pb"
    val file = File(path)
    val fileInputStream = FileInputStream(file)
    val dependencies = ApexManifestProto.ApexManifest.parseFrom(fileInputStream)
    saveLog(dependencies.toString(), "AndroidManifest")
}


fun saveLog(message: String, fileName: String) {
    val path = Environment.getExternalStorageDirectory().toString() + "/MyLog"
    val files = File(path)
    val date = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    if (!files.exists()) {
        files.mkdirs()
    }
    if (files.exists()) {
        try {
            val fw = FileWriter(
                path + File.separator
                    .toString() + fileName + dateFormat.format(date) + ".txt"
            )
            fw.write(
                """
                    $message
                    
                    """.trimIndent()
            )
            fw.write("\n")
            fw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun getFileSize(path: String): String{
    var fSize = File(path).length() /1024
    val decimal = (fSize - 1024) /1024
    return if (fSize > 1024){
        return "${fSize /1024}.${decimal}MB"
    }else{
        return "${fSize}KB"
    }
}