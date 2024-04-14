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

    private lateinit var nominationsAdapter: NominationsRecyclerViewAdapter
    private val nominationViewModel by viewModels<NominationViewModel>()

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        populateUI()
    }

    private fun populateUI() {
        setupRecyclerView()
        createNewNomination()

        nominationViewModel.collectNominations()
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
                            toggleEmptyState(event.data.isEmpty())
                            nominationsAdapter.submitList(event.data.sortedBy { it.nominee.firstName })
                        }
                    }
                }
            }
        }

    }

    private fun createNewNomination() {
        binding.createButton.setOnClickListener {
            val intent = Intent(this, CreateNominationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        nominationsAdapter = NominationsRecyclerViewAdapter()
        binding.nominationsList.layoutManager = LinearLayoutManager(this)
        binding.nominationsList.adapter = nominationsAdapter
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        binding.nominationsList.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.emptyContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}