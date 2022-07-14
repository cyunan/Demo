package com.modelsplitapks.dao

import `in`.sunilpaulmathew.sCommon.Utils.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.modelsplitapks.callback.OnCallbackImpl
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

object SplitAPKInstaller {

    //解压apks
    fun handleAppBundle(
        path: String,
        context: Context,
        connectDevice: Boolean = true,//是否生成设备专用APK集
        callback: OnCallbackImpl.() -> Unit
    ) {
        val mCallback = OnCallbackImpl()
        mCallback.callback()
        object : sExecutor(){
            override fun onPreExecute() {
//                checkPermission(activity)
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
                handlerPrepareInstall(context, connectDevice, mCallback)
            }
        }.execute()
    }

    private fun handlerPrepareInstall(
        context: Context,
        connectDevice: Boolean = true,//是否生成设备专用APK集
        callback: OnCallbackImpl
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
                try {
                    for (mAPKs in Common.mAPKList) {
                        sAPKUtils.getPackageName(mAPKs, context)?.let {
                            Common.mApplicationID = Objects.requireNonNull(
                                sAPKUtils.getPackageName(mAPKs, context)
                            )
                        }
                    }
                }catch (e: ConcurrentModificationException){
                    callback.errorInstall(e.toString())
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
                        context
                    )
                    installSplitAPKs(context, callback)
                }
            }

        }.execute()
    }

    //批量安装apk
    fun installSplitAPKs(
        context: Context,
        callback: OnCallbackImpl
    ){
        object : sExecutor(){
            override fun onPreExecute() {
                // TODO: 需要回调什么信息
//                checkPermission(context)
                sUtils.saveString("installationStatus", "waiting", context)
                callback.preInstall()
//                preCallBack?.let { preCallBack() }
//            val installIntent = Intent(activity, InstallerActivity::class.java)
//            activity.startActivity(installIntent)
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun doInBackground() {
                // TODO: 能不能得到实时安装进度
                callback.inInstall()
//                inCallBack?.let { inCallBack() }
                //返回文件的长度
                val totalSize = getTotalSize()
                //返回sInstallerParams对象
                val installParams = sInstallerUtils.makeInstallParams(totalSize)
                //返回代表该会话的唯一ID
                val sessionId = sInstallerUtils.runInstallCreate(installParams, context)
                try {
                    for (str in Common.mAPKList){
                        val mFile = File(str)
                        if (mFile.exists() && mFile.name.endsWith(".apk")) {
                            sInstallerUtils.runInstallWrite(
                                mFile.length(),
                                sessionId,
                                mFile.name,
                                mFile.toString(),
                                context
                            ) }
                    }
                } catch (ignored: NullPointerException) {
                    callback.errorInstall(ignored.toString())
//                    errorCallback?.let { errorCallback(ignored.toString()) }
                }
                sInstallerUtils.doCommitSession(
                    sessionId,
                    getInstallerCallbackIntent(context),
                    context
                )
            }
            override fun onPostExecute() {
                refreshStatus(context, callback)
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
        context: Context,
        callback: OnCallbackImpl
    ) {
        object : Thread() {
            @SuppressLint("StringFormatInvalid")
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(500)
                        val installationStatus: String =
                            sUtils.getString("installationStatus", "waiting", context)
                        if (installationStatus == "waiting") continue
                        if (installationStatus == context.getString(`in`.sunilpaulmathew.sCommon.R.string.installation_status_success)){
                            callback.successInstall()
//                            successCallback?.let { successCallback() }
                        }else{
                            callback.errorInstall(installationStatus)
//                            errorCallback?.let { errorCallback(installationStatus) }
                        }
                    }
                } catch (ignored: InterruptedException) {
                }
            }
        }.start()
    }
}