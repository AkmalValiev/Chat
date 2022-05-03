package uz.evkalipt.sevenmodullesson412.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.evkalipt.sevenmodullesson412.ForPagerFragment
import uz.evkalipt.sevenmodullesson412.models.ModelForPager

@Suppress("DEPRECATION")
class AdapterForPager(var list: List<ModelForPager>, manager: FragmentManager)
    :FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
       return list.size
    }

    override fun getItem(position: Int): Fragment {
        return ForPagerFragment.newInstance(list[position])
    }

}