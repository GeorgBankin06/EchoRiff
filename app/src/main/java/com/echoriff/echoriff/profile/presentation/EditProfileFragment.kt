package com.echoriff.echoriff.profile.presentation

import android.content.Intent
import android.icu.text.Edits
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.databinding.FragmentEditProfileBinding
import com.echoriff.echoriff.profile.domain.EditState
import com.echoriff.echoriff.profile.domain.ProfileState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditProfileFragment : Fragment() {
    lateinit var binding: FragmentEditProfileBinding
    private val profileViewModel: ProfileViewModel by viewModel()
    var image: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
                binding.ivProfile.setImageURI(uri)
                image = uri
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error selecting image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)

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

        profileViewModel.fetchUserInfo()
        observeViewModel()

        binding.btnSave.setOnClickListener {
            val user = User(
                firstName = binding.etName.text.toString(),
                lastName = binding.etLastname.text.toString(),
                email = binding.etEmail.text.toString()
            )
            profileViewModel.updateUserInfo(user, image)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    profileViewModel.user.collect { state ->
                        when (state) {
                            is ProfileState.Loading -> {}

                            is ProfileState.Success -> {
                                setInfo(state.user)
                            }

                            is ProfileState.Failure -> {

                            }
                        }
                    }
                }
                launch {
                    profileViewModel.userInfo.collect { state ->
                        when (state) {
                            is EditState.Loading -> {
                                binding.indicator.show()
                                binding.tilName.isEnabled = false
                                binding.tilLastname.isEnabled = false
                                binding.tilEmail.isEnabled = false
                            }

                            is EditState.Success -> {
                                binding.indicator.hide()
                                findNavController().navigateUp()
                                profileViewModel.fetchUserInfo()
                            }

                            is EditState.Failure -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setInfo(user: User) {
        Glide.with(binding.ivProfile.context)
            .load(user.profileImage)
            .placeholder(R.drawable.player_background)
            .into(binding.ivProfile)

        binding.ivProfile.apply {
            alpha = 0f

            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }

        binding.etName.apply {
            alpha = 0f
            setText(user.firstName)

            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }

        binding.etLastname.apply {
            alpha = 0f
            setText(user.lastName)

            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }

        binding.etEmail.apply {
            alpha = 0f
            binding.tilEmail.animate().alpha(1f)
            setText(user.email)
            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }
    }
}