package com.echoriff.echoriff.radio.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.NetworkUtils
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentRadiosBinding
import com.echoriff.echoriff.radio.domain.CategoriesState
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio
import com.echoriff.echoriff.radio.presentation.adapters.CategoriesAdapter
import com.echoriff.echoriff.radio.presentation.adapters.EqualSpaceItemDecoration
import com.echoriff.echoriff.radio.presentation.adapters.RadiosAdapter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RadiosFragment : BaseFragment() {
    private val radioModel: RadiosViewModel by viewModel()
    private val playerViewModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)
    private val userPreferences: UserPreferences by inject()

    private var hasConnected = true
    lateinit var binding: FragmentRadiosBinding
    private var playScreenFragment = PlayerFragment.newInstance()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var radiosAdapter: RadiosAdapter

    private lateinit var networkObserver: NetworkUtils
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRadiosBinding.inflate(layoutInflater)

        val window = requireActivity().window
        window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        windowColors(R.color.transparent, R.color.navBar)

        adjustStatusBarIconsBasedOnBackgroundColor(
            this@RadiosFragment, ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )

        ViewCompat.setOnApplyWindowInsetsListener(binding.categoriesRv) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top + 20
            view.layoutParams = layoutParams
            insets
        }

        userPreferences.clearSelectedCategory()
        playerViewModel.setRecordingsList(userPreferences.loadRecordings(requireContext()))
        val (lastPlayedRadio, lastPlayedCategory) = userPreferences.getLastPlayedRadioWithCategory(
            requireContext()
        )
        if (lastPlayedRadio != null) {
            if (lastPlayedCategory != null) {
                playerViewModel.loadRadioOnce(lastPlayedRadio, lastPlayedCategory)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkObserver = NetworkUtils(requireContext())
        networkObserver.start()

        viewLifecycleOwner.lifecycleScope.launch {
            networkObserver.isConnected.collect { isConnected ->
                if (isConnected) {
                    binding.tvInternet.visibility = View.GONE
                    binding.playScreenFrameLayout.visibility = View.VISIBLE
                    binding.categoriesRv.visibility = View.VISIBLE
                    binding.radiosRv.visibility = View.VISIBLE
                    if (!hasConnected) {
                        val (lastPlayedRadio, lastPlayedCategory) = userPreferences.getLastPlayedRadioWithCategory(
                            requireContext()
                        )
                        if (lastPlayedRadio != null) {
                            if (lastPlayedCategory != null) {
                                playerViewModel.loadRadio(lastPlayedRadio, lastPlayedCategory)
                            }
                        }
                    }

                    observeViewModel()
                } else {
                    binding.tvInternet.visibility = View.VISIBLE
                    binding.radiosRv.visibility = View.GONE
                    binding.categoriesRv.visibility = View.GONE
                    binding.playScreenFrameLayout.visibility = View.GONE
                    hasConnected = false
                }
            }
        }

        setupCategoriesRV(view)
        setupRadiosRV(view)
        observeViewModel()
        setupRadioPlayerFragment()
    }

    private fun observeViewModel() {
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
                        val animation = AnimationUtils.loadLayoutAnimation(
                            requireContext(),
                            R.anim.rv_animation
                        )
                        binding.radiosRv.layoutAnimation = animation
                        binding.radiosRv.scheduleLayoutAnimation()
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
        // Used for fix issue where after minimizing the app and reopen it the first category is always selected
        // although the recycleView shows different radios
        val savedCategoryTitle = userPreferences.getSelectedCategory() // Retrieve saved category
        val selectedIndex = categories.indexOfFirst { it.title == savedCategoryTitle }
        val initialSelectedIndex = if (selectedIndex != -1) selectedIndex else 0

        categoriesAdapter =
            CategoriesAdapter(categories, initialSelectedIndex) { selectedCategory ->
                val selectedIndex = categories.indexOf(selectedCategory)
                scrollToCenter(selectedIndex)
                radioModel.setSelectedCategory(selectedIndex)
            }
        binding.categoriesRv.adapter = categoriesAdapter
    }

    private fun setupRadiosAdapter(radios: List<Radio>) {
        radiosAdapter = RadiosAdapter(radios, { selectedRadio ->
            playerViewModel.playRadio(selectedRadio, radioModel.selectedCategory.value)
            binding.playScreenFrameLayout.visibility = View.VISIBLE
        }, { selectedRadio, actionId ->
            when (actionId) {
                R.id.like_radio -> {
                    playerViewModel.likeRadio(selectedRadio)
                }
            }
        })
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
            return
        }

        if (position == categoriesAdapter.itemCount - 1 && itemView.right > recyclerViewWidth) {
            scrollOffset = itemView.right - recyclerViewWidth
        }

        binding.categoriesRv.smoothScrollBy(scrollOffset, 0)
    }
}