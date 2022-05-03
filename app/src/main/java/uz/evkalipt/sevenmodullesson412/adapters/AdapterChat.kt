package uz.evkalipt.sevenmodullesson412.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import uz.evkalipt.sevenmodullesson412.databinding.ItemForRvChatBinding
import uz.evkalipt.sevenmodullesson412.models.Message
import uz.evkalipt.sevenmodullesson412.models.Users

class AdapterChat(var list: List<Users>, var context: Context, var myOnClick: MyOnClick):RecyclerView.Adapter<AdapterChat.Vh>() {
    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var auth: FirebaseAuth

    inner class Vh(var itemForRvChatBinding: ItemForRvChatBinding):RecyclerView.ViewHolder(itemForRvChatBinding.root){
        fun onBind(user: Users){
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("users")
            auth = FirebaseAuth.getInstance()
            var messageList1 = ArrayList<Message>()
            Picasso.with(context).load(user.photoUrl).into(itemForRvChatBinding.profileImageChat)
            if (user.online==0){
                itemForRvChatBinding.online1.visibility = View.INVISIBLE
            }else if (user.online ==1){
                itemForRvChatBinding.online1.visibility = View.VISIBLE
            }
            itemForRvChatBinding.displayNameChat.text = user.displayName
            reference.child("${user.uid}/message/${auth.currentUser?.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.children
                        for (child in children) {
                            val message = child.getValue(Message::class.java)
                            messageList1.add(message!!)
                        }
                        if (messageList1.size>0) {
                            itemForRvChatBinding.endSms.text =
                                messageList1[messageList1.size - 1].message
                            itemForRvChatBinding.timeEndSms.text =
                                messageList1[messageList1.size - 1].date
                        }else{
                            itemForRvChatBinding.endSms.text = ""
                            itemForRvChatBinding.timeEndSms.text = ""
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            itemForRvChatBinding.root.setOnClickListener {
                myOnClick.onClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemForRvChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface MyOnClick{
        fun onClick(chat: Users)
    }

}