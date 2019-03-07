package com.example.kotaekwang.basiccamera

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var cameraPreview : CameraPreview? = null
    private var mCamera : Camera? = null
    private val requestPermission : Int = 0
    private val arrayPemission : Array<String> = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val MEDIA_IMAGE_TYPE : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck()

        camera_button.setOnClickListener{takePicture()}
    }

    private fun takePicture(){
        Toast.makeText(this,"TAKEPICTURE",Toast.LENGTH_SHORT).show()
        mCamera?.takePicture(null,null,mPicture)
    }

    private val mPicture = Camera.PictureCallback { data, _ ->

        val pictureFile: File = getOutputMediaFile(MEDIA_IMAGE_TYPE) ?: run {
            Toast.makeText(this@MainActivity,"fail make file",Toast.LENGTH_SHORT).show()
        Log.d("BasicCamera", ("Error creating media file, check storage permissions"))
        return@PictureCallback
        }

        try{
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        }catch (e: FileNotFoundException){
            Log.d("BasicCamera", "File not found: ${e.message}")
        }catch (e: IOException){
            Log.d("BasicCamera", "File not found: ${e.message}")
        }
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
        if(mCamera == null){
            mCamera = Camera.open()
        }

        cameraPreview = CameraPreview(this,mCamera!!)
        camera_frameLayout.addView(cameraPreview)
    }


    private fun getOutputMediaFile(type : Int) : File?{
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"basicCameraApp")
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        Toast.makeText(this@MainActivity,"GETOUT$mediaStorageDir",Toast.LENGTH_SHORT).show()
        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if(!exists()){
                if(!mkdirs()){
                    Toast.makeText(this@MainActivity,"fail create",Toast.LENGTH_SHORT).show()
                    Log.d("BasicCamera", "failed to create directory")
                    return null
                }
            }
        }

        //Create file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp")
    }

    @Override
    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    private fun releaseCamera(){
        mCamera?.release()
        mCamera = null
    }


}
