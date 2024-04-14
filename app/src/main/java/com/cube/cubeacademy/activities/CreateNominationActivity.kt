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

    // ViewModel instance provided by Hilt, encapsulating the business logic of the activity
    private val createNominationViewModel by viewModels<CreateNominationViewModel>()

    /*  @Inject
      lateinit var repository: Repository*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNominationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        populateUI()
    }


    /**
     * Set up observers and response handlers to react to data and state changes.
     */
    private fun setupObservers() {
        createNominationViewModel.collectNominees()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    createNominationViewModel.events.collect { event ->
                        when (event) {

                            is UiEvent.Error -> showError(event.message)
                            is UiEvent.Loading -> showLoading()
                            is UiEvent.Success -> {
                                binding.progressBar.visibility = View.GONE
                                updateSpinnerUI(event.data)
                            }
                        }
                    }
                }

                // Observing changes in form state to enable/disable submit button based on form validation
                launch {
                    createNominationViewModel.formState.collect { formState ->
                        updateFormState(formState)
                    }
                }

                launch {// Handling response to the submission of a nomination
                    createNominationViewModel.nominationEvent.collect { nominationEvent ->
                        handleNominationState(nominationEvent)
                    }
                }
            }
        }
    }

    /**
     * Sets up listeners for UI elements to react to user interactions.
     */
    private fun setupListeners() {
        binding.etReason.addTextChangedListener { createNominationViewModel.validateReason(it.toString()) }
        binding.myRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val process = findViewById<RadioButton>(checkedId).text.toString()
            createNominationViewModel.validateProcess(process)
        }
        binding.submitButton.setOnClickListener { createNominationViewModel.submitData() }
    }

    /**
     * Shows a loading indicator while data is being processed.
     */
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }


    /**
     * Handles successful nomination submission by navigating to the success activity.
     */
    private fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        startActivity(Intent(this, NominationSubmittedActivity::class.java))
        finish()
    }

    /**
     * Shows an error message using a Toast.
     */
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Updates the form state based on the validation of form fields.
     */
    private fun updateFormState(formState: FormState) {
        binding.submitButton.isEnabled = formState.isSubmitEnabled
        binding.backButton.setOnClickListener {
            if (formState.isValueAdded) showBottomSheet() else finish()
        }
    }

    /**
     * Manages the UI response based on the nomination creation state.
     */
    private fun handleNominationState(resource: UiEvent<Nomination>) {
        when (resource) {
            is UiEvent.Error -> showError(resource.message)
            is UiEvent.Loading -> showLoading()
            is UiEvent.Success -> showSuccess()
        }
    }

    /**
     * Updates the nominees spinner with the list of nominees.
     */
    private fun updateSpinnerUI(nominees: List<Nominee>) {
        val nomineesWithPrompt = listOf(Nominee("0", "Select", "Option")) + nominees
        binding.cubeNameList.setItems(nomineesWithPrompt)
        binding.cubeNameList.setNomineeSelectedListener(NomineeSelectedListener())

    }

    /**
     * Sets up the UI components and binds observers and listeners.
     */
    private fun populateUI() {
        setupObservers()
        setupListeners()
    }


    /**
     * Inner class to handle item selection in the spinner for nominee selection.
     */
    inner class NomineeSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val nomineeId =
                (parent.getItemAtPosition(position) as Nominee).nomineeId.takeIf { position > 0 }
            createNominationViewModel.validateNominee(nomineeId)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }


    /**
     * Shows a bottom sheet dialog to confirm leaving the page when the back button is pressed.
     */
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