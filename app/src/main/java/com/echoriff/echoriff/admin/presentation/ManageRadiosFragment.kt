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
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentManageRadiosBinding
import com.echoriff.echoriff.radio.domain.model.Radio
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageRadiosFragment : BaseFragment() {
    lateinit var binding: FragmentManageRadiosBinding
    private val adminModel: AdminViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageRadiosBinding.inflate(layoutInflater)

        setupCornerAnim()
        windowColors(R.color.statusBar, R.color.navBar)

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

        observeViewModel()
        adminModel.loadCategories()

        binding.btnAddRadio.setOnClickListener {
            addRadio()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun addRadio() {
        val radioTitle = binding.etRadioTitle.text.toString()
        val radioStreamUrl = binding.etStreamUrl.text.toString()
        val radioIntro = binding.etRadioIntro.text.toString()
        val radioWebUrl = binding.etWebUrl.text.toString()
        val selectedCategory = binding.dropdownMenu.text.toString().trim()

        val newRadio = Radio(
            title = radioTitle,
            streamUrl = radioStreamUrl,
            coverArtUrl = "",
            intro = radioIntro,
            webUrl = radioWebUrl
        )
        if (selectedCategory.isNotEmpty()) {
            adminModel.addRadioToCategory(selectedCategory, newRadio)
            Toast.makeText(requireContext(), "Radio Added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    adminModel.categories.collect { categoryList ->
                        val titles = categoryList.map { it.title }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.spinner_item,
                            titles
                        )
                        binding.dropdownMenu.setAdapter(adapter)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun setupCornerAnim() {
        setupHintAndCornerAnimation(this@ManageRadiosFragment, binding.etRadioTitle, "Radio Title")
        setupHintAndCornerAnimation(this@ManageRadiosFragment, binding.etRadioIntro, "Radio Intro")
        setupHintAndCornerAnimation(this@ManageRadiosFragment, binding.etWebUrl, "Web Url")
        setupHintAndCornerAnimation(this@ManageRadiosFragment, binding.etStreamUrl, "Stream Url")
    }
}