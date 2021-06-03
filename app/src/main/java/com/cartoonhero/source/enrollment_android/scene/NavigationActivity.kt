package com.cartoonhero.source.enrollment_android.scene

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cartoonhero.source.props.inlineMethods.removeFragment
import com.cartoonhero.source.props.inlineMethods.replaceFragment

open class NavigationActivity : AppCompatActivity() {

    private var currentPage = 0
    private val pageStack = ArrayList<Fragment>()

    fun setRootFragment(fragment: Fragment, resourceId: Int) {
        if (pageStack.size > 0) {
            removeFragment(currentFragment())
            pageStack.clear()
            clearFragmentBackStack()
        }
        replaceFragment(fragment,resourceId)
        pageStack.add(0, fragment)
    }

    fun goForward(fragments: List<Fragment>, resourceId: Int) {
        currentPage += fragments.size
        pageStack.addAll(fragments)
        replaceFragment(fragments.last(), resourceId)
    }

    fun goBack(resourceId: Int) {
        if (currentPage > 0) {
            val currentFragment = pageStack[currentPage]
            val previousFragment = pageStack[currentPage-1]
            pageStack.remove(currentFragment)
            currentPage -= 1
            replaceFragment(previousFragment, resourceId)
        } else {
            finish()
        }
    }
    fun backToPage(index: Int, resourceId: Int) {
        if (currentPage > index) {
            replaceFragment(pageStack[index], resourceId)
            pageStack.dropLast( (currentPage-index) )
        }
    }

    private fun currentFragment(): Fragment{
        return pageStack.last()
    }

    fun childFragments(): List<Fragment> {
        return pageStack.toList()
    }

    private fun clearFragmentBackStack() {
        if (supportFragmentManager.fragments.size > 0) {
            for (fragment in supportFragmentManager.fragments) {
                supportFragmentManager.beginTransaction().remove(fragment!!).commit()
            }
        }
    }
}