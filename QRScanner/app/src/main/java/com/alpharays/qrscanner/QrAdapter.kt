package com.alpharays.qrscanner

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alpharays.qrscanner.fragment.QrGeneratorFragment
import com.alpharays.qrscanner.fragment.QrScannerFragment

class QrAdapter(fragmentActivity: MainActivity) : FragmentStateAdapter(fragmentActivity) {
    // no. of fragment count
    override fun getItemCount() = 2

    // creating fragments
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> QrGeneratorFragment()
            1 -> QrScannerFragment()
            else ->  throw Resources.NotFoundException("Position not found")
        }
    }
}