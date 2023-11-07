package com.me.pleme.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.me.pleme.R
import com.me.pleme.navigation.model.ContentDTO
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.me.pleme.navigation.model.AlarmDTO

class BoardFragment : Fragment() {

    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //프레그먼트 레이아웃 받아오는 부분
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_board, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        val recyclerView = view.findViewById<RecyclerView>(R.id.detailviewfragment_recyclerview)
        recyclerView.adapter = DetailViewRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    //디테일뷰의 아이템과 디테일뷰 layout을 합치는 어뎁터
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        //시간순으로 이미지들을 받아오고 만약 연결된 주소의 데이터가 바뀌면 바때마다 자동으로 업데이트해줌
        init {
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if(querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()      //값 새로고침 자동으로!
            }
        }

        //화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성한다.
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail,p0,false)
            return CustomViewHolder(view)
        }

        //클래스를 만들어서 리턴해주는이유는 메모리사용량 딱히 사용하지 않아도 문제 X.
        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        //위의 onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터를 연결
        //서버에서 넘어온 데이터들을 연결!
        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            //uesrId
            viewholder.findViewById<TextView>(R.id.detailviewitem_profile_textView).text = contentDTOs!![p1].userId

            //Image
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.findViewById(R.id.detailviewitem_imageview_content))

            //Explain of content
            viewholder.findViewById<TextView>(R.id.detailviewitem_explain_textview).text = contentDTOs!![p1].explain

            //likes
            viewholder.findViewById<TextView>(R.id.detailviewitem_favoritecounter_textview).text = "Likes " + contentDTOs!![p1].favoriteCount + "개"

            //좋아요 이벤트
            viewholder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview).setOnClickListener{
                favoriteEvent(p1)
            }
            FirebaseFirestore.getInstance().collection("profileImages").document(contentDTOs[p1].uid!!).get().addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val url = task.result!!["image"]
                    Glide.with(viewholder.context).load(url).apply(RequestOptions().circleCrop()).into(viewholder.findViewById(R.id.detailviewitem_profile_image))
                }
            }

            //좋아요 카운터 하트색상 이벤트
            if(contentDTOs!![p1].favorites.containsKey(uid)){
                //좋아요 클릭시(누른상태)
                viewholder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview).setImageResource(R.drawable.ic_favorite)
            } else {
                //좋아요 클릭시(안누른상태)
                viewholder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview).setImageResource(R.drawable.ic_favorite_border)
            }

            viewholder.findViewById<ImageView>(R.id.detailviewitem_profile_image).setOnClickListener {
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[p1].uid)
                bundle.putString("userId",contentDTOs[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()

            }
            viewholder.findViewById<ImageView>(R.id.detailviewitem_comment_imageview).setOnClickListener {v ->
                var intent = Intent(v.context,CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[p1])
                intent.putExtra("destinationUid",contentDTOs[p1].uid)
                startActivity(intent)

            }
        }
        fun favoriteEvent(position : Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction{ transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)) {
                    //좋아요 클릭이 되어있을 경우
                    contentDTO.favoriteCount = contentDTO?.favoriteCount!! - 1
                    contentDTO.favorites.remove(uid)
                } else {
                    //좋아요 클릭이 안 되어있을 경우
                    contentDTO.favoriteCount = contentDTO?.favoriteCount!! + 1
                    contentDTO.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }
        }

        fun favoriteAlarm(destinationUid : String){
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

            var message = FirebaseAuth.getInstance()?.currentUser?.email + getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid, "Pleme", message)
        }

        fun favoriteAlarm2(destinationUid : String){
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 3
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

            var message = FirebaseAuth.getInstance()?.currentUser?.email + getString(R.string.alarm_poke)
            FcmPush.instance.sendMessage(destinationUid, "Pleme", message)
        }


    }
}