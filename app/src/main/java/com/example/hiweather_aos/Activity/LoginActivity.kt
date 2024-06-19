package com.example.hiweather_aos.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.MainActivity
import com.example.hiweather_aos.R
import com.example.hiweather_aos.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // 로그인 버튼
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            signIn(email, password)
        }

        // 회원가입 페이지로 이동
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


        // google login
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            Log.d("loginact","account1 : ${task.toString()}")
            try{
                val account = task.getResult(ApiException::class.java)
                val crendential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(crendential)
                    .addOnCompleteListener(this){task ->
                        // 로그인 성공 시 실행
                        if(task.isSuccessful){
                            Toast.makeText(baseContext,"구글 로그인 성공",Toast.LENGTH_SHORT).show()
                            Log.d("loginact", "구글 로그인 성공\n${auth.currentUser?.email} 님 안녕하세요!")
                            finish()
                        }
                        else{ // 로그인 실패 시 실행
                            Toast.makeText(baseContext,"구글 로그인 실패",Toast.LENGTH_SHORT).show()
                            Log.d("loginact", "구글 로그인 실패")
                        }
                    }
            }catch (e: ApiException){ // APIException은 이미 지정된 exception말고 custom한 exception을 만들어서 쓰고 싶을때 사용
                Toast.makeText(baseContext,"구글 로그인 Exception : ${e.printStackTrace()},${e.statusCode}",Toast.LENGTH_SHORT).show()
                Log.d("loginact", "구글 로그인 Exception : ${e.message}, ${e.statusCode}")
            }
        }

        // 구글 로그인 로직
        binding.btnGoogleLogin.setOnClickListener {
            Log.d("loginact", "클릭")
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build()
            val signInIntent = GoogleSignIn.getClient(this,gso).signInIntent
            requestLauncher.launch(signInIntent)
        }

        // 트위터 로그인 로직
        binding.btnTwitterLogin.setOnClickListener {

        }

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            Toast.makeText(
                baseContext, "로그아웃합니다.",
                Toast.LENGTH_SHORT
            ).show()
            auth.signOut()
            updateUI(null)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }

    private fun signIn(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인에 성공하였습니다.\n${auth.currentUser?.email} 님 안녕하세요!",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(auth.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext, "로그인에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) { // 로그인 상태
            binding.tvUsername.text = "${user.email} 님 안녕하세요!"
            binding.etEmail.visibility = View.GONE
            binding.etPassword.visibility = View.GONE
            binding.btnLogin.visibility = View.GONE
            binding.btnRegister.visibility = View.GONE
            binding.btnGoogleLogin.visibility = View.GONE
            binding.btnTwitterLogin.visibility = View.GONE
            binding.btnLogout.visibility = View.VISIBLE
        } else { // 로그아웃 상태
            binding.tvUsername.visibility = View.INVISIBLE
            binding.etPassword.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnRegister.visibility = View.VISIBLE
            binding.btnGoogleLogin.visibility = View.VISIBLE
            binding.btnTwitterLogin.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE
        }
    }
}
