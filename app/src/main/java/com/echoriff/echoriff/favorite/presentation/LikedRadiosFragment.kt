package com.echoriff.echoriff.favorite.presentation

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentLikedRadiosBinding
import com.echoriff.echoriff.favorite.domain.LikedRadiosState
import com.echoriff.echoriff.favorite.presentation.adapters.FavoriteRadiosAdapter
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class LikedRadiosFragment : Fragment() {
    private lateinit var binding: FragmentLikedRadiosBinding
    private val likedRadiosViewModel: LikedRadiosViewModel by viewModel()
    private val playerViewModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)
    private lateinit var adapter: FavoriteRadiosAdapter
    private var category: Category? = null
    private var radiosList: List<Radio>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedRadiosBinding.inflate(layoutInflater)

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnBack) { view, insets ->
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

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
//        observeLikedRadioModel()
        setupRadiosRV()
        startTimer()
    }

    private fun observeLikedRadioModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    likedRadiosViewModel.likedRadiosState.collect { state ->
                        when (state) {
                            is LikedRadiosState.Loading -> {
                                binding.progressBar.visibility =
                                    View.VISIBLE
                                binding.likedRadiosRv.visibility = View.GONE
                            }

                            is LikedRadiosState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.likedRadiosRv.visibility = View.VISIBLE
                                radiosList = state.likedRadios
                                setupRadiosAdapter(state.likedRadios)
                                val animation = AnimationUtils.loadLayoutAnimation(
                                    requireContext(),
                                    R.anim.layout_animation
                                )
                                binding.likedRadiosRv.layoutAnimation = animation
                                binding.likedRadiosRv.scheduleLayoutAnimation()
                                binding.tvRadiosNumber.text = "${state.likedRadios.size} radios"
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

    private fun setupRadiosRV() {
        binding.likedRadiosRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun startTimer() {
        object : CountDownTimer(600, 100) { // 600ms duration, ticks every 100ms
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                observeLikedRadioModel()
            }
        }.start()
    }

    private fun setupRadiosAdapter(radios: List<Radio>) {
        adapter = FavoriteRadiosAdapter(radios) { selectedRadio ->
            playerViewModel.playRadio(selectedRadio, category)
        }
        binding.likedRadiosRv.adapter = adapter

        if ((radiosList?.size ?: 0) > 1) {
            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(binding.likedRadiosRv)
        }
    }

    private val simpleCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                Collections.swap(
                    radiosList ?: return false,
                    fromPosition,
                    toPosition
                ) // Swap items in the list
                adapter.notifyItemMoved(fromPosition, toPosition) // Notify adapter

                likedRadiosViewModel.updateRadioList(radiosList ?: return false) // Persist changes

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }
}
