package com.echoriff.echoriff.favorite.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.databinding.FragmentRecordsBinding
import com.echoriff.echoriff.favorite.presentation.adapters.RecordsAdapter
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class RecordsFragment : Fragment() {
    lateinit var binding:FragmentRecordsBinding
    private val playerViewModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecords.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        val recordings = UserPreferences(requireContext()).loadRecordings(requireContext())
        val adapter = RecordsAdapter(recordings) { selected ->
            playerViewModel.setRecordingsList(recordings)
            playerViewModel.playRecording(selected)
        }
        binding.rvRecords.adapter = adapter
    }
}