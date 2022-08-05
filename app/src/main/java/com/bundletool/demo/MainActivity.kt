package com.bundletool.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bundletool.modelprotocol.getFileSize
import com.bundletool.modelprotocol.parseDependencies
import com.bundletool.modelprotocol.parseManifests
import com.modelsplitapks.dao.SplitAPKInstaller.handleAppBundle
import com.xiasuhuei321.loadingdialog.view.LoadingDialog

class MainActivity : AppCompatActivity() {
    lateinit var dialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        val btButton = findViewById<Button>(R.id.bt_split)
        dialog = LoadingDialog(this)

        btButton.setOnClickListener{
            dialog.setLoadingText("安装中")
                .setSuccessText("安装成功")
                .setFailedText("安装失败")
                .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                .show()
            handleAppBundle("/storage/emulated/0/aab/my_app.apks",
                this,
                connectDevice = false){
                preInstall {
                }
                inInstall {
                    Log.e("handleAppBundle","inCallBack")
                }
                successInstall {
                    runOnUiThread { dialog.loadSuccess() }
                }
                errorInstall {
                    runOnUiThread { dialog.loadFailed() }
                }
            }
        }

        val btCheck = findViewById<Button>(R.id.bt_check)
        btCheck.setOnClickListener{
            dialog.setLoadingText("安装中")
                .setSuccessText("安装成功")
                .setFailedText("安装失败")
                .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                .show()
            handleAppBundle("/storage/emulated/0/aab/my_app.apks",
                this,
                connectDevice = true){
                preInstall {

                }
                inInstall {
                    Log.e("handleAppBundle","inCallBack")
                }
                successInstall {
                    runOnUiThread { dialog.loadSuccess() }
                }
                errorInstall {
                    runOnUiThread { dialog.loadFailed() }
                }
            }
        }
        val btTest = findViewById<Button>(R.id.bt_dpi)
        btTest.setOnClickListener {
            parseDependencies()
//            println(getFileSize("/storage/emulated/0/aab/41750.aab"))
//            parseBundleConfig()
//            parseManifests()

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

    /**
     * 1. 找到aab下载位置
     * 2. 根据对应的数据包类型进行安装
     * 3. 完善安装过程的回调
     */



}