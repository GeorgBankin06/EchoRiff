package com.echoriff.echoriff.favorite.presentation

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
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentLikedRadiosBinding
import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.presentation.adapters.FavoriteRadiosAdapter
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import com.echoriff.echoriff.radio.presentation.adapters.RadiosAdapter
import com.echoriff.echoriff.register.presentation.RegisterViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedRadiosFragment : Fragment() {
    private lateinit var binding: FragmentLikedRadiosBinding
    private val likedRadiosViewModel: LikedRadiosViewModel by viewModel()
    private val playerViewModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)
    private lateinit var adapter: FavoriteRadiosAdapter
    private var category: Category? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedRadiosBinding.inflate(layoutInflater)

        ViewCompat.setOnApplyWindowInsetsListener(binding.likedRadiosRv) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top
            view.layoutParams = layoutParams
            insets
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLikedRadioModel()
        setupRadiosRV(view)
    }

    private fun observeLikedRadioModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    likedRadiosViewModel.likedRadiosState.collect { state ->
                        when (state) {
                            is LikedRadiosState.Loading -> binding.progressBar.visibility =
                                View.VISIBLE

                            is LikedRadiosState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                setupRadiosAdapter(state.likedRadios)
                                category = Category(
                                    bgImgUrl = "favoriteRadios",
                                    title = "favoriteRadios",
                                    radios = state.likedRadios
                                )
                            }

                            is LikedRadiosState.Failure -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    state.messageError,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRadiosRV(view: View) {
        binding.likedRadiosRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setupRadiosAdapter(radios: List<Radio>) {
        adapter = FavoriteRadiosAdapter(radios) { selectedRadio ->
            playerViewModel.playRadio(selectedRadio, category)
        }
        binding.likedRadiosRv.adapter = adapter
    }
}
