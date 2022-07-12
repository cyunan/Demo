package com.modelsplitapks

import android.os.Build
import android.util.Log
import java.lang.reflect.Method
import kotlin.math.log

/**
 *    author : ChenYuNan
 *    date   : 2022/7/8
 *    desc   :
 */
object DeviceInformation {
    val mApiVersion: Int = getApiVersion()
    val mIsX86: Boolean = checkIfCPUx86()

    private fun getApiVersion() = Build.VERSION.SDK_INT
    /**
     * Check if the CPU architecture is x86
     */
    private fun checkIfCPUx86(): Boolean {
        return getSystemProperty("ro.product.cpu.abi", "arm").contains("x86")
    }

    private fun getSystemProperty(key: String, defaultValue: String): String {
        var value = defaultValue
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val get: Method = clazz.getMethod("get", String::class.java, String::class.java)
            value = get.invoke(clazz, key, "") as String
        } catch (e: Exception) {
            Log.d("",e.message.toString())

        }

        return value
    }


}
