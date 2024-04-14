package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityCreateNominationBinding
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.FormState
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utils.UiEvent
import com.cube.cubeacademy.viewmodel.CreateNominationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateNominationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNominationBinding
    private val createNominationViewModel by viewModels<CreateNominationViewModel>()

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNominationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        setupListeners()
        //populateUI()
    }


    private fun setupObservers() {
        createNominationViewModel.collectNominees()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    createNominationViewModel.events.collect { event ->
                        when (event) {

                            is UiEvent.Error -> {
                                Toast.makeText(
                                    this@CreateNominationActivity,
                                    event.message,
                                    Toast.LENGTH_LONG
                                ).show()
//                            binding.progressBar.visibility = View.GONE
                            }

                            is UiEvent.Loading -> {
                                // binding.progressBar.visibility =  View.VISIBLE
                            }

                            is UiEvent.Success -> {

                                updateSpinnerUI(event.data)
                            }
                        }
                    }
                }

                launch {
                    createNominationViewModel.formState.collect { formState ->
                        updateFormState(formState)
                    }
                }

                launch {
                    createNominationViewModel.nominationEvent.collect { nominationEvent ->
                        handleNominationState(nominationEvent)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.etReason.addTextChangedListener { createNominationViewModel.validateReason(it.toString()) }
        binding.myRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val process = findViewById<RadioButton>(checkedId).text.toString()
            createNominationViewModel.validateProcess(process)
        }
        binding.submitButton.setOnClickListener { createNominationViewModel.submitData() }
    }

    private fun showLoading() {
        // Show loading indicator logic
    }

    private fun showSuccess() {
        startActivity(Intent(this, NominationSubmittedActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun updateFormState(formState: FormState) {
        binding.submitButton.isEnabled = formState.isSubmitEnabled
        binding.backButton.setOnClickListener {
            if (formState.isValueAdded) showBottomSheet() else finish()
        }
    }

    private fun handleNominationState(resource: UiEvent<Nomination>) {
        when (resource) {
            is UiEvent.Error -> showError(resource.message)
            is UiEvent.Loading -> showLoading()
            is UiEvent.Success -> showSuccess()
        }
    }

    private fun updateSpinnerUI(nominees: List<Nominee>) {
        val nomineesWithPrompt = listOf(Nominee("0", "Select", "Option")) + nominees
        binding.cubeNameList.setItems(nomineesWithPrompt)
        binding.cubeNameList.setNomineeSelectedListener(NomineeSelectedListener())

    }

    private fun populateUI() {
        /**
         * TODO: Populate the form after having added the views to the xml file (Look for TODO comments in the xml file)
         * 		 Add the logic for the views and at the end, add the logic to create the new nomination using the api
         * 		 The nominees drop down list items should come from the api (By fetching the nominee list)
         */
    }

    inner class NomineeSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val nomineeId =
                (parent.getItemAtPosition(position) as Nominee).nomineeId.takeIf { position > 0 }
            createNominationViewModel.validateNominee(nomineeId)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    private fun showBottomSheet() {
        BottomSheetDialog(this).apply {
            setContentView(layoutInflater.inflate(R.layout.layout_bottom_sheet, null).apply {
                findViewById<Button>(R.id.leave_page).setOnClickListener {
                    finish()
                    dismiss()
                }
                findViewById<Button>(R.id.cancel).setOnClickListener { dismiss() }
            })
            setCancelable(false)
            show()
        }
    }
}