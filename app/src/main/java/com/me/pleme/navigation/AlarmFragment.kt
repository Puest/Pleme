package com.me.pleme.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.me.pleme.R
import com.me.pleme.navigation.model.AlarmDTO


class AlarmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm, container, false)
        view.findViewById<RecyclerView>(R.id.alarmfragment_recyclerview).adapter =
            AlarmRecyclerviewAdapter()
        view.findViewById<RecyclerView>(R.id.alarmfragment_recyclerview).layoutManager =
            LinearLayoutManager(activity)
        return view
    }

    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    alarmDTOList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_comment, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView

            FirebaseFirestore.getInstance().collection("profileImages")
                .document(alarmDTOList[p1].uid!!).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result!!["image"]
                    Glide.with(view.context).load(url).apply(RequestOptions().circleCrop())
                        .into(view.findViewById(R.id.commentviewitem_imageview_profile))
                }
            }

            when (alarmDTOList[p1].kind) {
                0 -> {
                    var str_0 = alarmDTOList[p1].userId + " " + getString(R.string.alarm_favorite)
                    view.findViewById<TextView>(R.id.commentviewitem_textview_profile).text = str_0
                }

                1 -> {
                    var str_0 =
                        alarmDTOList[p1].userId + " 님이 \"" + alarmDTOList[p1].message + getString(R.string.alarm_comment)
                    view.findViewById<TextView>(R.id.commentviewitem_textview_profile).text = str_0
                }

                2 -> {
                    var str_0 = alarmDTOList[p1].userId + " " + getString(R.string.alarm_follow)
                    view.findViewById<TextView>(R.id.commentviewitem_textview_profile).text = str_0
                }

                3 -> {
                    var str_0 = alarmDTOList[p1].userId + " " + getString(R.string.alarm_poke)
                    view.findViewById<TextView>(R.id.commentviewitem_textview_profile).text = str_0
                }
            }
            view.findViewById<TextView>(R.id.commentviewitem_textview_comment).visibility =
                View.INVISIBLE
        }
    }
}