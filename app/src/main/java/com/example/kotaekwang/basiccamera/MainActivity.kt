package com.example.kotaekwang.basiccamera

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var cameraPreview : CameraPreview? = null
    private var mCamera : Camera? = null
    private val requestPermission : Int = 0
    private val arrayPemission : Array<String> = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck()

    }

    private fun permissionCheck(){
        var cameraPermission : Int = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
        var writeExternalStoragePermission : Int =
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //if granted, those values are 1

        if(cameraPermission == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED){

            //both permissions are granted
            setupCamera()

        }else{
            ActivityCompat.requestPermissions(this,arrayPemission,requestPermission)
        }
    }

    @Override
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == requestPermission && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            setupCamera()
        }else{
            Toast.makeText(this,"permissions are not granted",Toast.LENGTH_LONG).show()
        }

    }


    private fun setupCamera(){
        Log.d("Camera","Setup")
        if(mCamera == null){
            Log.d("Camera","Open")
            mCamera = Camera.open()
        }

        cameraPreview = CameraPreview(this,mCamera!!)

    }

}
