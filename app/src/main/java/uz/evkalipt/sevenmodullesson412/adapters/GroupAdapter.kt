package uz.evkalipt.sevenmodullesson412.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.evkalipt.sevenmodullesson412.databinding.ItemGroupBinding
import uz.evkalipt.sevenmodullesson412.models.Groups

class GroupAdapter(var list: List<Groups>, var onClick: OnClick):RecyclerView.Adapter<GroupAdapter.Vh>() {

    inner class Vh(var itemGroupBinding: ItemGroupBinding):RecyclerView.ViewHolder(itemGroupBinding.root){
        fun onBind(groups: Groups){
            itemGroupBinding.tvGroup.text = groups.name
            itemGroupBinding.root.setOnClickListener {
                onClick.onClick1(groups)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClick{
        fun onClick1(groups: Groups)
    }

}