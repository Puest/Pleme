package com.me.pleme.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.me.pleme.R
import com.me.pleme.navigation.model.ContentDTO
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_photo)

        // 스토리지, auih, firestore 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 앨범 열기
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        //버튼 이벤트(업로드)
        val addphoto_btn_upload : Button = findViewById(R.id.addphoto_btn_upload)
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if (resultCode == Activity.RESULT_OK){
                //사진 선택 시 경로 이쪽으로 이동
                photoUri = data?.data
                val addphoto_image : ImageView = findViewById(R.id.addphoto_image)
                addphoto_image.setImageURI(photoUri)

            }else{
                //취소 버튼
                finish()
            }
        }
    }

    fun contentUpload() {
        //파일 이름
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        //Promise method --> 구글 권장 방식
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //이미지 업로드(promise 방식)
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //ContentDTO 데이터 클래스 생성.(image의 downloadUrl 저장)
            contentDTO.imageUrl = uri.toString()
            //currentUser의 uid 저장
            contentDTO.uid = auth?.currentUser?.uid
            //currentUser의 email(userId) 저장
            contentDTO.userId = auth?.currentUser?.email

            val addphoto_edit_explain: EditText = findViewById(R.id.addphoto_edit_explain)
            contentDTO.explain = addphoto_edit_explain.text.toString()

            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}