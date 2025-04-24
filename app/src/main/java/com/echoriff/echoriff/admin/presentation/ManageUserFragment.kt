package com.echoriff.echoriff.admin.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.admin.domain.ChangeUserRoleState
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentManageUserBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageUserFragment : BaseFragment() {
    lateinit var binding: FragmentManageUserBinding
    private val adminModel: AdminViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageUserBinding.inflate(layoutInflater)

        windowColors(R.color.transparent, R.color.navBar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnBack) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top + 20
            view.layoutParams = layoutParams
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.tvEdit) { view, insets ->
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

        setupCornerAnim()
        setupSpinnerAdapter()
        observeViewModel()

       changeUserRole()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun changeUserRole() {
        binding.btnChangeRole.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val selectedRole = binding.dropdownMenu.text.toString().trim()

            if (email.isNotEmpty() && selectedRole.isNotEmpty()) {
                adminModel.changeUserCase(email, selectedRole)
            } else {
                Toast.makeText(requireContext(), "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinnerAdapter() {
        val roles = listOf("admin", "user")
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            roles
        )
        binding.dropdownMenu.setAdapter(adapter)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    adminModel.changeUserRole.collect { result ->
                        when (result) {
                            is ChangeUserRoleState.Success -> {
                                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            is ChangeUserRoleState.Failure -> {
                                Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            is ChangeUserRoleState.Loading -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setupCornerAnim() {
        setupHintAndCornerAnimation(this@ManageUserFragment, binding.etEmail, "Radio Title")
    }
}