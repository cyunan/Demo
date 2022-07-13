package com.modelsplitapks.callback

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