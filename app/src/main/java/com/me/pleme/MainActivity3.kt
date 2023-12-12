package com.me.pleme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.me.pleme.databinding.ActivityMainBinding

class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //nullable한 FirebaseAuth 객체 선언
    var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        //auth 객체 초기화
        auth = FirebaseAuth.getInstance()
        //join_btn 뷰 바인딩
        val joinBtn : Button = findViewById(R.id.join_btn)
        joinBtn.setOnClickListener {
            signinAndSignup()
        }

        var relogin: OutlineTextView = findViewById(R.id.relogin)
        relogin.setOnClickListener {
            var intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }
    }

    //signUp(회원가입) 함수
    fun signinAndSignup() {
        var joinID : TextView = findViewById(R.id.join_id)
        var joinPW : TextView = findViewById(R.id.join_pw)

        auth?.createUserWithEmailAndPassword(joinID.text.toString(),joinPW.text.toString())
            ?.addOnCompleteListener {   //통신 완료가 된 후 무슨일을 할지
                    task ->
                if(task.isSuccessful){
                    //회원가입(성공적으로 가입 완료 시)
                    moveMainPage(task.result.user)
                } else if(task.exception?.message.isNullOrEmpty()) {
                    //에러 메세지 출력 - 로그인 실패(틀릴시)
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }else {
                    //여기가 실행되는 경우는 이미 db에 해당 이메일과 패스워드가 있는 경우
                    //그래서 계정 생성이 아닌 로그인 함수로 이동
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    //성공적 가입 시 이동 페이지
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity2::class.java))
        }
    }
}