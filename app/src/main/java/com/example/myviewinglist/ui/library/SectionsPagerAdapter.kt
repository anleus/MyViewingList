package com.example.myviewinglist.ui.library

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myviewinglist.R
import com.example.myviewinglist.ui.library.tabs.CompletedFragment
import com.example.myviewinglist.ui.library.tabs.DroppedFragment
import com.example.myviewinglist.ui.library.tabs.WaitingFragment
import com.example.myviewinglist.ui.library.tabs.WatchingFragment

private val TAB_TITLES = arrayOf(
    R.string.watching_name,
    R.string.waiting_name,
    R.string.completed_name,
    R.string.dropped_name
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
        // Show 4 total pages.
        return 4
    }
}