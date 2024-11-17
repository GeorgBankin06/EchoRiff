package com.echoriff.echoriff.radio.presentation

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.FragmentRadiosBinding
import com.echoriff.echoriff.radio.domain.CategoriesState
import com.echoriff.echoriff.radio.domain.Category
import com.echoriff.echoriff.radio.domain.Radio
import com.echoriff.echoriff.radio.presentation.adapters.CategoriesAdapter
import com.echoriff.echoriff.radio.presentation.adapters.EqualSpaceItemDecoration
import com.echoriff.echoriff.radio.presentation.adapters.RadiosAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RadiosFragment : Fragment() {

    private val radioModel: RadiosViewModel by viewModel()
    private val playerViewModel: PlayerViewModel by navGraphViewModels(R.id.main_nav_graph)

    lateinit var binding: FragmentRadiosBinding
    private var playScreenFragment = PlayerFragment.newInstance()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var radiosAdapter: RadiosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRadiosBinding.inflate(layoutInflater)

        ViewCompat.setOnApplyWindowInsetsListener(binding.categoriesRv) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top + 20
            view.layoutParams = layoutParams
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.playScreenFrameLayout) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = systemBarsInsets.bottom
            view.layoutParams = layoutParams
            insets
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRadioPlayerFragment()
        setupCategoriesRV(view)
        setupRadiosRV(view)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    radioModel.categories.collect { categoriesState ->
                        when (categoriesState) {
                            CategoriesState.Failure -> {}
                            CategoriesState.Loading -> {}
                            is CategoriesState.Success -> {
                                setupCategoriesAdapter(categoriesState.categories)
                            }
                        }
                    }
                }
                launch {
                    radioModel.selectedCategory.collect {
                        setupRadiosAdapter(it?.radios ?: emptyList())
                    }
                }
            }
        }
    }

    private fun setupRadioPlayerFragment() {
        requireFragmentManager().beginTransaction()
            .replace(R.id.play_screen_frame_layout, playScreenFragment, PlayerFragment.TAG)
            .commitAllowingStateLoss()
    }

    private fun setupCategoriesRV(view: View) {
        binding.categoriesRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.categoriesRv.addItemDecoration(
            EqualSpaceItemDecoration(
                resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
            )
        )
    }

    private fun setupRadiosRV(view: View) {
        binding.radiosRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setupCategoriesAdapter(categories: List<Category>) {
        categoriesAdapter = CategoriesAdapter(categories) { selectedCategory ->
            val selectedIndex = categories.indexOf(selectedCategory)
            scrollToCenter(selectedIndex)
            radioModel.setSelectedCategory(selectedIndex)
        }
        binding.categoriesRv.adapter = categoriesAdapter
    }

    private fun setupRadiosAdapter(radios: List<Radio>) {
        radiosAdapter = RadiosAdapter(radios) { selectedRadio ->
            playerViewModel.playRadio(selectedRadio, radioModel.selectedCategory.value)
        }
        binding.radiosRv.adapter = radiosAdapter
    }
    private fun scrollToCenter(position: Int) {
        val layoutManager = binding.categoriesRv.layoutManager as LinearLayoutManager
        val itemView = layoutManager.findViewByPosition(position) ?: return
        val recyclerViewWidth = binding.categoriesRv.width
        val recyclerViewCenterX = recyclerViewWidth / 2
        val itemCenterX = (itemView.left + itemView.right) / 2
        var scrollOffset = itemCenterX - recyclerViewCenterX
        if (position == 0 && itemView.left < 0) {
            binding.categoriesRv.smoothScrollBy(0, 0)
        }
        if (position == categoriesAdapter.itemCount - 1 && itemView.right > recyclerViewWidth) {
            scrollOffset = itemView.right - recyclerViewWidth
        }
        val targetScrollX = binding.categoriesRv.computeHorizontalScrollOffset() + scrollOffset
        val animator =
            ValueAnimator.ofInt(binding.categoriesRv.computeHorizontalScrollOffset(), targetScrollX)
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            binding.categoriesRv.scrollBy(
                animatedValue - binding.categoriesRv.computeHorizontalScrollOffset(),
                0
            )
        }
        animator.duration = 500
        animator.start()
    }

}