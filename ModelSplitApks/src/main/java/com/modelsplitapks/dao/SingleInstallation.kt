package com.modelsplitapks.dao

import `in`.sunilpaulmathew.sCommon.Utils.sExecutor
import `in`.sunilpaulmathew.sCommon.Utils.sUtils
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import com.modelsplitapks.bean.APKData
import com.modelsplitapks.bean.APKItems
import java.io.File

/**
 *    author : ChenYuNan
 *    date   : 2022/7/13
 *    desc   : 单apk安装
 */
var mFile: File? = null
fun manageInstallation(uri: Uri?, activity: Activity): sExecutor {
    return object : sExecutor() {
        override fun onPreExecute() {
            // Nullify previously acquired certificates, if any
            APKData.mCertificate = null
            if (APKData.mAPK != null) {
                mFile = APKData.mAPK
            } else if (uri != null) {
                sUtils.delete(activity.getExternalFilesDir("APK"))
                mFile = File(activity.getExternalFilesDir("APK"), "APK.apk")
            }
        }

        @SuppressLint("StringFormatInvalid")
        override fun doInBackground() {
            if (uri != null) {
                sUtils.copy(uri, mFile, activity)
            }
            try {
                val mAPKData: APKItems? = APKData.getAPKData(mFile!!.absolutePath, activity)
                mAPKData?.let {
                    // TODO: apk相关数据回调
                }
            } catch (ignored: Exception) {
                // TODO: 异常回调
            }
        }

        override fun onPostExecute() {

        }
    }
}