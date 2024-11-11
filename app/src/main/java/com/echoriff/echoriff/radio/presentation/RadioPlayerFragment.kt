package com.echoriff.echoriff.radio.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentRadioPlayerBinding
import com.echoriff.echoriff.databinding.FragmentRadiosBinding

class RadioPlayerFragment : Fragment() {
    lateinit var binding: FragmentRadioPlayerBinding

    companion object {
        const val TAG = "RadioPlayerFragment"
        fun newInstance(): RadioPlayerFragment {
            val args = Bundle()
            val playScreenFragment = RadioPlayerFragment()
            playScreenFragment.arguments = args
            return playScreenFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRadioPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}