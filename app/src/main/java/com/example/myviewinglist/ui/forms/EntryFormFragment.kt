package com.example.myviewinglist.ui.forms

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.myviewinglist.R
import com.example.myviewinglist.databinding.FragmentEntryFormBinding
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class State { VIEWING, COMPLETED, DROPPED, HOLD}

class EntryFormFragment : Fragment() {

    private lateinit var binding: FragmentEntryFormBinding
    private val viewModel: EntryFormViewModel by viewModels()

    private var datePickerOwner: TextInputEditText? = null
    private var statePicked = State.COMPLETED

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private var datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.date_null_error)
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntryFormBinding.inflate(inflater)

        viewModel.entryAdded.observe(viewLifecycleOwner, Observer { value ->
            if (value == 1) {
                createSnackBar(true)
                viewModel.resetEntryAdded()
            } else if (value == -1) {
                createSnackBar(false)
                viewModel.resetEntryAdded()
            }
        })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Dropdown tipo de Entry
        val adapter =
            ArrayAdapter.createFromResource(
                requireContext(), R.array.entry_type, R.layout.basic_list_item)
        (binding.entryType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //Botones de estado
        binding.toggleButton.addOnButtonCheckedListener(
                MaterialButtonToggleGroup.OnButtonCheckedListener {
                    _, checkedId, isChecked -> toggleButtonManager(checkedId, isChecked) })

        //Date picker
        binding.publicationDateValue.setOnClickListener {
            openDatePicker()
            datePickerOwner = binding.publicationDateValue
        }
        binding.completeDate.setOnClickListener {
            openDatePicker()
            datePickerOwner = binding.completeDateValue
        }

        datePicker.addOnPositiveButtonClickListener {
            val myInstant = Instant.ofEpochMilli(it)
            val myDate = LocalDateTime.ofInstant(myInstant, ZoneId.systemDefault()).toLocalDate()

            localDateToString(myDate)
        }

        //Boton de completado
        binding.addButton.setOnClickListener { checkFields() }

        //Entrada añadida?

    }

    private fun toggleButtonManager(buttonId: Int, isChecked: Boolean) {
        if (isChecked) {
            when (buttonId) {
                R.id.toggleCompleted -> {
                    binding.completeDate.visibility = View.VISIBLE
                    statePicked = State.COMPLETED
                }
                R.id.toggleDropped -> {
                    binding.completeDate.visibility = View.GONE
                    statePicked = State.DROPPED
                }
                R.id.toggleViewing -> {
                    statePicked = State.VIEWING
                    binding.completeDate.visibility = View.GONE
                }
                else -> {
                    statePicked = State.HOLD
                    binding.completeDate.visibility = View.GONE
                }
            }
        }
    }

    private fun openDatePicker() {
        datePicker.show(childFragmentManager, "datePicker")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun localDateToString(date: LocalDate) {
        val parsedDate = date.format(formatter)

        datePickerOwner?.setText(parsedDate)
    }

    @SuppressLint("ShowToast")
    private fun createSnackBar(state: Boolean) {

        val message: String = if (state) {
            getString(R.string.entry_created)
        } else {
            getString(R.string.entry_fail)
        }

        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.nav_view)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkFields() {
        var canAdd = true

        val name = binding.entryNameValue.text.toString()
        if (name == "") {
            canAdd = false
            binding.entryName.error = getString(R.string.name_error)
        } else {
            binding.entryName.error = null
        }

        val type = binding.entryTypeValue.text.toString()
        if (type == "") {
            canAdd = false
            binding.entryType.error = getString(R.string.type_error)
        } else {
            binding.entryType.error = null
        }

        //val state = statePicked
        val publicationDate = binding.publicationDateValue.text.toString()
        if (publicationDate == "") {
            canAdd = false
            binding.publicationDate.error = getString(R.string.date_null_error)
        }
        else {
            binding.publicationDate.error = null
        }

        if (canAdd) {
            Log.d("Dbug", "Datos enviados al view model")
            viewModel.createNewEntry(name, type, publicationDate)
        }
        else {
            Log.d("Dbug", "No se pueden enviar los datos")
        }
    }
}