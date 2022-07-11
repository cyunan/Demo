package com.modelsplitapks

import android.os.Build
import android.util.Log
import java.lang.reflect.Method

/**
 *    author : ChenYuNan
 *    date   : 2022/7/8
 *    desc   :
 */
object DeviceInformation {
    val mApiVersion: Int = getApiVersion()
    val mABI: Array<out String>? =  getABI()
    val mIsX86: Boolean = checkIfCPUx86()
    private val mAPKList: List<String> = ArrayList()

    private val mApplicationID: String? = null
    private var mPath:String? = null

    private fun getApiVersion() = Build.VERSION.SDK_INT
    private fun getABI() = Build.SUPPORTED_ABIS

    const val CPU_ARCHITECTURE_TYPE_32 = "32"
    const val CPU_ARCHITECTURE_TYPE_64 = "64"


    private const val LOGENABLE = false

    /**
     * Check if the CPU architecture is x86
     */
    private fun checkIfCPUx86(): Boolean {
        //1. Check CPU architecture: arm or x86
        return getSystemProperty("ro.product.cpu.abi", "arm").contains("x86")
    }

    private fun getSystemProperty(key: String, defaultValue: String): String {
        var value = defaultValue
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val get: Method = clazz.getMethod("get", String::class.java, String::class.java)
            value = get.invoke(clazz, key, "") as String
        } catch (e: Exception) {
            if (LOGENABLE) {
                Log.d("getSystemProperty", "key = " + key + ", error = " + e.message)
            }
        }
        if (LOGENABLE) {
            Log.d("getSystemProperty", "$key = $value")
        }
        return value
    }


}
