package com.modelsplitapks.bean

import `in`.sunilpaulmathew.sCommon.Utils.sAPKUtils
import android.content.Context
import com.modelsplitapks.utils.PackageExplorer
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.bean.ApkMeta
import java.io.File
import java.io.IOException

/**
 *    author : ChenYuNan
 *    date   : 2022/7/13
 *    desc   : APK信息类
 */
object APKData {
    var mAPK: File? = null
    var mPermissions = mutableListOf<String>()
    var mCertificate: String? = null
    var mManifest = ""
    var mMinSDKVersion = ""
    var mSDKVersion = ""
    var mSize = ""
    var mVersion = ""

    fun getAPKData(apk: String, context: Context): APKItems? {
        try {
            ApkFile(File(apk)).use { apkFile ->
                val apkMeta: ApkMeta = apkFile.apkMeta
                val mAPKData = APKItems(
                    mIcon = sAPKUtils.getAPKIcon(apk, context),
                    mPermissions = apkMeta.usesPermissions,
                    mVersionCode = apkMeta.versionCode,
                    mManifest = PackageExplorer.readManifest(apk),
                    mAPPName = apkMeta.label,
                    mPackageName = apkMeta.packageName,
                    mVersionName = apkMeta.versionName,
                    mSDKVersion = apkMeta.compileSdkVersion,
                    mMinSDKVersion = apkMeta.minSdkVersion
                )
                apkFile.close()
                return mAPKData
            }
        } catch (ignored: IOException) {
        }
        return null
    }


}