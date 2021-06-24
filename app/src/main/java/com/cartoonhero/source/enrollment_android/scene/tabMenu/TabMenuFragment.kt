package com.cartoonhero.source.enrollment_android.scene.tabMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.entities.TabMenuSource
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_tab_menu.*
import kotlinx.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class TabMenuFragment:Fragment() {

    private val scenario = TabMenuScenario()
    private var tabSource: TabMenuSource = TabMenuSource()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_tab_menu,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scenario.toBePrepareTabSources { source ->
            tabSource = source
            tab_bar.inflateMenu(tabSource.menuResId)
            activity?.let { act ->
                if (act is AppCompatActivity)
                    this.tab_viewPager.adapter = ViewPagerStateAdapter(act)
            }
            TabLayoutMediator(this.tabLayout,this.tab_viewPager) { tab,position ->
                val tabItem = source.tabItems[position]
                tab.text = tabItem.title
            }.attach()
        }
    }

    private inner class ViewPagerStateAdapter(
        activity: AppCompatActivity): FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return tabSource.pages.size
        }

        override fun createFragment(position: Int): Fragment {
            return tabSource.pages[position]
        }
    }
}