package com.example.fingerspell.View

import android.Manifest
import android.content.Intent
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
import com.example.fingerspell.model.ReviewAdapter

class HomeActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ReviewAdapter
    private val list = ArrayList<Finger>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        setupRecyclerView()
        loadData()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_Camera -> {
                    startCamera()
                    true
                }
                else -> {
                    startHistory()
                    true
                }
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
    private fun startCamera(){
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
    private fun startHistory(){
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
