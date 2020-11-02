package com.example.bottomnavigationmultistack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    var backStack = ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation.setOnNavigationItemSelectedListener {
            replaceBottomNavigationFragment(it.itemId)
            true
        }

        if (savedInstanceState == null) {
            replaceFragment(getHomeFragment(), HomeFragment.TAG)
            reorderBackStack(HomeFragment.TAG)
        } else {
            backStack = savedInstanceState.getStringArrayList("FRAGMENTS") ?: return
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("FRAGMENTS", backStack)
    }

    override fun onBackPressed() {

        val lastFragment = supportFragmentManager.findFragmentById(R.id.container) ?: return
        val backStackEntryCount = lastFragment.childFragmentManager.backStackEntryCount

        if (backStackEntryCount == 0) {
            if (backStack.size == 1) {
                finish()
            } else {
                backStack.removeLast()
                val tag = backStack.last()

                val fragment = supportFragmentManager.findFragmentByTag(tag) ?: return

                replaceFragment(fragment, tag)

            }
        } else {
            lastFragment.childFragmentManager.popBackStack()
        }


    }

    fun replaceBottomNavigationFragment(itemId: Int) {
        when(itemId){
            R.id.item1 -> {
                reorderBackStack(HomeFragment.TAG)
                replaceFragment(getHomeFragment(), HomeFragment.TAG)
            }
            R.id.item2 -> {
                reorderBackStack(CartOneFragment.TAG)
                replaceFragment(getCartFragment(), CartOneFragment.TAG)
            }
            R.id.item3 -> {
                reorderBackStack(ProfileOneFragment.TAG)
                replaceFragment(getProfileFragment(), ProfileOneFragment.TAG)
            }
        }
    }

    fun getHomeFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(HomeFragment.TAG) ?: HomeFragment()
    }

    fun getCartFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(CartOneFragment.TAG) ?: CartOneFragment()
    }

    fun getProfileFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(ProfileOneFragment.TAG) ?: ProfileOneFragment()
    }

    fun reorderBackStack(tag: String?) {
        val name = backStack.find { it == tag }
        if (name == null) {
            backStack.add(tag)
        } else {
            backStack.remove(name)
            backStack.add(name)
        }
    }

    fun addedToBackStack(tag: String?) : Boolean {
        return supportFragmentManager.findFragmentByTag(tag) == null
    }

    fun replaceFragment(fragment: Fragment, tag: String?) {
        supportFragmentManager.beginTransaction().run {
            replace(R.id.container, fragment, tag)
            if (addedToBackStack(tag)) {
                addToBackStack(tag)
            }
            commit()
        }
    }

    fun <T> ArrayList<T>.removeLast() {
        removeAt(lastIndex)
    }

}