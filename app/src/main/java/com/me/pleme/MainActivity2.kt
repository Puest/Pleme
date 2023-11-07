package com.me.pleme

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.w3c.dom.Text
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.me.pleme.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //nullable한 FirebaseAuth 객체 선언
    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //auth 객체 초기화
        auth = FirebaseAuth.getInstance()
        //log_btn 뷰 바인딩
        val logBtn: Button = findViewById(R.id.log_btn)
        logBtn.setOnClickListener {
            signingEmail()
        }

        var text: TextView = findViewById(R.id.text1)

//        로그인 페이지 이동
        val Join: Button = findViewById(R.id.Join)
        Join.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
//        팝업창 조건 설정
        text.setOnClickListener {
            var con_1 = dialog(this)
            con_1.showDialog()
        }

        //편지GIF 실행
        showLetter();
    } //onCreate

    //편지GIF 메서드
    fun showLetter() {
        val imageView: ImageView = findViewById(R.id.imageView2)
        Glide.with(this).load(R.drawable.letter).into(imageView)
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    //signIn(로그인) 함수
    fun signingEmail() {
        //id_text 뷰 바인딩
        var idText: TextView = findViewById(R.id.id_text)
        //pw_text 뷰 바인딩
        var pwText: TextView = findViewById(R.id.pw_text)

        auth?.signInWithEmailAndPassword(idText.text.toString(), pwText.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //로그인(아이디와 패스워드 성공) 처리
                    moveMainPage(task.result.user)
                } else if (task.exception?.message.isNullOrEmpty()) {
                    //에러 메세지 출력 - 로그인 실패(틀릴시)
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    //에러 메세지 출력 - 로그인 실패(틀릴시)
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, Main_page1::class.java))
            finish()
        }
    }
}