package com.modelsplitapks.base

import java.lang.Error

/**
 *    author : ChenYuNan
 *    date   : 2022/7/13
 *    desc   :
 */
interface OnCallback{
    fun preInstall()
    fun inInstall()
    fun successInstall()
    fun errorInstall(error: String)
}