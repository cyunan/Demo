package com.modelsplitapks

import `in`.sunilpaulmathew.sCommon.Utils.*
import `in`.sunilpaulmathew.sCommon.Utils.sAPKUtils.getPackageName
import `in`.sunilpaulmathew.sCommon.Utils.sPermissionUtils.requestPermission
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import java.io.File
import java.util.*

/**
 * author : chenyunan
 * date   : 2022/6/28
 * desc   : APKS操作类
 */

typealias Callback = ()-> Unit
//解压apks
fun handleAppBundle(
    path: String,
    activity: Activity,
    connectDevice: Boolean = true,//是否生成设备专用APK集
    preCallBack: Callback? = null,
    inCallBack: Callback? = null,
    postCallBack: Callback? = null
) {
    object : sExecutor(){
        override fun onPreExecute() {
            checkPermission(activity)
            sUtils.delete(File(activity.cacheDir.path, ""))
        }

        override fun doInBackground() {
            if (path.endsWith(".apks")) {
                unzip(path, activity.cacheDir.path)
                Log.d("handleAppBundle", ".apks unzip...")
            }
        }

        override fun onPostExecute() {
            Log.d("handleAppBundle", ".apks unzip success")
            Common.getAppList().clear()
            Common.setPath(activity.cacheDir.path)
            handlerPrepareInstall(activity, connectDevice, preCallBack, inCallBack, postCallBack)
//            installSplitAPKs(activity,connectDevice, preCallBack, inCallBack, postCallBack)
        }
    }.execute()
}

private fun handlerPrepareInstall(
    activity: Activity,
    connectDevice: Boolean = true,//是否生成设备专用APK集
    preCallBack: Callback? = null,
    inCallBack: Callback? = null,
    postCallBack: Callback? = null
) {
    object : sExecutor(){
        override fun onPreExecute() {
            //过滤数据
            if (!connectDevice) return
            Common.getAppList().addAll(
                FilePicker.getData(activity).apply {
                    this.filterNot {
                        it.isContains(basePrefix + baseMaster) && !it.isContains(getApiSuffixFilter())
                                || (it.isContains(basePrefix + baseARM) && !it.isContains(getCPUSuffixFilter()))
                                || (it.isContains(basePrefix + baseARM) && !it.isContains(getApiSuffixFilter()))
                                || it.isBlank()
                    }
                }
            )

        }

        override fun doInBackground() {
            for (mAPKs in Common.getAppList()) {
                sAPKUtils.getPackageName(mAPKs, activity)?.let {
                    Common.setApplicationID(
                        Objects.requireNonNull(
                            sAPKUtils.getPackageName(mAPKs, activity)
                        )
                    )
                }
            }
        }

        override fun onPostExecute() {
            Common.isUpdating(
                sPackageUtils.isPackageInstalled(
                    Common.getApplicationID(),
                    activity
                )
            )
            installSplitAPKs(activity, preCallBack, inCallBack, postCallBack)
        }

    }.execute()
}

//批量安装apk
fun installSplitAPKs(
    activity: Activity,
    preCallBack: Callback? = null,
    inCallBack: Callback? = null,
    postCallBack: Callback? = null
){
    object : sExecutor(){
        override fun onPreExecute() {
            // TODO: 跳转安装页面
            checkPermission(activity)
            preCallBack?.let { preCallBack() }
//            val installIntent = Intent(activity, InstallerActivity::class.java)
//            sUtils.saveString("installationStatus", "waiting", activity)
//            activity.startActivity(installIntent)
        }

        override fun doInBackground() {
            inCallBack?.let { inCallBack() }
            //返回文件的长度
            val totalSize = getTotalSize()
            //返回sInstallerParams对象
            val installParams = sInstallerUtils.makeInstallParams(totalSize)
            //返回代表该会话的唯一ID
            val sessionId = sInstallerUtils.runInstallCreate(installParams, activity)
            try {
                for (str in Common.getAppList()){
                    val mFile = File(str)
                    if (sUtils.exist(File(str)) && str.endsWith(".apk")) {
                        if (mFile.exists() && mFile.name.endsWith(".apk")) {
                            sInstallerUtils.runInstallWrite(
                                mFile.length(),
                                sessionId,
                                mFile.name,
                                mFile.toString(),
                                activity
                            ) }
                    }
                }
            } catch (ignored: NullPointerException) {}
            sInstallerUtils.doCommitSession(
                sessionId,
                getInstallerCallbackIntent(activity),
                activity
            )
        }
        override fun onPostExecute() {
            postCallBack?.let { postCallBack }
        }
    }.execute()
}


private fun getInstallerCallbackIntent(context: Context): Intent? {
    return Intent(context, SplitAPKInstallService::class.java)
}


private fun getTotalSize(): Long {
    var totalSize = 0
    if (Common.getAppList().size > 0) {
        for (string in Common.getAppList()) {
            val mFile = File(string)
            if (mFile.exists() && mFile.name.endsWith(".apk")) {
                totalSize += mFile.length().toInt()
            }
        }
    }
    return totalSize.toLong()
}


private fun checkPermission(activity: Activity){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // 先判断有没有权限
        if (Environment.isExternalStorageManager()) {
            //自动获取权限
            handlerPermission(activity)
        } else {
            //跳转到设置界面引导用户打开
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + activity.packageName)
            activity.startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE)
        }
    } else {
        //自动获取权限
        handlerPermission(activity)
    }
}

private fun handlerPermission(activity: Activity){

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECORD_AUDIO,
    )

    requestPermission(permissions,activity)
}


const val REQUEST_EXTERNAL_STORAGE = 1
const val basePrefix = "base-"
const val baseMaster = "master"
const val baseARM = "arm"
const val baseSuffix = ".apk"
const val baseV7 = "_v7a_"
const val baseV8 = "_v8a_"

private fun String.isContains(str: String) = this.contains(str)
private fun getApiSuffixFilter() =
    DeviceInformation.mApiVersion.run {
        if (this < Build.VERSION_CODES.LOLLIPOP){
            // TODO: 21以下待定
            ""
        }else if (this == Build.VERSION_CODES.LOLLIPOP
            ||this == Build.VERSION_CODES.LOLLIPOP){
            baseSuffix
        }else{//23以上 _2
            "_2$baseSuffix"
        }
    }
private fun getCPUSuffixFilter() = if (DeviceInformation.mIsX86) baseV7 else baseV8



