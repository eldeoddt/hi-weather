package com.example.hiweather_aos.post

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hiweather_aos.databinding.ActivityAddBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    lateinit var uri: Uri
    lateinit var auth: FirebaseAuth
    lateinit var filePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            // 사용자 로그인 처리 필요
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            binding.tvId.text = "작성자 : ${currentUser.email}" // 로그인한 사용자 이메일 출력
        }

        // 이미지뷰에 이미지 로드
        // 파일 경로를 고정된 값으로 설정
        filePath = File(filesDir, "selected_image.jpg").absolutePath
        loadImageFromInternalStorage(this, filePath, binding.ivPostPrev)

        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == android.app.Activity.RESULT_OK) {
                binding.addImageView.visibility = View.VISIBLE
                binding.tvPostCurrent.visibility = View.VISIBLE
                Glide
                    .with(applicationContext)
                    .load(it.data?.data)
                    .override(200, 150)
                    .into(binding.addImageView)
                uri = it.data?.data!!

                // 이미지 저장하기 기기내
                val fileName = "selected_image.jpg"
                filePath = saveImageToInternalStorage(this, uri, fileName)
                Toast.makeText(this, "기기 내 이미지 저장 완료: $filePath", Toast.LENGTH_SHORT).show()

            }
        }
        binding.uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
//            binding.ivPostPrev.visibility = View.GONE
//            binding.tvPostPrev.visibility = View.GONE

        }

        binding.saveButton.setOnClickListener {
            if (binding.input.text.isNotEmpty()) {
//                Toast.makeText(this, "save btn clicked", Toast.LENGTH_SHORT).show()
                Log.d("post", "Save button clicked")

                // 로그인 이메일, 스타, 한줄평, 입력 시간 저장하기
// val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm ", Locale.getDefault())
// val timestamp = Timestamp(dateFormat.parse("yyyy-MM-dd HH:mm "))
                val currentTimeStamp = Timestamp(Date())
                val data = mapOf(
                    "email" to Firestore.email,
                    "weather" to binding.weather.text.toString(),
                    "comments" to binding.input.text.toString(),
                    "date_time" to currentTimeStamp
                )

                Log.d("post", "Data to save: $data")
                Log.d("post", "Firestore instance: ${Firestore.db}")

                Firestore.db.collection("comments")
                    .add(data)
                    // 추가 성공한 경우
                    .addOnSuccessListener {
                        Toast.makeText(this, "데이터 저장 성공", Toast.LENGTH_SHORT).show()
                        uploadImage(it.id)
                        Log.d("post", "Data successfully saved")

                        finish() // board activity로 돌아간다.
                    }
                    // 실패한 경우
                    .addOnFailureListener { e ->
                        Log.e("post", "Error adding document", e)
                        Toast.makeText(this, "데이터 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                    // task log
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("post", "Task completed successfully")
                        } else {
                            Log.e("post", "Task failed", task.exception)
                        }
                    }
            }
            // is empty인 경우
            else {
                Toast.makeText(this, "내용을 먼저 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun uploadImage(docId: String) {
        // uri가 초기화되지 않았으면 처리 중단
        if (!::uri.isInitialized) {
            Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val imageRef = Firestore.storage.reference.child("images/${docId}.jpg")

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "이미지 업로드 성공.", Toast.LENGTH_SHORT).show()

        }
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "사진 업로드 실패.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri, fileName: String): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }

    private fun loadImageFromInternalStorage(context: Context, filePath: String, imageView: ImageView) {
        val imgFile = File(filePath)
        if (imgFile.exists()) {
            Glide.with(context)
                .load(imgFile)
                .into(imageView)
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
            binding.tvPostPrev.visibility = View.GONE
        }
    }
}