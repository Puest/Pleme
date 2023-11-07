package com.me.pleme

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.me.pleme.navigation.AddPhotoActivity
import com.me.pleme.navigation.AlarmFragment
import com.me.pleme.navigation.DetailViewFragment
import com.me.pleme.navigation.UserFragment
import com.me.pleme.navigation.BoardFragment
import android.Manifest;
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.me.pleme.navigation.FcmPush

@Suppress("DEPRECATION")
class Main_page1 : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        setToolbarDefault()
        when(p0.itemId){
            R.id.action_favorite_alarm -> {
                val alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                return true
            }
            R.id.action_home -> {
                val detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                return true
            }
            R.id.action_add_board -> {
                val boardFragment = BoardFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,boardFragment).commit()
                return true
            }
            R.id.action_add_photo -> {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    // 권한 체크해서 권한이 있을 때
                    startActivity(Intent(this,AddPhotoActivity::class.java))
                }
                return true
            }
            R.id.action_account -> {
                val userFragment = UserFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid

                bundle.putString("destinationUid",uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }
    fun setToolbarDefault(){
        findViewById<TextView>(R.id.toolbar_username).visibility = View.GONE
        findViewById<ImageView>(R.id.toolbar_btn_back).visibility = View.GONE
        findViewById<ImageView>(R.id.icon_1).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.icon_2).visibility = View.VISIBLE
    }

    fun registerPushToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page1)

        val bottom_navigation : BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        //기본 스크린 값 지정
        bottom_navigation.selectedItemId = R.id.action_home
        registerPushToken()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == RESULT_OK){
            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask{task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String,Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }
    }
}
/*
//        콕
        var poke:ImageButton=findViewById(R.id.poke_btn)
        poke.setOnClickListener {
            var con_2 = dialog2(this)
            con_2.showDialog()
        }
//        메시지
        var note:ImageButton=findViewById(R.id.note_btn)
        note.setOnClickListener {
            var con_3 = dialog3(this)
            con_3.showDialog()
        }
//        알람
        var alarm_1 : ImageButton = findViewById(R.id.alarm)
        alarm_1.setOnClickListener {
            var intent = Intent(this,Main_page2::class.java)
            startActivity(intent)
        }
//        친구추가
var f_add : Button = findViewById(R.id.set_btn2)
f_add.visibility = View.GONE
f_add.setOnClickListener{
    var f_add_dialog = add_f(this)
    f_add_dialog.showDialog()
}
//        설정 들어가기
var setting_in : Button = findViewById(R.id.set_btn)
setting_in.visibility = View.GONE
setting_in.setOnClickListener {
    var setting_dialog = setting(this)
    setting_dialog.showDialog()
}

//      플러스 버튼
var se_btn : Button = findViewById(R.id.se_btn)
se_btn.setOnClickListener {
    if(f_add.getVisibility()==View.GONE){
        f_add.visibility = View.VISIBLE
    }
    else{
        f_add.visibility = View.GONE
    }
    if(setting_in.getVisibility()==View.GONE){
        setting_in.visibility = View.VISIBLE
    }
    else{
        setting_in.visibility = View.GONE
    }
}
//        알람창으로 이동
var state: ImageButton = findViewById(R.id.alarm)
state.setOnClickListener {
    var intent = Intent(this,Main_page2::class.java)
    startActivity(intent)
}

//        플러스 버튼
var set_btn : Button = findViewById(R.id.se_btn)
set_btn.setOnClickListener {
    if(f_add.getVisibility() == View.GONE ){

    }
    else{
        f_add.visibility = View.GONE
    }
    if(setting_in.getVisibility() == View.GONE){
        setting_in.visibility = View.VISIBLE
    }
    else{
        setting_in.visibility = View.GONE
    }
}


var stat_btn1 : ImageButton = findViewById(R.id.state_btn)
stat_btn1.setOnClickListener{
    var intent  = Intent(this, Main_page2::class.java)
    startActivity(intent)
}
var ala_1 : ImageButton = findViewById(R.id.alarm)
ala_1.setOnClickListener {
    var intent = Intent(this, Main_page2::class.java)
    startActivity(intent)
}
 */
