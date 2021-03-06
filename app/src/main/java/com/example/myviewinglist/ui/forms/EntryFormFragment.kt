package com.example.myviewinglist.ui.forms

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.myviewinglist.R
import com.example.myviewinglist.databinding.FragmentEntryFormBinding
import com.example.myviewinglist.model.EntryType
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EntryFormFragment : Fragment() {

    private lateinit var binding: FragmentEntryFormBinding
    private val viewModel: EntryFormViewModel by viewModels()

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

        viewModel.operationState.observe(viewLifecycleOwner, Observer { value ->
            when (value) {
                FormOpState.ADD_COMPLETED -> openEntrySnackBar(getString(R.string.entry_created), true)
                FormOpState.ADD_FAIL -> openEntrySnackBar(getString(R.string.entry_create_fail), false)
                //2 -> openEntrySnackBar(getString(R.string.entry_deleted), false)
                //-2 -> openEntrySnackBar(getString(R.string.entry_delete_fail), false)
            }
        })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Dropdown tipo de Entry
        val types = resources.getStringArray(R.array.entry_type)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.basic_list_item, types)
        binding.entryTypeValue.setAdapter(arrayAdapter)

        /*val adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.entry_type, R.layout.basic_list_item)
        (binding.entryType.editText as? AutoCompleteTextView)?.setAdapter(adapter)*/

        //Date picker
        binding.publicationDateValue.setOnClickListener {
            openDatePicker()
        }

        datePicker.addOnPositiveButtonClickListener {
            val myInstant = Instant.ofEpochMilli(it)
            val myDate = LocalDateTime.ofInstant(myInstant, ZoneId.systemDefault()).toLocalDate()

            localDateToString(myDate)
        }

        //Complete button
        binding.addButton.setOnClickListener { checkFormFields() }
    }

    private fun openDatePicker() {
        datePicker.show(childFragmentManager, "datePicker")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun localDateToString(date: LocalDate) {
        val parsedDate = date.format(formatter)

        binding.publicationDateValue.setText(parsedDate)
    }

    private fun openEntrySnackBar(message: String, added: Boolean) {
        val objLayoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)

        if (added) {
            snackbar.setAction(R.string.entry_look) {
                openEntry()
            }
            clearFormFields()
        }
        snackbar.show()
        viewModel.resetOperationState()
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
            viewModel.createNewEntry(name, convertStringToEnum(type), publicationDate)
        }
        else {
            Log.d("Dbug", "No se pueden enviar los datos")
        }
    }

    private fun convertStringToEnum(type: String) : EntryType {
        var newType: EntryType = EntryType.VIDEOGAME

        when (type) {
            getString(R.string.videogame_name) -> newType = EntryType.VIDEOGAME
            getString(R.string.anime_name) -> newType = EntryType.ANIME
            getString(R.string.manga_name) -> newType = EntryType.MANGA
            getString(R.string.film_name) -> newType = EntryType.FILM
            getString(R.string.serie_name) -> newType = EntryType.SERIE
            getString(R.string.book_name) -> newType = EntryType.BOOK
        }

        return newType
    }

    private fun clearFormFields() {
        binding.entryNameValue.setText("")
        binding.entryTypeValue.setText("")
        binding.publicationDateValue.setText("")
    }

    private fun openEntry() {
        val action = EntryFormFragmentDirections.
            actionEntryFormFragmentToEntryFragment(entryToList())

        view?.findNavController()?.navigate(action)
    }

    private fun entryToList(): Array<String?> {
        val entry = viewModel.lastEntry.value!!

        return arrayOf(entry.id, entry.name, entry.type?.ordinal.toString(), entry.publication)
    }
}