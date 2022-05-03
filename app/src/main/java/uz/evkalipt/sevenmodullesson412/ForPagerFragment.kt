package uz.evkalipt.sevenmodullesson412

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uz.evkalipt.sevenmodullesson412.adapters.AdapterChat
import uz.evkalipt.sevenmodullesson412.adapters.GroupAdapter
import uz.evkalipt.sevenmodullesson412.databinding.FragmentForPagerBinding
import uz.evkalipt.sevenmodullesson412.databinding.ItemForAlertBinding
import uz.evkalipt.sevenmodullesson412.models.Groups
import uz.evkalipt.sevenmodullesson412.models.ModelForPager
import uz.evkalipt.sevenmodullesson412.models.Users

private const val ARG_PARAM1 = "param1"
class ForPagerFragment : Fragment() {
    private var param1:ModelForPager?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as ModelForPager
        }
    }
    lateinit var binding:FragmentForPagerBinding
    lateinit var auth:FirebaseAuth
    lateinit var adapterChat:AdapterChat
    lateinit var groupAdapter: GroupAdapter
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var referenceGroup:DatabaseReference
    lateinit var userList:ArrayList<Users>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForPagerBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        userList = ArrayList()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        referenceGroup = firebaseDatabase.getReference("groups")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Users::class.java)
                    if (value?.uid != auth.currentUser?.uid)
                        userList.add(value!!)
                }
                if (param1?.title.toString() == "Chats") {
                    adapterChat =
                        AdapterChat(userList, binding.root.context, object : AdapterChat.MyOnClick {
                            override fun onClick(users: Users) {
                                var bundle = Bundle()
                                bundle.putSerializable("key", users)
                                findNavController().navigate(R.id.chatFragment, bundle)
                            }

                        })
                    binding.rvFragmentForPager.adapter = adapterChat
                } else if (param1?.title.toString() == "Groups") {
                    binding.floating.visibility = View.VISIBLE
                    load()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.floating.setOnClickListener {
            var alertDialog = AlertDialog.Builder(binding.root.context)
            alertDialog.setMessage("Gruruh nomini kiriting!")
            val inflate = ItemForAlertBinding.inflate(layoutInflater)
            alertDialog.setView(inflate.root)
            alertDialog.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if (!inflate.etAlert.text.toString().trim().equals("")) {
                        referenceGroup.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val children = snapshot.children
                                var boolean = false
                                for (child in children) {
                                    val value = child.getValue(Groups::class.java)
                                    if (value?.name == inflate.etAlert.text.toString()) {
                                        boolean = true
                                        break
                                    }
                                }
                                if (boolean){
                                    Toast.makeText(
                                        binding.root.context,
                                        "Bunday gurux mavjud, boshqa nom kiriting!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else{
                                    var key = referenceGroup.push().key
                                    var groups = Groups(inflate.etAlert.text.toString(), key)
                                    referenceGroup.child("$key").setValue(groups)
                                    load()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })

                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "Gurux nomini kiriting!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
            alertDialog.setNegativeButton("Cancel", object :DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }

            })
            alertDialog.show()
        }

        return binding.root
    }

    fun load(){
        var groupList = ArrayList<Groups>()
        referenceGroup.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                val children1 = snapshot.children
                for (dataSnapshot in children1) {
                    val value = dataSnapshot.getValue(Groups::class.java)
                    groupList.add(value!!)
                }
                groupAdapter = GroupAdapter(groupList, object :GroupAdapter.OnClick{
                    override fun onClick1(groups: Groups) {
                        var bundle = Bundle()
                        bundle.putSerializable("group", groups)
                        findNavController().navigate(R.id.groupChatFragment, bundle)
                    }

                })
                binding.rvFragmentForPager.adapter = groupAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1:ModelForPager) =
            ForPagerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}