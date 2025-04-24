package com.echoriff.echoriff.admin.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentAdminBinding

class AdminFragment : BaseFragment() {
    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(layoutInflater)

        windowColors(R.color.statusBar, R.color.navBar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnManageUsers.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_manageUserFragment)
        }

        binding.btnManageRadios.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_manageRadiosFragment)
        }
    }
}
