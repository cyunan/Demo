package com.modelsplitapks

import `in`.sunilpaulmathew.sCommon.Utils.sPermissionUtils
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

/**
 *    author : ChenYuNan
 *    date   : 2022/7/12
 *    desc   :
 */
const val REQUEST_EXTERNAL_STORAGE = 1

fun checkPermission(activity: Activity){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // 先判断有没有权限
        if (Environment.isExternalStorageManager()) {
            handlerPermission(activity)
        } else {
            //跳转到设置界面引导用户打开
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + activity.packageName)
            activity.startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE)
        }
    } else {
        handlerPermission(activity)
    }
}

private fun handlerPermission(activity: Activity){
    val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECORD_AUDIO,
    )

    sPermissionUtils.requestPermission(permissions, activity)
}