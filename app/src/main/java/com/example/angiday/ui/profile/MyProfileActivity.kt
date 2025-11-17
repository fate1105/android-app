package com.example.angiday.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.session.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MyProfileActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var capturedBitmap: Bitmap? = null

    // Launcher chọn ảnh từ thư viện
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        tempImgView?.setImageURI(uri)
    }

    // Launcher chụp ảnh
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                capturedBitmap = it
                tempImgView?.setImageBitmap(it)
            }
        }
    }

    private var tempImgView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val tvName = findViewById<TextView>(R.id.tvName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val layoutShared = findViewById<LinearLayout>(R.id.layoutSharedFoods)
        val btnAddPost = findViewById<FloatingActionButton>(R.id.btnAddPost)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)

        val session = SessionManager(this)
        val userName = session.getUserName()
        val userEmail = session.getUserEmail()
        val userId = session.getUserId()

        tvName.text = userName
        tvEmail.text = userEmail
        topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        fun loadSharedPosts() {
            lifecycleScope.launch {
                val db = AppDatabase.get(this@MyProfileActivity)
                val sharedFoods = db.userBehaviorDao().getSharedFoodsWithDetail(userId.toInt())
                layoutShared.removeAllViews()

                if (sharedFoods.isEmpty()) {
                    val tvEmpty = TextView(this@MyProfileActivity).apply {
                        text = "Chưa có món nào được chia sẻ 🍽️"
                        setTextColor(ContextCompat.getColor(this@MyProfileActivity, R.color.colorOnSurface))
                        textSize = 16f
                        setPadding(20, 20, 20, 20)

                    }
                    layoutShared.addView(tvEmpty)
                } else {
                    sharedFoods.forEach { foodWithRelations ->
                        val itemView = layoutInflater.inflate(R.layout.item_shared_food, layoutShared, false)
                        val img = itemView.findViewById<ImageView>(R.id.imgShared)
                        val title = itemView.findViewById<TextView>(R.id.tvSharedTitle)
                        val desc = itemView.findViewById<TextView>(R.id.tvSharedDesc)
                        val btnDelete = itemView.findViewById<ImageView>(R.id.btnDeletePost)

                        // Hiển thị ảnh từ file hoặc resource
                        val imagePath = foodWithRelations.food.imageRes
                        if (!imagePath.isNullOrEmpty()) {
                            val file = File(imagePath)
                            if (file.exists()) img.setImageURI(Uri.fromFile(file))
                            else {
                                val resId = resources.getIdentifier(imagePath, "drawable", packageName)
                                if (resId != 0) img.setImageResource(resId)
                            }
                        }

                        title.text = foodWithRelations.food.title
                        desc.text = foodWithRelations.food.desc ?: "Không có mô tả"

                        btnDelete.setOnClickListener {
                            AlertDialog.Builder(this@MyProfileActivity)
                                .setTitle("Gỡ bài chia sẻ")
                                .setMessage("Bạn có chắc muốn xóa bài chia sẻ này không?")
                                .setPositiveButton("Xóa") { _, _ ->
                                    lifecycleScope.launch {
                                        db.userBehaviorDao().deleteBehavior(
                                            userId = userId.toLong(),
                                            foodWithRelations.food.id,
                                            "shared"
                                        )
                                        loadSharedPosts()
                                        Toast.makeText(this@MyProfileActivity, "Đã gỡ bài chia sẻ!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton("Hủy", null)
                                .show()
                        }
                        layoutShared.addView(itemView)
                    }
                }
            }
        }

        loadSharedPosts()

        // ➕ Tạo bài chia sẻ mới
        btnAddPost.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_share, null)
            val edtName = dialogView.findViewById<EditText>(R.id.edtFoodName)
            val edtDesc = dialogView.findViewById<EditText>(R.id.edtFoodDesc)
            val imgPreview = dialogView.findViewById<ImageView>(R.id.imgPreview)
            val btnSelectImage = dialogView.findViewById<Button>(R.id.btnSelectImage)
            tempImgView = imgPreview

            btnSelectImage.setOnClickListener {
                val options = arrayOf("Chọn từ thư viện", "Chụp ảnh mới")
                AlertDialog.Builder(this)
                    .setTitle("Chọn ảnh món ăn")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> pickImageLauncher.launch("image/*")
                            1 -> {
                                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED
                                ) {
                                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
                                } else {
                                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    takePhotoLauncher.launch(intent)
                                }
                            }
                        }
                    }
                    .show()
            }

            AlertDialog.Builder(this)
                .setTitle("Tạo món chia sẻ mới")
                .setView(dialogView)
                .setPositiveButton("Lưu") { _, _ ->
                    val name = edtName.text.toString().ifEmpty { "Món ăn mới" }
                    val desc = edtDesc.text.toString()

                    // Lưu ảnh đã chọn/chụp vào thư mục app
                    val imagePath = when {
                        selectedImageUri != null -> saveUriToFile(selectedImageUri!!)
                        capturedBitmap != null -> saveBitmapToFile(capturedBitmap!!)
                        else -> null
                    }

                    lifecycleScope.launch {
                        val db = AppDatabase.get(this@MyProfileActivity)
                        val newFood = com.example.angiday.model.entity.FoodEntity(
                            title = name,
                            desc = desc,
                            imageRes = imagePath,
                            instructions = null,
                            calories = null,
                            youtubeId = null,
                            categoryId = null
                        )
                        val foodId = db.foodDao().insert(newFood)
                        // Dòng insert behavior
                        db.userBehaviorDao().insert(
                            UserBehaviorEntity(
                                userId = userId,
                                foodId = foodId,
                                behaviorType = "shared"
                            )
                        )
                        loadSharedPosts()
                        Toast.makeText(this@MyProfileActivity, "Đã chia sẻ món: $name", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    // Lưu ảnh chụp vào file app
    private fun saveBitmapToFile(bitmap: Bitmap): String {
        val file = File(filesDir, "photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
        return file.absolutePath
    }

    // Lưu ảnh từ thư viện vào file app
    private fun saveUriToFile(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri) ?: return ""
        val file = File(filesDir, "image_${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { out -> inputStream.copyTo(out) }
        return file.absolutePath
    }
}
