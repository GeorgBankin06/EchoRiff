package com.echoriff.echoriff.admin.presentation

import android.app.Notification
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media.app.NotificationCompat
import androidx.navigation.fragment.findNavController
import com.echoriff.echoriff.R
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.common.presentation.BaseFragment
import com.echoriff.echoriff.databinding.FragmentAdminBinding
import com.echoriff.echoriff.radio.domain.model.Radio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminFragment : BaseFragment() {

    private val adminModel: AdminViewModel by viewModel()
    private val userPreferences: UserPreferences by inject()
    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(layoutInflater)

        setupCornerAnim()
        windowColors(R.color.statusBar, R.color.navBar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        adminModel.loadCategories()

        binding.btnAddRadio.setOnClickListener {
            addRadio()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun addRadio() {
        val radioTitle = binding.etRadioTitle.text.toString()
        val radioStreamUrl = binding.etStreamUrl.text.toString()
        val radioIntro = binding.etRadioIntro.text.toString()
        val radioWebUrl = binding.etWebUrl.text.toString()
        val selectedCategory = binding.spinner.selectedItem.toString()

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
            Toast.makeText(requireContext(), "Fill the info", Toast.LENGTH_SHORT).show()
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
                        binding.spinner.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun setupCornerAnim() {
        setupHintAndCornerAnimation(this@AdminFragment, binding.etRadioTitle, "Radio Title")
        setupHintAndCornerAnimation(this@AdminFragment, binding.etRadioIntro, "Radio Intro")
        setupHintAndCornerAnimation(this@AdminFragment, binding.etWebUrl, "Web Url")
        setupHintAndCornerAnimation(this@AdminFragment, binding.etStreamUrl, "Stream Url")
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        lifecycleScope.launch {
            userPreferences.clearUserRole()
        }
        findNavController().navigate(R.id.auth_nav_graph)
    }
}
