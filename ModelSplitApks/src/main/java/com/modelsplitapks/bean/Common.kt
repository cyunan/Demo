package com.modelsplitapks.bean

/**
 *    author : ChenYuNan
 *    date   : 2022/7/12
 *    desc   :
 */
object Common {
    var mReloadPage = false
    var mRunning = false
    var mSystemApp = false
    var mUninstal = false
    var mUpdating = false
    /**
     *  需要安装的apk列表
     */
    val mAPKList = mutableListOf<String>()

    var mApplicationID = ""
    var mPath: String = ""
}