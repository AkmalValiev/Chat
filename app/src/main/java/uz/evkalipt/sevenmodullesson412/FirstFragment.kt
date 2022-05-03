package uz.evkalipt.sevenmodullesson412

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import uz.evkalipt.sevenmodullesson412.databinding.FragmentFirstBinding
import uz.evkalipt.sevenmodullesson412.utils.NetworkHelper


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Suppress("DEPRECATION")
class FirstFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var binding: FragmentFirstBinding
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var networkHelper: NetworkHelper
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var RC_SIGN_IN = 1
    lateinit var auth:FirebaseAuth
    private val TAG = "FirstFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        sharedPreferences = binding.root.context.getSharedPreferences("db", 0)
        editor = sharedPreferences.edit()

        networkHelper = NetworkHelper(binding.root.context)
        if (networkHelper.isNetWorkConnected()){
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(binding.root.context, gso)
            binding.signInTv.visibility = View.VISIBLE
            binding.error.visibility = View.INVISIBLE
        } else{
            binding.signInTv.visibility = View.INVISIBLE
            binding.error.visibility = View.VISIBLE
        }
        val int = sharedPreferences.getInt("enter", -1)
        if (int == 1){
            binding.signInTv.visibility = View.GONE
            signIn()
            findNavController().popBackStack()
        }

        binding.signInTv.setOnClickListener {
            editor.putInt("enter", 1)
            editor.commit()
            signIn()
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(binding.root.context, e.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(binding.root.context as Activity) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.secondFragment)
                } else {
                    Toast.makeText(binding.root.context, "ErrorN2", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "firebaseAuthWithGoogle: ${task.exception?.message}")
                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}