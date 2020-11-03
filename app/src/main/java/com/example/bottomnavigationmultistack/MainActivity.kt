package com.example.bottomnavigationmultistack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import com.example.bottomnavigationmultistack.cart.CartFragment
import com.example.bottomnavigationmultistack.home.HomeFragment
import com.example.bottomnavigationmultistack.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalStateException
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var bottomNavigationBackStack = ArrayList<String?>()

    private val fragmentItemIds = SparseArrayCompat<String?>()

    init {
        fragmentItemIds.put(R.id.inicio, HomeFragment.TAG)
        fragmentItemIds.put(R.id.buscar, SearchFragment.TAG)
        fragmentItemIds.put(R.id.carrito, CartFragment.TAG)
        fragmentItemIds.put(R.id.cuenta, ProfileOneFragment.TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            replaceFragment(getHomeFragment(), HomeFragment.TAG)
            reorderBackStack(HomeFragment.TAG)
        } else {
            bottomNavigationBackStack = savedInstanceState.getStringArrayList("FRAGMENTS") ?: return
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("FRAGMENTS", bottomNavigationBackStack)
    }

    override fun onBackPressed() {

        val lastFragment = supportFragmentManager.findFragmentById(R.id.container) ?: return
        val backStackEntryCount = lastFragment.childFragmentManager.backStackEntryCount

        if (backStackEntryCount == 0) {
            if (bottomNavigationBackStack.size == 1) {
                finish()
            } else {
                bottomNavigationBackStack.removeLast()
                val tag = bottomNavigationBackStack.last()

                val fragment = supportFragmentManager.findFragmentByTag(tag) ?: return

                selectBottomNavigationItem(getItemId(tag))
                replaceFragment(fragment, tag)

            }
        } else {
            lastFragment.childFragmentManager.popBackStack()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectBottomNavigationItem(item.itemId)
        replaceBottomNavigationFragment(item.itemId)
        return false
    }

    fun selectBottomNavigationItem(itemId: Int) {
        bottomNavigation.setOnNavigationItemSelectedListener(null)
        bottomNavigation.selectedItemId = itemId
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    fun replaceBottomNavigationFragment(itemId: Int) {
        val tag = fragmentItemIds.get(itemId)
        reorderBackStack(tag)

        val fragment = getOrCreateFragment(itemId)
        replaceFragment(fragment, tag)
    }

    private fun getItemId(tag: String?): Int {
        return when(tag) {
            HomeFragment.TAG -> R.id.inicio
            SearchFragment.TAG -> R.id.buscar
            CartFragment.TAG -> R.id.carrito
            ProfileOneFragment.TAG -> R.id.cuenta
            else -> throw IllegalStateException("No id found")
        }
    }

    private fun getOrCreateFragment(itemId: Int): Fragment {
        return when(itemId){
            R.id.inicio -> {
                getHomeFragment()
            }
            R.id.buscar -> {
                getSearchFragment()
            }
            R.id.carrito -> {
                getCartFragment()
            }
            R.id.cuenta -> {
                getProfileFragment()
            }
            else -> throw IllegalStateException("No Fragment found")
        }
    }

    private fun getHomeFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(HomeFragment.TAG) ?: HomeFragment()
    }

    private fun getSearchFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(SearchFragment.TAG) ?: SearchFragment()
    }

    private fun getCartFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(CartFragment.TAG) ?: CartFragment()
    }

    private fun getProfileFragment(): Fragment {
        return supportFragmentManager.findFragmentByTag(ProfileOneFragment.TAG) ?: ProfileOneFragment()
    }

    private fun reorderBackStack(tag: String?) {
        val name = bottomNavigationBackStack.find { it == tag }
        if (name == null) {
            bottomNavigationBackStack.add(tag)
        } else {
            bottomNavigationBackStack.remove(name)
            bottomNavigationBackStack.add(name)
        }
    }

    private fun fragmentNeedsToBeAddedToBackStack(tag: String?) : Boolean {
        return supportFragmentManager.findFragmentByTag(tag) == null
    }

    private fun replaceFragment(fragment: Fragment, tag: String?) {
        supportFragmentManager.beginTransaction().run {
            replace(R.id.container, fragment, tag)
            if (fragmentNeedsToBeAddedToBackStack(tag)) {
                addToBackStack(tag)
            }
            commit()
        }
    }

    private fun <T> ArrayList<T>.removeLast() {
        removeAt(lastIndex)
    }

}