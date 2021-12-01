package com.example.cdmdda.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cdmdda.view.fragment.LoginFragment
import com.example.cdmdda.view.fragment.RegisterFragment

class AccountFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int) = when (position) {
        0 -> LoginFragment()
        1 -> RegisterFragment()
        else -> Fragment()
    }

}