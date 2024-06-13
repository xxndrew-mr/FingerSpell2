package com.example.fingerspell.View

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fingerspell.R
import com.example.fingerspell.databinding.ActivityScanBinding
import com.example.fingerspell.model.ObjectDetectorHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.enableEdgeToEdge
import java.io.IOException

class ScanActivity : AppCompatActivity(), ObjectDetectorHelper.ClassifierListener {
    private lateinit var binding: ActivityScanBinding
    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private var currentImageUri: Uri? = null

    private val cameraActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CameraActivity.CAMERAX_RESULT) {
            val imageUri = result.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)
            imageUri?.let {
                val uri = Uri.parse(it)
                currentImageUri = uri
                binding.previewImageView.setImageURI(uri)
                classifyImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Scan"

        objectDetectorHelper = ObjectDetectorHelper(
            context = this,
            classifierListener = this
        )

        binding.ScanButton.setOnClickListener {
            showLoading(true)
            startCameraActivity()
        }
    }

    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        cameraActivityLauncher.launch(intent)
    }

    private fun classifyImage(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            Log.d("ScanActivity", "Ukuran bitmap: ${bitmap.width}x${bitmap.height}")
            objectDetectorHelper.classifyImage(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
            onError("Gagal memproses gambar")
        }
    }

    override fun onError(error: String) {
        showLoading(false)
        binding.resultTextView.text = error
        Log.e("ScanActivity", "Kesalahan: $error")
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        showLoading(false)
        if (results.isNullOrEmpty()) {
            binding.resultTextView.text = getString(R.string.error_message)
            Log.d("ScanActivity", "Tidak ada hasil klasifikasi")
        } else {
            val resultString = results.joinToString { it.categories[0].label }
            Log.d("ScanActivity", "Hasil klasifikasi: $resultString")
            binding.resultTextView.text = resultString
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
