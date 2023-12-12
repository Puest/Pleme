package com.me.pleme.navigation

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.me.pleme.MainActivity2
import com.me.pleme.Main_page1
import com.me.pleme.R
import com.me.pleme.navigation.model.AlarmDTO
import com.me.pleme.navigation.model.ContentDTO
import com.me.pleme.navigation.model.FollowDTO

class UserFragment : Fragment() {
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        if (uid == currentUserUid) {
            //내 페이지
            fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.text =
                getString(R.string.signout)
            fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)
                ?.setOnClickListener {
                    activity?.finish()
                    startActivity(Intent(activity, MainActivity2::class.java))
                    auth?.signOut()
                }
        } else {
            //상대창 페이지
            fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.text =
                getString(R.string.follow)
            var mainactivity = (activity as Main_page1)
            mainactivity?.findViewById<TextView>(R.id.toolbar_username)?.text =
                arguments?.getString("userId")
            mainactivity?.findViewById<ImageView>(R.id.toolbar_btn_back)?.setOnClickListener {
                mainactivity.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId =
                    R.id.action_home
            }
            mainactivity?.findViewById<ImageView>(R.id.icon_1)?.visibility = View.GONE
            mainactivity?.findViewById<ImageView>(R.id.icon_2)?.visibility = View.GONE
            mainactivity?.findViewById<TextView>(R.id.toolbar_username)?.visibility = View.VISIBLE
            mainactivity?.findViewById<ImageView>(R.id.toolbar_btn_back)?.visibility = View.VISIBLE
            fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)
                ?.setOnClickListener {
                    requestFollow()
                }
        }
        fragmentView?.findViewById<RecyclerView>(R.id.account_recyclerview)?.adapter =
            UserFragmentRecyclerViewAdapter()
        fragmentView?.findViewById<RecyclerView>(R.id.account_recyclerview)?.layoutManager =
            GridLayoutManager(requireActivity(), 3)

        fragmentView?.findViewById<ImageView>(R.id.account_iv_profile)?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }
        getProfileImage()
        getFollowerAndFollowing()
        return fragmentView
    }

    fun getFollowerAndFollowing() {
        firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                if (followDTO?.followingCount != null) {
                    fragmentView?.findViewById<TextView>(R.id.account_tv_following_count)?.text =
                        followDTO.followingCount?.toString()
                }
                if (followDTO?.followerCount != null) {
                    fragmentView?.findViewById<TextView>(R.id.account_tv_follower_count)?.text =
                        followDTO.followerCount?.toString()
                    if (followDTO?.followers?.containsKey(currentUserUid!!) == true) {
                        fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.text =
                            getString(R.string.follow_cancel)
                        fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.background?.setColorFilter(
                            ContextCompat.getColor(requireActivity()!!, R.color.colorLightGray),
                            PorterDuff.Mode.MULTIPLY
                        )
                    } else {
                        if (uid != currentUserUid) {
                            fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.text =
                                getString(R.string.follow)
                            fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.background?.colorFilter =
                                null
                        }
                    }
                }
            }
    }

    fun requestFollow() {
        var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followingCount = 1
                followDTO!!.followers[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }
            if (followDTO.followings.containsKey(uid)) {
                //이미 팔로잉시 (취소)
                followDTO?.followingCount = followDTO.followingCount - 1
                followDTO?.followings?.remove(uid)
            } else {
                //팔로잉
                followDTO?.followingCount = followDTO.followingCount + 1
                followDTO.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }
        //내가 팔로잉 한 상대방 계정에 접근 코드
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }
            if (followDTO!!.followers.containsKey(currentUserUid)) {
                //이미 팔로잉시 (취소)
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            } else {
                //팔로잉
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }

    fun followerAlarm(destinationUid: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = auth?.currentUser?.email
        alarmDTO.uid = auth?.currentUser?.uid
        alarmDTO.kind = 2
        alarmDTO.timestamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = auth?.currentUser?.email + getString(R.string.alarm_follow)
        FcmPush.instance.sendMessage(destinationUid, "Pleme", message)
    }

    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    var url = documentSnapshot?.data!!["image"]
                    Glide.with(requireActivity()!!).load(url).apply(RequestOptions().circleCrop())
                        .into(fragmentView?.findViewById<ImageView>(R.id.account_iv_profile)!!)
                }
            }
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.whereEqualTo("uid", uid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    //Somtimes, This cod return null of querySnapshot when it signout
                    if (querySnapshot == null) return@addSnapshotListener

                    //Get data
                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }

                    fragmentView?.findViewById<TextView>(R.id.account_tv_post_count)?.text =
                        contentDTOs.size.toString()

                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var imageview = (p0 as CustomViewHolder).imageview
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageview)
        }

    }
}