package com.github.neckster.imagepicker.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.neckster.imagepicker.ImagePicker
import com.github.neckster.imagepicker.sample.databinding.ActivityMainBinding
import com.github.neckster.imagepicker.sample.databinding.ContentCameraOnlyBinding
import com.github.neckster.imagepicker.sample.databinding.ContentGalleryOnlyBinding
import com.github.neckster.imagepicker.sample.databinding.ContentProfileBinding
import com.github.neckster.imagepicker.sample.util.FileUtil
import com.github.neckster.imagepicker.sample.util.IntentUtil
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentProfileBinding: ContentProfileBinding
    private lateinit var contentGalleryOnlyBinding: ContentGalleryOnlyBinding
    private lateinit var contentCameraOnlyBinding: ContentCameraOnlyBinding

    companion object {

        private const val GITHUB_REPOSITORY = "https://github.com/Neckster/ImagePicker"

        private const val PROFILE_IMAGE_REQ_CODE = 101
        private const val GALLERY_IMAGE_REQ_CODE = 102
        private const val CAMERA_IMAGE_REQ_CODE = 103
    }

    private var mCameraFile: File? = null
    private var mGalleryFile: File? = null
    private var mProfileFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.contentMainInclude.run {
            contentProfileBinding = contentProfileInclude
            contentGalleryOnlyBinding = contentGalleryOnlyInclude
            contentCameraOnlyBinding = contentCameraOnlyInclude
        }

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        contentProfileBinding.imgProfile.setDrawableImage(R.drawable.ic_person, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_github -> {
                IntentUtil.openURL(this, GITHUB_REPOSITORY)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun pickProfileImage(view: View) {
        ImagePicker.with(this)
            // Crop Square image
            .cropSquare()
            .setImageProviderInterceptor { imageProvider -> // Intercept ImageProvider
                Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
            }
            // Image resolution will be less than 512 x 512
            .maxResultSize(512, 512)
            .start(
                registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { activityResult ->
                    onActivityResultHandler(
                        PROFILE_IMAGE_REQ_CODE,
                        activityResult.resultCode,
                        activityResult.data
                    )
                }
            )

        /*
        * mProfileFile = file
                    imgProfile.setLocalImage(file, true)*/
    }

    fun pickGalleryImage(view: View) {
        ImagePicker.with(this)
            // Crop Image(User can choose Aspect Ratio)
            .crop()
            // User can only select image from Gallery
            .galleryOnly()

            .galleryMimeTypes(  //no gif images at all
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            // Image resolution will be less than 1080 x 1920
            .maxResultSize(1080, 1920)
            .start(
                registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { activityResult ->
                    onActivityResultHandler(
                        GALLERY_IMAGE_REQ_CODE,
                        activityResult.resultCode,
                        activityResult.data
                    )
                }
            )
    }

    fun pickCameraImage(view: View) {
        ImagePicker.with(this)
            // User can only capture image from Camera
            .cameraOnly()
            // Image size will be less than 1024 KB
            .compress(1024)
            .saveDir(Environment.getExternalStorageDirectory())
            // .saveDir(Environment.getExternalStorageDirectory().absolutePath+File.separator+"ImagePicker")
            // .saveDir(getExternalFilesDir(null)!!)
            .start(
                registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { activityResult ->
                    onActivityResultHandler(
                        CAMERA_IMAGE_REQ_CODE,
                        activityResult.resultCode,
                        activityResult.data
                    )
                }
            )
    }

    private fun onActivityResultHandler(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                Log.e("TAG", "Path:${ImagePicker.getFilePath(data)}")
                // File object will not be null for RESULT_OK
                val file = ImagePicker.getFile(data)!!
                when (requestCode) {
                    PROFILE_IMAGE_REQ_CODE -> {
                        mProfileFile = file
                        contentProfileBinding.imgProfile.setLocalImage(file, true)
                    }
                    GALLERY_IMAGE_REQ_CODE -> {
                        mGalleryFile = file
                        contentGalleryOnlyBinding.imgGallery.setLocalImage(file)
                    }
                    CAMERA_IMAGE_REQ_CODE -> {
                        mCameraFile = file
                        contentCameraOnlyBinding.imgCamera.setLocalImage(file, false)
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showImageCode(view: View) {
        val resource = when (view) {
            contentProfileBinding.imgProfileCode -> R.drawable.img_profile_code
            contentCameraOnlyBinding.imgCameraCode -> R.drawable.img_camera_code
            contentGalleryOnlyBinding.imgGalleryCode -> R.drawable.img_gallery_code
            else -> 0
        }
        ImageViewerDialog.newInstance(resource).show(supportFragmentManager, "")
    }

    fun showImage(view: View) {
        val file = when (view) {
            contentProfileBinding.imgProfile -> mProfileFile
            contentCameraOnlyBinding.imgCamera -> mCameraFile
            contentGalleryOnlyBinding.imgGallery -> mGalleryFile
            else -> null
        }

        file?.let {
            IntentUtil.showImage(this, file)
        }
    }

    fun showImageInfo(view: View) {
        val file = when (view) {
            contentProfileBinding.imgProfileInfo -> mProfileFile
            contentCameraOnlyBinding.imgCameraInfo -> mCameraFile
            contentGalleryOnlyBinding.imgGalleryInfo -> mGalleryFile
            else -> null
        }

        AlertDialog.Builder(this)
            .setTitle("Image Info")
            .setMessage(FileUtil.getFileInfo(file))
            .setPositiveButton("Ok", null)
            .show()
    }
}
