package com.me.pleme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //손가락GIF 실행
        showGood();
    }

    //화면 전환
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val intent = Intent(this, MainActivity2::class.java)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startActivity(intent)
            }
        }
        overridePendingTransition(R.anim.vertical_enter, R.anim.non)
        return super.onTouchEvent(event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.non, R.anim.vertical_back)
    }
//여기 까지

    //손가락 GIF 메서드
    fun showGood() {
        val imageView: ImageView = findViewById(R.id.imageView1)
        Glide.with(this).load(R.drawable.good).into(imageView)
    }
}