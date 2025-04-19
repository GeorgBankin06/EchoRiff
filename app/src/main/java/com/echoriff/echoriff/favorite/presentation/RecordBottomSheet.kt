package com.echoriff.echoriff.favorite.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.echoriff.echoriff.R
import com.echoriff.echoriff.databinding.BottomSheetRecordBinding
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import com.echoriff.echoriff.radio.service.RecordingService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.navigation.koinNavGraphViewModel

class RecordBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetRecordBinding
    private val playerModel: PlayerViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetRecordBinding.inflate(layoutInflater)

        binding.btnSave.setOnClickListener {
            if (!binding.etSaveName.text.isNullOrEmpty()) {
                val name = binding.etSaveName.text.toString()
                val intent = Intent(requireContext(), RecordingService::class.java).apply {
                    action = RecordingService.ACTION_STOP
                    putExtra(RecordingService.EXTRA_RECORD_NAME, name)
                }
                requireContext().startService(intent)
                playerModel.isRecording = false
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Enter recording name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}