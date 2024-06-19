package com.example.hiweather_aos.post

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hiweather_aos.databinding.ItemPostBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

class PostAdapter (val context: Context, val itemList: MutableList<PostItemData>): RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(ItemPostBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val data = itemList[position]

        holder.binding.run {
            tvPostId.text = data.email
            tvPostDate.text = data.date_time?.toDateString() ?: "날짜 없음"
            tvPostComment.text = data.comments
            tvPostWeather.text = "날씨 : ${data.weather}"
            Log.d("post adapter", "time :${data.date_time?.toDateString()}, weather:${data.weather}")

            // date_time을 특정 포맷으로 변환하여 TextView에 설정
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = data.date_time?.toDate()
            val dateString = dateFormat.format(date)

            tvPostDate.text = dateString
        }

        // 이미지 가져오기
        val imageRef = Firestore.storage.reference.child("images/${data.docId}.jpg")
        imageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("post adapter", "img :${holder.binding.ivPostImg}")
                holder.binding.ivPostImg.visibility = View.VISIBLE
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.ivPostImg)
            } else {
                holder.binding.ivPostImg.visibility = View.GONE
            }
        }.addOnFailureListener { e ->
            holder.binding.ivPostImg.visibility = View.GONE
            Log.e("post adapter", "Image download failed: ${e.message}", e)
            e.printStackTrace()
        }
    }

    private fun Timestamp.toDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(this.toDate())
    }
}