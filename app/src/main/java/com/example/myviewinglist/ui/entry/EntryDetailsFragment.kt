package com.example.myviewinglist.ui.entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myviewinglist.R
import com.example.myviewinglist.databinding.FragmentEntryBinding
import com.example.myviewinglist.model.AddedEntryState
import com.example.myviewinglist.model.Entry
import com.example.myviewinglist.model.EntryType
import com.example.myviewinglist.network.ServiceStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class EntryDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEntryBinding
    private val viewModel by lazy { ViewModelProvider(this).get(EntryDetailsViewModel::class.java)}

    private lateinit var selectedState: AddedEntryState
    private lateinit var completeDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val valuesList = it.getStringArray("entryValues")!!
            val entry = Entry(
                valuesList[0],
                valuesList[1],
                EntryType.values()[valuesList[2]!!.toInt()],
                null,
                valuesList[3])

            viewModel.initializeDetailsPage(entry)

            selectedState = AddedEntryState.WAITING
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addedEntryObserver()
        operationStatusObserver()

        viewModel.requestStatus.observe(viewLifecycleOwner, Observer { value ->
            setServiceStatusImage(value)
        })

        binding.stateButton.setOnClickListener {
            showStatePicker()
        }

        binding.annotationButton.setOnClickListener {
            annotationButtonAction(binding.annotationButton.text.toString())
        }
    }

    //region observers
    private fun addedEntryObserver() {
        viewModel.addedEntry.observe(viewLifecycleOwner, Observer {
            setAnnotationButtonText(it.annotation)
            setStateButtonStyle(it.state)
            setCompleteDateVisibility(it.state)
        })
    }

    private fun operationStatusObserver() {
        viewModel.operationStatus.observe(viewLifecycleOwner, Observer { value ->
            createOperationSnackBar(value)
        })
    }
    //endregion

    private fun setServiceStatusImage(status: ServiceStatus) {
        val statusImg = binding.statusImage
        when (status) {
            ServiceStatus.LOADING -> {
                Log.d("Service", "Service in loading status")
                statusImg.visibility = View.VISIBLE
                statusImg.setImageResource(R.drawable.loading_animation)
            }
            ServiceStatus.ERROR -> {
                statusImg.visibility = View.VISIBLE
                statusImg.setImageResource(R.drawable.ic_connection_error)
            }
            ServiceStatus.DONE -> {
                statusImg.visibility = View.GONE
            }
        }
    }

    private fun setCompleteDateVisibility(state: AddedEntryState?) {
        val completeDateLayout = binding.completeDateLayout

        when (state) {
            AddedEntryState.COMPLETED -> completeDateLayout.visibility = View.VISIBLE
            else -> completeDateLayout.visibility = View.GONE
        }
    }

    private fun setStateButtonStyle(state: AddedEntryState?) {
        val stateButton = binding.stateButton
        Log.d("debug","added entry status ${state?.name}")
        when (state) {
            AddedEntryState.COMPLETED -> applyCompletedStyle(stateButton)
            AddedEntryState.VIEWING -> applyViewingStyle(stateButton)
            AddedEntryState.WAITING -> applyWaitingStyle(stateButton)
            AddedEntryState.DROPPED -> applyDroppedStyle(stateButton)
            else -> { }
        }
    }

    //region annotation
    private fun annotationButtonAction(annotation: String?) {
        when (binding.annotationButton.text) {
            getString(R.string.entry_annotation_update) -> {
                setAnnotationVisibility(true)
                viewModel.updateAddedEntry(
                    null, null, binding.annotationEditValue.text.toString())
            }
            else -> {
                setAnnotationVisibility(false)
            }
        }
    }

    private fun setAnnotationVisibility(save: Boolean) {
        if (save) {
            binding.annotationText.visibility = View.VISIBLE
            binding.annotationEditLayout.visibility = View.GONE
            binding.annotationButton.text = getString(R.string.entry_annotation_edit)

            binding.annotationText.text = binding.annotationEditValue.text.toString()

        } else {
            binding.annotationText.visibility = View.GONE
            binding.annotationEditLayout.visibility = View.VISIBLE
            binding.annotationButton.text = getString(R.string.entry_annotation_update)
        }
    }

    private fun setAnnotationButtonText(annotation: String?) {
        //se llama solo al principio
        if (annotation == null) {
            binding.annotationButton.text = getString(R.string.entry_annotation_add)
        }
        else {
            binding.annotationButton.text = getString(R.string.entry_annotation_edit)
        }
    }
    //endregion

    //region pickers
    private fun showStatePicker() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.states_title))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    when (selectedState) {
                        AddedEntryState.COMPLETED -> {
                            showDatePicker()
                        }
                        else -> {
                            setStateButtonStyle(selectedState)
                            viewModel.updateAddedEntry(selectedState)
                            setCompleteDateVisibility(selectedState)
                        }
                    }
            }
            .setSingleChoiceItems(resources.getStringArray(R.array.entry_state), -1)
            { _, which ->
                selectedState = AddedEntryState.values()[which]
            }
            .show()
    }

    private fun showDatePicker() {
        val date = stringDateToLong(viewModel.entry.value?.publication!!)

        val constraintBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(date))

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.date_null_error)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            completeDate = longToStringDate(it)
            setStateButtonStyle(selectedState)
            viewModel.updateAddedEntry(selectedState, completeDate)
            setCompleteDateVisibility(selectedState)
        }

        datePicker.show(childFragmentManager, "datePicker")
    }
    //endregion

    private fun createOperationSnackBar(state: DetailsOpState) {
        val message =
            when (state) {
                DetailsOpState.SAVE_COMPLETED -> getString(R.string.add_success)
                DetailsOpState.SAVE_ERROR -> getString(R.string.add_error)

                DetailsOpState.UPDATE_COMPLETED -> getString(R.string.update_success)
                DetailsOpState.UPDATE_ERROR -> getString(R.string.update_error)

                DetailsOpState.IMPOSSIBLE_OPERATION -> getString(R.string.save_error)
                else -> ""
            }

        if (message != "") {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
            viewModel.resetOperationState()
        }
    }

    //region utils
    private fun longToStringDate(time: Long) : String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    private fun stringDateToLong(date: String) : Long {
        val df = SimpleDateFormat("dd/MM/yyyy")
        return df.parse(date).time
    }
    //endregion

    //region state styles
    private fun applyCompletedStyle(buttonView: MaterialButton) {
        with(buttonView) {
            setText(R.string.completed_name)
            setTextColor(resources.getColor(R.color.secondaryTextColor))
            setBackgroundColor(resources.getColor(R.color.completedColor))
        }
    }

    private fun applyViewingStyle(buttonView: MaterialButton) {
        with(buttonView) {
            setText(com.example.myviewinglist.R.string.viewing_name)
            setTextColor(resources.getColor(com.example.myviewinglist.R.color.primaryTextColor))
            setBackgroundColor(resources.getColor(com.example.myviewinglist.R.color.viewingColor))
        }
    }

    private fun applyWaitingStyle(buttonView: MaterialButton) {
        with(buttonView) {
            setText(com.example.myviewinglist.R.string.waiting_name)
            setTextColor(resources.getColor(com.example.myviewinglist.R.color.primaryTextColor))
            setBackgroundColor(resources.getColor(com.example.myviewinglist.R.color.waitingColor))
        }
    }

    private fun applyDroppedStyle(buttonView: MaterialButton) {
        with(buttonView) {
            setText(com.example.myviewinglist.R.string.dropped_name)
            setTextColor(resources.getColor(com.example.myviewinglist.R.color.primaryTextColor))
            setBackgroundColor(resources.getColor(com.example.myviewinglist.R.color.droppedColor))
        }
    }

    //endregion
}