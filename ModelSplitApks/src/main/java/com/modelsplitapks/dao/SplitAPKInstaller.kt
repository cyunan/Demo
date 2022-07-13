package com.modelsplitapks.dao

import `in`.sunilpaulmathew.sCommon.Utils.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import com.modelsplitapks.bean.APKData
import com.modelsplitapks.bean.Common
import com.modelsplitapks.bean.DeviceData
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

typealias Callback = ()-> Unit
object SplitAPKInstaller {

    //解压apks
    fun handleAppBundle(
        path: String,
        activity: Activity,
        connectDevice: Boolean = true,//是否生成设备专用APK集
        preCallBack: Callback? = null,
        inCallBack: Callback? = null,
        successCallback: (()->Unit)? = null,
        errorCallback: ((String) -> Unit)? =null
    ) {
        object : sExecutor(){
            override fun onPreExecute() {
                checkPermission(activity)
                sUtils.delete(File(activity.cacheDir.path, ""))
            }

            override fun doInBackground() {
                if (!path.endsWith(".apks")) return
                try {
                    unzip(path, activity.cacheDir.path)
                } catch (ignored: ZipException) {
                    errorCallback?.let { errorCallback(ignored.toString()) }
                }
            }

            override fun onPostExecute() {
                Common.mAPKList.clear()
                Common.mPath = activity.cacheDir.path
                handlerPrepareInstall(activity, connectDevice, preCallBack, inCallBack, successCallback, errorCallback)
            }
        }.execute()
    }

    private fun handlerPrepareInstall(
        activity: Activity,
        connectDevice: Boolean = true,//是否生成设备专用APK集
        preCallBack: Callback? = null,
        inCallBack: Callback? = null,
        successCallback: (()->Unit)? = null,
        errorCallback: ((String) -> Unit)? =null
    ) {
        object : sExecutor(){
            override fun onPreExecute() {
                //过滤数据
                FilePicker.getData().filterNot {
                    connectDevice &&(it.isContains(basePrefix + baseMaster) && !it.isContains(
                        getApiSuffixFilter()
                    )
                            || (it.isContains(basePrefix + baseARM) && !it.isContains(
                        getCPUSuffixFilter()
                    ))
                            || (it.isContains(basePrefix + baseARM) && !it.isContains(
                        getApiSuffixFilter()
                    ))
                            || it.isBlank())
                }.apply { Common.mAPKList.addAll(this) }

            }

            override fun doInBackground() {
                for (mAPKs in Common.mAPKList) {
                    sAPKUtils.getPackageName(mAPKs, activity)?.let {
                        Common.mApplicationID = Objects.requireNonNull(
                            sAPKUtils.getPackageName(mAPKs, activity)
                        )
                    }
                }
            }

            override fun onPostExecute() {
                if(Common.mAPKList.size == 1){//apk集只有一个apk
//                    APKData.mAPK = File(Common.mAPKList[0])
//                    if (APKData.mAPK != null) {
//                        manageInstallation(null, activity).execute()
//                    } else if (activity.getIntent().getData() != null) {
//                        manageInstallation(getIntent().getData(), this).execute()
//                    }

                }else{//apk集有多个apk
                    Common.mUpdating = sPackageUtils.isPackageInstalled(
                        Common.mApplicationID,
                        activity
                    )
                    installSplitAPKs(activity, preCallBack, inCallBack, successCallback, errorCallback)
                }
            }

        }.execute()
    }

    //批量安装apk
    fun installSplitAPKs(
        activity: Activity,
        preCallBack: Callback? = null,
        inCallBack: Callback? = null,
        successCallback: (()->Unit)? = null,
        errorCallback: ((String) -> Unit)? =null
    ){
        object : sExecutor(){
            override fun onPreExecute() {
                // TODO: 需要回调什么信息
                checkPermission(activity)
                sUtils.saveString("installationStatus", "waiting", activity)
                preCallBack?.let { preCallBack() }
//            val installIntent = Intent(activity, InstallerActivity::class.java)
//            activity.startActivity(installIntent)
            }

            override fun doInBackground() {
                // TODO: 能不能得到实时安装进度
                inCallBack?.let { inCallBack() }
                //返回文件的长度
                val totalSize = getTotalSize()
                //返回sInstallerParams对象
                val installParams = sInstallerUtils.makeInstallParams(totalSize)
                //返回代表该会话的唯一ID
                val sessionId = sInstallerUtils.runInstallCreate(installParams, activity)
                try {
                    for (str in Common.mAPKList){
                        val mFile = File(str)
                        if (mFile.exists() && mFile.name.endsWith(".apk")) {
                            sInstallerUtils.runInstallWrite(
                                mFile.length(),
                                sessionId,
                                mFile.name,
                                mFile.toString(),
                                activity
                            ) }
                    }
                } catch (ignored: NullPointerException) {
                    errorCallback?.let { errorCallback(ignored.toString()) }
                }
                sInstallerUtils.doCommitSession(
                    sessionId,
                    getInstallerCallbackIntent(activity),
                    activity
                )
            }
            override fun onPostExecute() {
                refreshStatus(activity, successCallback, errorCallback)
            }
        }.execute()
    }


    private fun getInstallerCallbackIntent(context: Context): Intent {
        return Intent(context, SplitAPKInstallService::class.java)
    }


    private fun getTotalSize(): Long {
        var totalSize = 0
        if (Common.mAPKList.size > 0) {
            for (string in Common.mAPKList) {
                val mFile = File(string)
                if (mFile.exists() && mFile.name.endsWith(".apk")) {
                    totalSize += mFile.length().toInt()
                }
            }
        }
        return totalSize.toLong()
    }

    const val basePrefix = "base-"
    const val baseMaster = "master"
    const val baseARM = "arm"
    private const val baseSuffix = ".apk"
    private const val baseV7 = "_v7a_"
    private const val baseV8 = "_v8a_"

    private fun String.isContains(str: String) = this.contains(str)
    private fun getApiSuffixFilter() =
        DeviceData.mApiVersion.run {
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
    private fun getCPUSuffixFilter() = if (DeviceData.mIsX86) baseV7 else baseV8



    fun refreshStatus(
        activity: Activity,
        successCallback: (()->Unit)? = null,
        errorCallback: ((String) -> Unit)? =null
    ) {
        object : Thread() {
            @SuppressLint("StringFormatInvalid")
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(500)
                        val installationStatus: String =
                            sUtils.getString("installationStatus", "waiting", activity)
                        if (installationStatus == "waiting") continue
                        if (installationStatus == activity.getString(`in`.sunilpaulmathew.sCommon.R.string.installation_status_success)){
                            successCallback?.let { successCallback() }
                        }else{
                            errorCallback?.let { errorCallback(installationStatus) }
                        }
                    }
                } catch (ignored: InterruptedException) {
                }
            }
        }.start()
    }
}