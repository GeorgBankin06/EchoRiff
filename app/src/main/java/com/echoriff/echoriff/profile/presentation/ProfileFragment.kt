package com.echoriff.echoriff.profile.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.echoriff.echoriff.MainActivity
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.databinding.FragmentProfileBinding
import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.domain.LikedSongsState
import com.echoriff.echoriff.favorite.presentation.LikedRadiosViewModel
import com.echoriff.echoriff.favorite.presentation.LikedSongsViewModel
import com.echoriff.echoriff.profile.domain.ProfileState
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private val profileModel: ProfileViewModel by viewModel()
    private val likedRadiosViewModel: LikedRadiosViewModel by viewModel()
    private val likedSongsViewModel: LikedSongsViewModel by viewModel()

    lateinit var binding: FragmentProfileBinding
    private val userPreferences: UserPreferences by inject()
    private val playerViewModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        ViewCompat.setOnApplyWindowInsetsListener(binding.tvAccount) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top + 20
            view.layoutParams = layoutParams
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnEdit) { view, insets ->
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

        observeViewModel()

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_viewProfileFragment)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)

        }

        binding.cardLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            lifecycleScope.launch {
                userPreferences.clearUserRole()
                userPreferences.clearLastPlayedRadio()
                playerViewModel.pause()
            }
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    profileModel.user.collect { state ->
                        when (state) {
                            is ProfileState.Loading -> {

                            }

                            is ProfileState.Success -> {
                                setupUserInfo(state.user)
                            }

                            is ProfileState.Failure -> {
                                Toast.makeText(
                                    requireContext(),
                                    "${state.error}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                launch {
                    likedRadiosViewModel.likedRadiosState.collect { state ->
                        if (state is LikedRadiosState.Success) {
                            val count = state.likedRadios.size
                            binding.tvRadiosCount.apply {
                                alpha = 0f
                                text = "$count radios"

                                animate()
                                    .alpha(1f)
                                    .setDuration(500)
                                    .start()
                            }
                        }
                    }
                }
                launch {
                    likedSongsViewModel.likedSongsState.collect { state ->
                        if (state is LikedSongsState.Success) {
                            val count = state.likedSongs.size
                            binding.tvSongCount.apply {
                                alpha = 0f
                                text = "$count songs"

                                animate()
                                    .alpha(1f)
                                    .setDuration(500)
                                    .start()
                            }
                        }
                    }
                }
                launch {
                    binding.tvRecordsCount.apply {
                        alpha = 0f
                        text = "${0} records"

                        animate()
                            .alpha(1f)
                            .setDuration(500)
                            .start()
                    }
                }
            }
        }
    }

    private fun setupUserInfo(user: User) {
        Glide.with(binding.ivProfile.context)
            .load(user.profileImage)
            .placeholder(R.drawable.player_background)
            .into(binding.ivProfile)

        binding.ivProfile.apply {
            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }

        binding.tvEmail.apply {
            alpha = 0f
            text = user.email

            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }

        binding.tvFullName.apply {
            alpha = 0f
            text = "${user.firstName} ${user.lastName}"

            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }
    }
}
