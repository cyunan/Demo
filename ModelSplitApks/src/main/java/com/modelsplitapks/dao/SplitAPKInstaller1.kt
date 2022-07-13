package com.modelsplitapks.dao

import `in`.sunilpaulmathew.sCommon.Utils.*
import android.content.Context
import com.modelsplitapks.base.OnCallbackImpl
import com.modelsplitapks.bean.Common
import com.modelsplitapks.service.SplitAPKInstallService
import com.modelsplitapks.unzip
import net.lingala.zip4j.exception.ZipException
import java.io.File
import java.util.*

/**
 *    author : ChenYuNan
 *    date   : 2022/7/12
 *    desc   : APKS操作类
 */

object SplitAPKInstaller1 {

    //解压apks
    fun handleAppBundle(
        path: String,
        context: Context,
        connectDevice: Boolean = true,//是否生成设备专用APK集
        callback: OnCallbackImpl.() -> Unit
    ) {
        val mCallback = OnCallbackImpl()
        mCallback.callback()
        object : sExecutor() {
            override fun onPreExecute() {
//                checkPermission(context)
//                mCallback.errorCallback("asidioasdulas")
                sUtils.delete(File(context.cacheDir.path, ""))
            }

            override fun doInBackground() {
                if (!path.endsWith(".apks")) return
                try {
                    unzip(path, context.cacheDir.path)
                } catch (ignored: ZipException) {
                    mCallback.errorInstall(ignored.toString())
                }
            }

            override fun onPostExecute() {
                Common.mAPKList.clear()
                Common.mPath = context.cacheDir.path

            }
        }.execute()
    }
}

