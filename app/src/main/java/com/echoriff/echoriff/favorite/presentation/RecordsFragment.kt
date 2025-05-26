package com.echoriff.echoriff.favorite.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.databinding.FragmentRecordsBinding
import com.echoriff.echoriff.favorite.presentation.adapters.RecordsAdapter
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.navigation.koinNavGraphViewModel

class RecordsFragment : Fragment() {
    lateinit var binding: FragmentRecordsBinding
    private lateinit var adapter: RecordsAdapter
    private val playerViewModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)
    private val userPreferences: UserPreferences by inject()
    private var records = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordsBinding.inflate(layoutInflater)
        val animation = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            R.anim.rv_animation
        )
        binding.rvRecords.layoutAnimation = animation
        binding.rvRecords.scheduleLayoutAnimation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observePlayerModel()

        binding.rvRecords.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        val recordings = userPreferences.loadRecordings(requireContext())
        adapter = RecordsAdapter(records = recordings, onRecordClick = { selected ->
            playerViewModel.playRecording(selected)
        }, onButtonClick = { deleteRecord ->
            userPreferences.deleteRecordByPath(deleteRecord.filePath)
            adapter.removeItem(deleteRecord)

            binding.tvRecordsNumber.text = "${--records} Records"
        })
        binding.rvRecords.adapter = adapter

        val animation = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            R.anim.rv_animation
        )
        binding.rvRecords.layoutAnimation = animation
        binding.rvRecords.scheduleLayoutAnimation()
    }

    private fun observePlayerModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    playerViewModel.recordsList.collect { size ->
                        records = size.size
                        binding.tvRecordsNumber.text = "$records Records"
                    }
                }
            }
        }
    }
}