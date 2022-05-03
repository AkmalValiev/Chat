package uz.evkalipt.sevenmodullesson412.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import uz.evkalipt.sevenmodullesson412.databinding.ItemFromGroupChatBinding
import uz.evkalipt.sevenmodullesson412.databinding.ItemFromUidBinding
import uz.evkalipt.sevenmodullesson412.models.Message
import uz.evkalipt.sevenmodullesson412.models.Users

class GroupChatAdapter(var list: List<Message>, var uid:String, var context: Context):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference

    inner class FromVh(var itemFromUidBinding: ItemFromUidBinding):RecyclerView.ViewHolder(itemFromUidBinding.root){
        fun onBind(message: Message){
            itemFromUidBinding.tvFrom.text = message.message
            itemFromUidBinding.dateFrom.text = message.date
        }
    }

    inner class ToVh(var itemFromGroupChatBinding: ItemFromGroupChatBinding):RecyclerView.ViewHolder(itemFromGroupChatBinding.root){
        fun onBind(message: Message){
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("users")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var users = Users()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Users::class.java)
                        if (value?.uid==message.formUid){
                            users = value!!
                            break
                        }
                    }
                    itemFromGroupChatBinding.displayProfile.text = users.displayName
                    itemFromGroupChatBinding.dateFromGroup.text = message.date
                    itemFromGroupChatBinding.textGroup.text = message.message
                    Picasso.with(context).load(users.photoUrl).into(itemFromGroupChatBinding.imageProfile)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1){
            return FromVh(ItemFromUidBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }else{
            return ToVh(ItemFromGroupChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) ==1){
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        }else{
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].formUid == uid){
            return 1
        }else{
            return 2
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}