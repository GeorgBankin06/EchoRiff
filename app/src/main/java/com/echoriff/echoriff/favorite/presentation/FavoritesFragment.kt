package com.echoriff.echoriff.favorite.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.echoriff.echoriff.databinding.FragmentFavoritesBinding
import com.echoriff.echoriff.favorite.presentation.adapters.ViewPagerAdapter

class FavoritesFragment : Fragment() {
    lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.textView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top + 20
            view.layoutParams = layoutParams
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(LikedRadiosFragment(), "Radios")
        adapter.addFragment(LikedSongsFragment(), "Songs")
        adapter.addFragment(RecordsFragment(), "Records")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }
}