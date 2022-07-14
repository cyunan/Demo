package com.modelsplitapks.bean

import android.graphics.drawable.Drawable

/**
 *    author : ChenYuNan
 *    date   : 2022/7/13
 *    desc   :
 */
data class APKItems(
    val mIcon: Drawable,
    val mPermissions: List<String>,
    val mVersionCode: Long?,
    val mManifest: String?,
    val mAPPName: String?,
    val mPackageName: String?,
    val mVersionName: String?,
    val mSDKVersion: String?,
    val mMinSDKVersion: String?
)
