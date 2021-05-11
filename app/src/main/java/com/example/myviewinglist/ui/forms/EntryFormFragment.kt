package com.example.myviewinglist.ui.forms

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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

    private var datePickerOwner: TextInputEditText? = null
    private var statePicked = State.COMPLETED

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private var datePicker =
            MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.date_null_error)
                    .build()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntryFormBinding.inflate(inflater)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Dropdown tipo de Entry
        val adapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.entry_type, R.layout.list_item)
        (binding.entryType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //Botones de estado
        binding.toggleButton.addOnButtonCheckedListener(
                MaterialButtonToggleGroup.OnButtonCheckedListener {
                    _, checkedId, isChecked -> toggleButtonManager(checkedId, isChecked) })

        //Date picker
        binding.publicationDate.setEndIconOnClickListener {
            openDatePicker()
            datePickerOwner = binding.publicationDateValue
        }
        binding.completeDate.setEndIconOnClickListener {
            openDatePicker()
            datePickerOwner = binding.completeDateValue
        }

        datePicker.addOnPositiveButtonClickListener {
            val myInstant = Instant.ofEpochMilli(it)
            val myDate = LocalDateTime.ofInstant(myInstant, ZoneId.systemDefault()).toLocalDate()

            localDateToString(myDate)
        }

        //Boton de completado
        binding.addButton.setOnClickListener { checkFormFields() }
    }

    private fun toggleButtonManager(buttonId: Int, isChecked: Boolean) {
        if (isChecked) {
            if (buttonId == R.id.toggleCompleted) {
                binding.completeDate.visibility = View.VISIBLE
                statePicked = State.COMPLETED
            }
            else if (buttonId == R.id.toggleDropped){
                binding.completeDate.visibility = View.GONE
                statePicked = State.DROPPED
            }
            else if (buttonId == R.id.toggleViewing){
                statePicked = State.VIEWING
                binding.completeDate.visibility = View.GONE
            }
            else {
                statePicked = State.HOLD
                binding.completeDate.visibility = View.GONE
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkFormFields() {
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

        val state = statePicked //?.ordinal

        var publicationDate: LocalDate? = null
        if (binding.publicationDateValue.text.toString() != "") {
            publicationDate =
                    LocalDate.parse(
                            binding.publicationDateValue.text.toString(), formatter)

            binding.publicationDate.error = null
        }
        else {
            canAdd = false
            binding.publicationDate.error = getString(R.string.date_null_error)
        }

        var completeDate: LocalDate? = null
        if (state == State.COMPLETED) {
            if (binding.publicationDateValue.text.toString() != "") {
                completeDate =
                        LocalDate.parse(
                                binding.completeDateValue.text.toString(), formatter)

                binding.completeDate.error = null

                if (publicationDate != null && completeDate.isBefore(publicationDate)) {
                    canAdd = false
                    binding.completeDate.error = getString(R.string.date_consistence_error)
                }
            } else {
                canAdd = false
                binding.completeDate.error = getString(R.string.date_null_error)
            }
        }
        val annotation = binding.entryAnnotationValue.text.toString()

        if (canAdd) {
            Snackbar.make(requireView(), R.string.form_completed, Snackbar.LENGTH_LONG).show()
        }
    }
}