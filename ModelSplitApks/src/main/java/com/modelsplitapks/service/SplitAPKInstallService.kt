package com.modelsplitapks.service

import `in`.sunilpaulmathew.sCommon.Utils.sInstallerUtils
import android.app.Service
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi

/**
 *    author : ChenYuNan
 *    date   : 2022/7/14
 *    desc   :APKInstallService
 */
class SplitAPKInstallService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sInstallerUtils.setStatus(
            intent!!.getIntExtra(PackageInstaller.EXTRA_STATUS, -999),
            intent,
            this
        )
        stopSelf()
        return START_NOT_STICKY
    }
}