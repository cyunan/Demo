package com.modelsplitapks.dao

import `in`.sunilpaulmathew.sCommon.Utils.sAPKCertificateUtils
import `in`.sunilpaulmathew.sCommon.Utils.sExecutor
import `in`.sunilpaulmathew.sCommon.Utils.sUtils
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.modelsplitapks.bean.APKData
import com.modelsplitapks.bean.APKItems
import com.modelsplitapks.callback.OnCallbackImpl
import java.io.File

/**
 *    author : ChenYuNan
 *    date   : 2022/7/13
 *    desc   : 单apk安装
 */
var mFile: File? = null
fun manageInstallation(
    uri: Uri?,
    context: Context,
    callback: OnCallbackImpl,

    ){
    object : sExecutor() {
        override fun onPreExecute() {
            // Nullify previously acquired certificates, if any
            APKData.mCertificate = null
            uri?.let {
                sUtils.delete(context.getExternalFilesDir("APK"))
                mFile = File(context.getExternalFilesDir("APK"), "APK.apk")
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("StringFormatInvalid")
        override fun doInBackground() {
            if (uri != null) {
                sUtils.copy(uri, mFile, context)
            }
            try {
                val mAPKData: APKItems? = APKData.getAPKData(mFile!!.absolutePath, context)
                APKData.apply {
                    if (mAPKData == null) return@apply
                    mAPKData.mAPPName
                    mAPKData.mPackageName
                    mAPKData.mIcon

                    sAPKCertificateUtils(mFile, null, context)
                        .certificateDetails?.let { mCertificate = it }
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
                callback.errorInstall(ignored.toString())
            }
        }

        override fun onPostExecute() {

        }
    }.execute()
}