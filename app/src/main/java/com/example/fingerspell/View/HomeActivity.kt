package com.example.fingerspell.View

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fingerspell.R
import com.example.fingerspell.data.Finger
import com.example.fingerspell.databinding.ActivityHomeBinding
import com.example.fingerspell.getImageUri
import com.example.fingerspell.model.ReviewAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ReviewAdapter
    private val list = ArrayList<Finger>()
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadData()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_camera -> {
                    checkPermissionAndStartCamera()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ReviewAdapter(list)
        binding.rvFinger.layoutManager = GridLayoutManager(this, 2)
        binding.rvFinger.setHasFixedSize(true)
        binding.rvFinger.adapter = adapter
    }

    private fun loadData() {
        list.addAll(getListFinger())
        adapter.notifyDataSetChanged()
    }

    private fun getListFinger(): ArrayList<Finger> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataPhoto = resources.obtainTypedArray(R.array.photo)
        val listFinger = ArrayList<Finger>()
        for (i in dataName.indices) {
            val finger = Finger(dataName[i], dataPhoto.getResourceId(i, -1))
            listFinger.add(finger)
        }
        dataPhoto.recycle()
        return listFinger
    }

    private fun checkPermissionAndStartCamera() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            // Implement your logic to display the image
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
