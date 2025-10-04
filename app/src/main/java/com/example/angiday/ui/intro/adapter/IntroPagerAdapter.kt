package com.example.angiday.ui.intro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

data class IntroPage(
    val title: String,
    val subtitle: String,
    val imageRes: Int
)

class IntroPagerAdapter(
    activity: FragmentActivity,
    private val items: List<IntroPage>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = items.size

    override fun createFragment(position: Int): Fragment {
        val p = items[position]
        return IntroFragment.newInstance(p.title, p.subtitle, p.imageRes)
    }
}
