package com.modelsplitapks;

import java.io.File
import java.util.*
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException


/**
 *    author : ChenYuNan
 *    date   : 2022/6/29
 *    desc   :
 */
fun delete(file: File): Boolean {
    if (file.isDirectory) {
        for (files in Objects.requireNonNull(file.listFiles())) delete(files)
    }
    return file.delete()
}
fun unzip(zip: String, path: String) {
    try {
        ZipFile(zip).extractAll(path)
    } catch (ignored: ZipException) {
    }
}

fun zip(zip: String?, files: List<File?>?) {
    try {
        ZipFile(zip).addFiles(files)
    } catch (ignored: ZipException) {
    }
}