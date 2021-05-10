package com.example.myviewinglist.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myviewinglist.R
import com.example.myviewinglist.ui.library.CompletedFragment
import com.example.myviewinglist.ui.library.DroppedFragment
import com.example.myviewinglist.ui.library.WaitingFragment
import com.example.myviewinglist.ui.library.WatchingFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_watching,
    R.string.tab_waiting,
    R.string.tab_completed,
    R.string.tab_dropped
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return WatchingFragment()
            }
            1 -> {
                return WaitingFragment()
            }
            2 -> {
                return CompletedFragment()
            }
            3 -> {
                return DroppedFragment()
            }
            else -> {
                return WatchingFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 4
    }
}