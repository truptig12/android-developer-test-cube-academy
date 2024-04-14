package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cube.cubeacademy.databinding.ActivityMainBinding
import com.cube.cubeacademy.lib.adapters.NominationsRecyclerViewAdapter
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.utils.UiEvent
import com.cube.cubeacademy.viewmodel.NominationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /** Adapter for managing data in the RecyclerView*/
    private lateinit var nominationsAdapter: NominationsRecyclerViewAdapter

    /** ViewModel instance provided by Hilt for managing UI-related data in a lifecycle-conscious way*/
    private val nominationViewModel by viewModels<NominationViewModel>()

    /*  @Inject
      lateinit var repository: Repository*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        populateUI()
    }

    private fun populateUI() {
        setupRecyclerView() // Initialize RecyclerView and its adapter
        createNewNomination() // Setup the listener for the 'Create New Nomination' button

        // Begin observing nomination data from the ViewModel
        nominationViewModel.collectNominations()

        // Collect UI events from the ViewModel to update the UI accordingly
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                nominationViewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.Error -> {
                            Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_LONG)
                                .show()
                            binding.progressBar.visibility = View.GONE
                        }

                        is UiEvent.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is UiEvent.Success -> {
                            binding.progressBar.visibility = View.GONE
                            toggleEmptyState(event.data.isEmpty())
                            nominationsAdapter.submitList(event.data.sortedBy { it.nominee.firstName })
                        }
                    }
                }
            }
        }

    }

    /**
     * Sets up click listener for creating a new nomination.
     */
    private fun createNewNomination() {
        binding.createButton.setOnClickListener {
            val intent = Intent(this, CreateNominationActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Initializes and sets up the RecyclerView.
     */
    private fun setupRecyclerView() {
        nominationsAdapter = NominationsRecyclerViewAdapter()
        binding.nominationsList.layoutManager = LinearLayoutManager(this)
        binding.nominationsList.adapter = nominationsAdapter
    }

    /**
     * Toggles visibility of list and empty state views based on whether the list is empty.
     */
    private fun toggleEmptyState(isEmpty: Boolean) {
        binding.nominationsList.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.emptyContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}