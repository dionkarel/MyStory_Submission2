package com.example.mystory.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.mystory.databinding.ActivityAddStoryBinding
import com.example.mystory.ui.viewmodel.AddStoryViewModel
import com.example.mystory.util.Util.createCustomTempFile
import com.example.mystory.util.Util.reduceFileImage
import com.example.mystory.util.Util.rotateBitmap
import com.example.mystory.util.Util.uriToFile
import com.example.mystory.util.ViewModelFactory
import com.example.mystory.util.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var getFile: File? = null
    private var userLocation: Location? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupAction()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupAction() {
        binding.btOpenCamera.setOnClickListener {
            startCamera()
        }

        binding.btOpenFile.setOnClickListener {
            openFile()
        }

        binding.btUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.edDesc.text
                .toString()
                .trim()
                .toRequestBody("text/plain".toMediaType())
            val lat = userLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
            val lon = userLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipartBody: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            uploadStory(imageMultipartBody, description, lat, lon)
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                "Masukkan gambarnya dulu ya!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadStory(
        imageMultipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        addStoryViewModel.getUserToken().observe(
            this@AddStoryActivity
        ) { it ->
            if (it.token != "") {
                addStoryViewModel.uploadImage(it.token, imageMultipartBody, description, lat, lon)
                    .observe(
                        this@AddStoryActivity
                    ) {
                        if (it != null) {
                            when (it) {

                                is Result.Loading -> {
                                    showLoading(true)
                                }

                                is Result.Success -> {
                                    showLoading(false)
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Berhasil!")
                                        setMessage("Cerita kamu berhasil diunggah!!.")
                                        setPositiveButton("Lanjut") { _, _ ->
                                            val intent = Intent(
                                                this@AddStoryActivity,
                                                MainActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                }

                                is Result.Error -> {
                                    showLoading(false)
                                    Toast.makeText(
                                        this@AddStoryActivity,
                                        it.error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun openFile() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(
            intent,
            "Pilih gambar"
        )
        launcherIntentFile.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.mystory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            val myFile = uriToFile(
                selectedImage,
                this@AddStoryActivity
            )
            getFile = myFile

            binding.img.setImageURI(selectedImage)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(
                    getFile?.path,
                )
            )
            binding.img.setImageBitmap(result)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}