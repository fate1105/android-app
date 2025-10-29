package com.example.angiday.ui.share

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.session.SessionManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ShareActivity : AppCompatActivity() {

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val tvShare = findViewById<TextView>(R.id.tvShare)
        val imgFood = findViewById<ImageView>(R.id.imgFoodShare)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmShare)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // Lấy dữ liệu chia sẻ từ Intent
        val sharedText = intent.getStringExtra(android.content.Intent.EXTRA_TEXT)
        val imageUriString = intent.getStringExtra(android.content.Intent.EXTRA_STREAM)
        imageUri = imageUriString?.let { Uri.parse(it) }

        tvShare.text = if (!sharedText.isNullOrEmpty()) {
            "📋 Nội dung được chia sẻ:\n\n$sharedText"
        } else {
            "⚠️ Không có nội dung nào được chia sẻ!"
        }

        if (imageUri != null) {
            imgFood.setImageURI(imageUri)
        }

        // Xác nhận chia sẻ
        btnConfirm.setOnClickListener {
            val session = SessionManager(this)
            val userId = session.getUserId()
            val foodId = intent.getLongExtra("food_id", -1L)

            lifecycleScope.launch {
                val dao = AppDatabase.get(this@ShareActivity).userBehaviorDao()
                dao.insert(
                    UserBehaviorEntity(
                        userId = userId.toInt(),
                        foodId = foodId.toInt(),
                        behaviorType = "shared"
                    )
                )
            }

            // Kiểm tra quyền ghi External Storage (Android < 10)
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            } else {
                imageUri?.let { saveImageToExternal(it) }
            }

            Toast.makeText(this, "Đã lưu ảnh chia sẻ!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Hủy chia sẻ
        btnCancel.setOnClickListener {
            finish()
        }
    }

    // ✅ Hàm lưu ảnh vào External Storage
    private fun saveImageToExternal(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val picturesDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            val appDir = File(picturesDir, "AngidayShared")

            if (!appDir.exists()) appDir.mkdirs()

            val outFile = File(appDir, "shared_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(outFile)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            Toast.makeText(
                this,
                "Ảnh đã lưu tại: ${outFile.absolutePath}",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lưu ảnh thất bại!", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Nếu không muốn quyền ngoài, có thể dùng cache
    private fun saveImageToCache(uri: Uri) {
        try {
            val cacheFile = File(cacheDir, "temp_shared_${System.currentTimeMillis()}.jpg")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(cacheFile).use { output -> input.copyTo(output) }
            }
            Toast.makeText(
                this,
                "Ảnh lưu tạm trong cache: ${cacheFile.absolutePath}",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Xử lý khi người dùng cấp quyền
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imageUri?.let { saveImageToExternal(it) }
        } else {
            Toast.makeText(this, "Không có quyền ghi bộ nhớ ngoài!", Toast.LENGTH_SHORT).show()
        }
    }
}
