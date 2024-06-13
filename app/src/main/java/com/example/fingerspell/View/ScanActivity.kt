package com.example.fingerspell.View

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fingerspell.R
import com.example.fingerspell.databinding.ActivityScanBinding
import com.example.fingerspell.model.ImageClasifier
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.IOException

class ScanActivity : AppCompatActivity(), ImageClasifier.ClassifierListener {
    private lateinit var binding: ActivityScanBinding
    private lateinit var imageClassifier: ImageClasifier
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Scan"

        imageClassifier = ImageClasifier(
            context = this,
            classifierListener = this
        )

        if (intent.hasExtra(EXTRA_IMAGE_URI)) {
            val uriString = intent.getStringExtra(EXTRA_IMAGE_URI)
            uriString?.let {
                currentImageUri = Uri.parse(it)
                currentImageUri?.let { uri ->
                    displayImage(uri)
                }
            }
        }

        binding.ScanButton.setOnClickListener {
            currentImageUri?.let { uri ->
                classifyImage(uri)
            }
        }
    }

    private fun displayImage(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            binding.previewImageView.setImageURI(uri)
        } catch (e: IOException) {
            e.printStackTrace()
            onError("Failed to display image")
        }
    }

    private fun classifyImage(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            imageClassifier.classifyImage(uri)
            showLoading(true)
        } catch (e: IOException) {
            e.printStackTrace()
            onError("Failed to process image")
        }
    }

    override fun onError(error: String) {
        showLoading(false)
        binding.resultTextView.text = error
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        showLoading(false)
        if (results.isNullOrEmpty()) {
            binding.resultTextView.text = getString(R.string.error_message)
        } else {
            val resultString = results.joinToString { it.categories[0].label }
            binding.resultTextView.text = resultString
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}
