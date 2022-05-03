package uz.evkalipt.sevenmodullesson412

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.evkalipt.sevenmodullesson412.adapters.GroupChatAdapter
import uz.evkalipt.sevenmodullesson412.databinding.FragmentGroupChatBinding
import uz.evkalipt.sevenmodullesson412.models.Groups
import uz.evkalipt.sevenmodullesson412.models.Message
import uz.evkalipt.sevenmodullesson412.models.Users
import uz.evkalipt.sevenmodullesson412.retrofit.ApiClient
import uz.evkalipt.sevenmodullesson412.retrofit.ApiService
import uz.evkalipt.sevenmodullesson412.retrofit.models.Data
import uz.evkalipt.sevenmodullesson412.retrofit.models.MyResponce
import uz.evkalipt.sevenmodullesson412.retrofit.models.Sender
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GroupChatFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentGroupChatBinding
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var reference2:DatabaseReference
    lateinit var groupChatAdapter: GroupChatAdapter
    lateinit var groups: Groups
    lateinit var userList:ArrayList<Users>
    lateinit var apiService:ApiService

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupChatBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("groups")
        reference2 = database.getReference("users")
        userList = ArrayList()
        reference2.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Users::class.java)
                    if (value?.uid != auth.currentUser?.uid){
                        userList.add(value!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        apiService = ApiClient.getRetrofit("https://fcm.googleapis.com").create(ApiService::class.java)
        groups = arguments?.getSerializable("group") as Groups
        binding.toolBarRvGroupChat.title = groups.name
        binding.toolBarRvGroupChat.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendGroup.setOnClickListener {
            if (!binding.edGroupChat.text.toString().trim().equals("")) {
                val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
                var date = format.format(Date())
                var message = Message(
                    binding.edGroupChat.text.toString(),
                    date,
                    auth.currentUser?.uid,
                    groups.uid
                )

                var key = reference.push().key
                reference.child("${groups.uid}/message/$key").setValue(message)
                load()
                for (users in userList) {
                    apiService.sendNotification(
                        Sender(
                            Data(
                                auth.currentUser?.uid!!,
                                R.drawable.ic_baseline_send_24,
                                binding.edGroupChat.text.toString(),
                                "in group  '${groups.name}'", users.uid!!
                            ), users.cloudMessageToken!!
                        )
                    ).enqueue(object : Callback<MyResponce> {
                        override fun onResponse(
                            call: Call<MyResponce>,
                            response: Response<MyResponce>
                        ) {
                            if (response.isSuccessful){

                            }
                        }

                        override fun onFailure(call: Call<MyResponce>, t: Throwable) {

                        }

                    })
                    binding.edGroupChat.setText("")
                }

            }

        }
        load()

        reference2.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return binding.root
    }

    override fun onStop() {
        reference2.child("${auth.currentUser?.uid}/online").setValue(0)
        super.onStop()
    }

    override fun onResume() {
        reference2.child("${auth.currentUser?.uid}/online").setValue(1)
        super.onResume()
    }

    fun load(){
        reference.child("${groups.uid}/message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var messageList = ArrayList<Message>()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Message::class.java)
                        messageList.add(value!!)
                    }
                    groupChatAdapter =
                        GroupChatAdapter(messageList, auth.currentUser!!.uid, binding.root.context)
                    binding.rvGroup.adapter = groupChatAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}