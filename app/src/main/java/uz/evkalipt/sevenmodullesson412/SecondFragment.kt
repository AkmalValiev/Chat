package uz.evkalipt.sevenmodullesson412

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_for_tab_layout.view.*
import uz.evkalipt.sevenmodullesson412.adapters.AdapterForPager
import uz.evkalipt.sevenmodullesson412.databinding.FragmentSecondBinding
import uz.evkalipt.sevenmodullesson412.databinding.ItemForTabLayoutBinding
import uz.evkalipt.sevenmodullesson412.models.Groups
import uz.evkalipt.sevenmodullesson412.models.ModelForPager
import uz.evkalipt.sevenmodullesson412.models.Users
import kotlin.system.exitProcess

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SecondFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var binding: FragmentSecondBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var reference1:DatabaseReference
    lateinit var userList: ArrayList<Users>
    lateinit var groupList:ArrayList<Groups>
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var pagerList:ArrayList<ModelForPager>
    lateinit var adapterForPager:AdapterForPager
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private val TAG = "SecondFragment"
    lateinit var cloudMessageToken:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(layoutInflater)
        cloudMessageToken = ""

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                Log.d(TAG, token)
                cloudMessageToken = token
            })



        sharedPreferences = binding.root.context.getSharedPreferences("db", 0)
        editor = sharedPreferences.edit()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        reference.child("${firebaseAuth.currentUser?.uid}/online").setValue(1)
        reference.child("${firebaseAuth.currentUser?.uid}")
        userList = ArrayList()
        groupList = ArrayList()
        pagerList = ArrayList()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(binding.root.context, gso)

        val currentUser = firebaseAuth.currentUser

        reference1 = firebaseDatabase.getReference("groups")
        Picasso.with(binding.root.context).load(currentUser?.photoUrl).into(binding.profileImage)
        binding.displayName.text = currentUser?.displayName
        binding.logOut.setOnClickListener {
            var alertDialog = AlertDialog.Builder(binding.root.context)
            alertDialog.setMessage("Dasturdan chiqib ketmoqchimisiz?")
            alertDialog.setPositiveButton("ha", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    reference.child("${firebaseAuth.currentUser?.uid}/online").setValue(0)
                    editor.putInt("enter", 2)
                    editor.commit()
                    googleSignInClient.signOut()
                    exitProcess(-1)
                    findNavController().popBackStack()
                }

            })
            alertDialog.setNegativeButton("yo'q", object :DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }

            })

            alertDialog.show()

        }
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reference.child("${firebaseAuth.currentUser?.uid}/cloudMessageToken").setValue(cloudMessageToken)
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Users::class.java)
                    userList.add(value!!)
                }

                var booleanUid = false
                for (users in userList) {
                    if (users.uid == currentUser?.uid) {
                        booleanUid = true
                        break
                    }
                }
                if (!booleanUid) {
                    var users = Users(
                        currentUser?.uid,
                        currentUser?.displayName,
                        "",
                        currentUser?.photoUrl.toString(),
                        0,
                        "",
                        cloudMessageToken
                    )
                    reference.child(currentUser?.uid!!).setValue(users)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        reference1.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                for (child in children) {
                    val group = child.getValue(Groups::class.java)
                    groupList.add(group!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        loadLists()
        adapterForPager = AdapterForPager(pagerList, childFragmentManager)
        binding.viewPager.adapter = adapterForPager
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        var tabCount = binding.tabLayout.tabCount
        for (i in 0 until tabCount){
            val tabView = ItemForTabLayoutBinding.inflate(layoutInflater)
            binding.tabLayout.getTabAt(i)?.customView = tabView.root
            tabView.tvChats.text =pagerList[i].title
            tabView.tvGroups.text =pagerList[i].title
            if (i==0){
                tabView.linear2.visibility = View.VISIBLE
            }else{
                tabView.linear2.visibility = View.INVISIBLE
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var tabView111 = tab?.customView
                tabView111?.linear2?.visibility = View.VISIBLE
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var tabView111 = tab?.customView
                tabView111?.linear2?.visibility = View.INVISIBLE
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        return binding.root
    }

    private fun loadLists() {
        pagerList.add(ModelForPager("Chats", userList, groupList))
        pagerList.add(ModelForPager("Groups", userList, groupList))
    }

    override fun onDestroy() {
        reference.child("${firebaseAuth.currentUser?.uid}/online").setValue(0)
        super.onDestroy()
    }

    override fun onStop() {
        reference.child("${firebaseAuth.currentUser?.uid}/online").setValue(0)
        super.onStop()
    }

    override fun onResume() {
        reference.child("${firebaseAuth.currentUser?.uid}/online").setValue(1)
        super.onResume()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}