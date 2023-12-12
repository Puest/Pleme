package com.me.pleme.navigation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.me.pleme.R
import com.me.pleme.add_f
import com.me.pleme.databinding.Dialog2Binding
import com.me.pleme.databinding.FragmentDetailBinding
import com.me.pleme.dialog2
import com.me.pleme.dialog3
import com.me.pleme.navigation.model.AlarmDTO
import com.me.pleme.navigation.model.ContentDTO
import com.me.pleme.setting
import com.me.pleme.sucess_1
import com.me.pleme.sucess_f


class DetailViewFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)

        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view2 = binding.root

        var counter: Int = 0
        val counterNum = binding.pokeCount


        binding.pokeBtn.setOnClickListener {
            binding.pokeBtn.isEnabled = false

            // 버튼 클릭 이벤트 처리 코드 작성
            var con_2 = dialog2(requireContext())
            con_2.showDialog()
            counter++
            counterNum.text = counter.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.pokeBtn.isEnabled = true
            }, 300000)
        }

        binding.noteBtn.setOnClickListener {
            binding.noteBtn.isEnabled = false
            // 버튼 클릭 이벤트 처리 코드 작성
            val con_3 = dialog3(requireContext())

            con_3.setOnClickListener(object : dialog3.OnDialogClickListener {
                override fun onCanceled() {
                    //"취소"
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.pokeBtn.isEnabled = true
                    }, 300000)
                }

            })

            con_3.setOnSendClickListener(object : dialog3.OnSendClickListener {
                override fun onSendClicked() {
                    // '보내기' 버튼이 클릭되었을 때 수행할 동작을 여기에 작성하세요.
                    val successPopup = sucess_1(requireContext())
                    successPopup.setOnClickListener(object : sucess_1.OnDialogClickListener {
                        override fun onClicked() {
                            // '확인' 버튼이 클릭되었을 때 수행할 동작을 여기에 작성하세요.
                        }
                    })
                    successPopup.showDialog()
                }

            })
            con_3.showDialog()
        }

        binding.setting.setOnClickListener {
            var setting = setting(requireContext())
            setting.showDialog()
        }

        binding.friend.setOnClickListener {
            val addDialog = add_f(requireContext())

            addDialog.setOnConfirmClickListener(object : add_f.OnConfirmClickListener {
                override fun onConfirmClicked() {
                    val successDialog = sucess_f(requireContext())
                    successDialog.setOnClickListener(object : sucess_f.OnDialogClickListener {
                        override fun onClicked() {
                            // 팝업 창이 닫힐 때 수행할 동작을 여기에 작성하세요.
                        }
                    })
                    successDialog.showDialog()
                }
            })

            addDialog.showDialog()
        }
        return view2

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}