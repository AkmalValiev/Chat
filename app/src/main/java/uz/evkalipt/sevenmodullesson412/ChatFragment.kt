package uz.evkalipt.sevenmodullesson412

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.evkalipt.sevenmodullesson412.adapters.AdapterRvMessage
import uz.evkalipt.sevenmodullesson412.databinding.FragmentChatBinding
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

class ChatFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var binding: FragmentChatBinding
    lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var adapterRvMessage: AdapterRvMessage
    lateinit var apiService:ApiService
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        apiService = ApiClient.getRetrofit("https://fcm.googleapis.com").create(ApiService::class.java)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        val users = arguments?.getSerializable("key") as Users
        var uid = users.uid
        binding.displayNameChat.text = users.displayName
        Picasso.with(binding.root.context).load(users.photoUrl).into(binding.profileImageChat)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var users1 = Users()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Users::class.java)
                    if (value?.uid==users.uid){
                        users1 = value!!
                        break
                    }
                }
                if (users1.online==1){
                    binding.onlineChat.visibility = View.VISIBLE
                    binding.onlineChat1.visibility = View.INVISIBLE
                }else{
                    binding.onlineChat.visibility = View.INVISIBLE
                    binding.onlineChat1.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.backIos.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendChat.setOnClickListener {
            val message1 = binding.edTextChat.text.toString()
            if (!message1.trim().equals("")){
                val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
                var date = format.format(Date())
                var currentUid = auth.currentUser?.uid
                var message = Message(message1, date, uid, currentUid)

                var key1 = reference.push().key
                reference.child("$currentUid/message/$uid/$key1").setValue(message)

                var key2 = reference.push().key
                reference.child("$uid/message/$currentUid/$key2").setValue(message)

                apiService.sendNotification(Sender(
                    Data(
                        auth.currentUser?.uid!!,
                        R.drawable.ic_baseline_send_24,
                        message1, "", uid!!
                    ), users.cloudMessageToken!!
                )).enqueue(object :Callback<MyResponce>{
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

            }
            binding.edTextChat.setText("")
        }

        reference.child("${auth.currentUser?.uid}/message/$uid")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var messageList = ArrayList<Message>()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Message::class.java)
                        messageList.add(value!!)
                    }
                    adapterRvMessage = AdapterRvMessage(messageList, auth.currentUser!!.uid)
                    binding.rvChat.adapter = adapterRvMessage
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        return binding.root
    }

    override fun onStop() {
        reference.child("${auth.currentUser?.uid}/online").setValue(0)
        super.onStop()
    }

    override fun onResume() {
        reference.child("${auth.currentUser?.uid}/online").setValue(1)
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}