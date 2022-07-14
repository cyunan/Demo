package com.modelsplitapks.dao

import `in`.sunilpaulmathew.sCommon.Utils.sAPKCertificateUtils
import `in`.sunilpaulmathew.sCommon.Utils.sExecutor
import `in`.sunilpaulmathew.sCommon.Utils.sUtils
import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("StringFormatInvalid")
        override fun doInBackground() {
            if (uri != null) {
                sUtils.copy(uri, mFile, activity)
            }
            try {
                val mAPKData: APKItems? = APKData.getAPKData(mFile!!.absolutePath, activity)
                APKData.apply {
                    if (mAPKData == null) return@apply
                    mAPKData.mAPPName
                    mAPKData.mPackageName
                    mAPKData.mIcon
                    mAPKData.mPermissions
                    mAPKData.mManifest
                    mAPKData.mVersionName
                    mAPKData.mSDKVersion
                    mAPKData.mMinSDKVersion
                    sAPKCertificateUtils(mFile, null, activity).certificateDetails?.let { mCertificate = it }
                    mPermissions = mAPKData.mPermissions
                    mAPKData.mManifest?.let { mManifest = it }
                    mAPKData.mVersionName?.let { mVersion = it }
                    mAPKData.mSDKVersion?.let { mSDKVersion = it }
                    mAPKData.mMinSDKVersion?.let { mMinSDKVersion = it }
                    mSize = ((mAPK?.length() ) ?: "") as String

                }
                mAPKData?.let {
                    it.mPermissions.let { APKData.mAPK }
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