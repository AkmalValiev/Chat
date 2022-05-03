package uz.evkalipt.sevenmodullesson412.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.evkalipt.sevenmodullesson412.databinding.ItemFromUidBinding
import uz.evkalipt.sevenmodullesson412.databinding.ItemToUidBinding
import uz.evkalipt.sevenmodullesson412.models.Message

class AdapterRvMessage(var list: List<Message>, var uid:String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVh(var itemFromUidBinding: ItemFromUidBinding):RecyclerView.ViewHolder(itemFromUidBinding.root){
        fun onBind(message: Message){
            itemFromUidBinding.tvFrom.text = message.message
            itemFromUidBinding.dateFrom.text = message.date
        }
    }

    inner class ToVh(var itemToUidBinding: ItemToUidBinding):RecyclerView.ViewHolder(itemToUidBinding.root){
        fun onBind(message: Message){
            itemToUidBinding.tvTo.text = message.message
            itemToUidBinding.dateTo.text = message.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            return ToVh(ItemToUidBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }else{
            return FromVh(ItemFromUidBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }else{
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
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