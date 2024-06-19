package com.example.hiweather_aos.post

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class Firestore : MultiDexApplication() {
    companion object {
        lateinit var auth : FirebaseAuth // 이 객체를 다른 클래스에서도 접근가능해야한다. - mani fest:name 설정
        var email : String? = null
        //firestore
        lateinit var db : FirebaseFirestore
        lateinit var storage : FirebaseStorage

        // 인증된 사용자인지 확인
        fun checkAuth(): Boolean {
            val currentUser = auth.currentUser
            return if (currentUser != null) {
                email = currentUser.email
                true
            } else {
                false
            }
        }
    }

    override fun onCreate(){
        super.onCreate()
        auth = Firebase.auth
        Log.d("post", "Firebase Auth initialized")
        db = FirebaseFirestore.getInstance() // firestore 초기화
        Log.d("post", "Firestore initialized: $db")
        storage  = Firebase.storage
    }
}