package com.bundletool.demo

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.modelsplitapks.handleAppBundle
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        val btButton = findViewById<Button>(R.id.bt_split);
        btButton.setOnClickListener{
            handleAppBundle("/storage/emulated/0/aab/test.apks",
                this,
                connectDevice = false,
                preCallBack = {
                    Log.e("handleAppBundle","preCallBack")
                },
                inCallBack = {
                    Log.e("handleAppBundle","inCallBack")
                },
                postCallBack = {
                    Log.e("handleAppBundle","postCallBack")
                }
            )
        }

        val btCheck = findViewById<Button>(R.id.bt_check)
        btCheck.setOnClickListener{
            handleAppBundle("/storage/emulated/0/aab/my_app.apks", this)
        }


    }

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                //自动获取权限
                autoObtainLocationPermission()
            } else {
                //跳转到设置界面引导用户打开
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + getPackageName())
                startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE)
            }
        } else {
            //自动获取权限
            autoObtainLocationPermission()
        }
    }

    private fun autoObtainLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100)
        }
    }

    companion object{
        private const val REQUEST_EXTERNAL_STORAGE = 1
    }
}