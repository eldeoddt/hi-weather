package com.example.hiweather_aos.post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiweather_aos.R
import com.example.hiweather_aos.databinding.FragmentPostBinding
import com.google.firebase.firestore.Query

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainFab.setOnClickListener {
            // 인증된 경우
            if (Firestore.checkAuth()) {
                startActivity(Intent(requireContext(), AddActivity::class.java))
            }
            // 인증되지 않은 경우
            else {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 로그인한 사용자일 때만 실행
        if (Firestore.checkAuth()) {
            Firestore.db.collection("comments")
                .orderBy("date_time", Query.Direction.DESCENDING) // date_time 기준으로 정렬
                .get()
                // 추가 성공한 경우
                .addOnSuccessListener { result ->
                    val itemList = mutableListOf<PostItemData>()
                    for (document in result) {
                        val item = document.toObject(PostItemData::class.java)
                        item.docId = document.id
                        itemList.add(item)
                    }
                    binding.rvPost.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvPost.adapter = PostAdapter(requireContext(), itemList) // adapter

//                    Toast.makeText(requireContext(), "데이터 불러오기 성공", Toast.LENGTH_SHORT).show()
                    Log.d("post", "Data successfully get")
                }
                // 실패한 경우
                .addOnFailureListener { e ->
                    Log.e("post", "Error adding document", e)
                    Toast.makeText(requireContext(), "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
