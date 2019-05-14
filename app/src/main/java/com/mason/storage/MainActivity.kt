package com.mason.storage

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.async
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt_internal.setOnClickListener(this)
        bt_extra_private.setOnClickListener(this)
        bt_extra_public.setOnClickListener(this)
        bt_extra_public_photo.setOnClickListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 110)
        }
    }

    override fun onClick(v: View?) {
        val photoFile: File
        when (v?.id) {
            R.id.bt_internal -> {//存放图片,发送通知,观察系统图片里是否有图,没有
                photoFile = File(cacheDir, "内部存储.jpg")
                downloadPic(
                    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2150681981,1079538934&fm=27&gp=0.jpg",
                    photoFile
                )
            }
            R.id.bt_extra_private -> {//存放图片,发送通知,观察系统图片里是否有图
                photoFile = File(externalCacheDir, "外部存储私有目录.jpg")//没有
                downloadPic(
                    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2695313341,2343529602&fm=27&gp=0.jpg",
                    photoFile
                )
            }
            R.id.bt_extra_public -> {//存放图片,发送通知,观察系统图片里是否有图,如果不发送广播没有
                photoFile = File(
                    Environment.getExternalStorageDirectory(),
                    "${File.separator + "storage" + File.separator}外部存储自定义文件夹的存储.jpg"
                )
                downloadPic(
                    "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3049633038,3453615417&fm=27&gp=0.jpg",
                    photoFile
                )
            }
            R.id.bt_extra_public_photo -> {//存放图片,不发送通知,观察系统图片里是否有图,如果不发送广播没有
                photoFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "外部存储系统图片目录.jpg"
                )
                downloadPic(
                    "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3821078962,1153323739&fm=27&gp=0.jpg",
                    photoFile, true
                )
            }
        }
    }

    private fun downloadPic(photoUrl: String, pic: File, canSendBroad: Boolean = true) {
        async {
            if (!pic.exists()) {
                if (!pic.parentFile.exists()) {
                    pic.parentFile.mkdirs()
                }
                pic.createNewFile()
            }
            val outStream = FileOutputStream(pic)
            outStream.write(URL(photoUrl).readBytes())
            outStream.flush()
            outStream.close()
            activityUiThread {
                if (canSendBroad) {
                    sendImageBroadcast(pic)
                }
            }
        }


    }

    private fun sendImageBroadcast(imageFile: File) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(imageFile)
        intent.data = uri
        sendBroadcast(intent)//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
        Toast.makeText(this, "图片已保存到$imageFile", Toast.LENGTH_SHORT).show()
    }

}
