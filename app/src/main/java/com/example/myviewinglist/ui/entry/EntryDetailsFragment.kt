package com.example.myviewinglist.ui.entry

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myviewinglist.R
import com.example.myviewinglist.databinding.FragmentEntryBinding
import com.google.android.material.button.MaterialButton

class EntryDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEntryBinding
    private val viewModel by lazy { ViewModelProvider(this).get(EntryDetailsViewModel::class.java)}
    private lateinit var entryId: String

    lateinit var newContext : ContextThemeWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            entryId = it.getString("entryId").toString()
            viewModel.initializeDetailsPage(entryId)
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
        setAnnotationButtonText()
        selectSateButtonStyle()

        binding.annotationButton.setOnClickListener {
            when (binding.annotationButton.text.toString()) {
                getString(R.string.entry_annotation_add) -> {
                    binding.annotationText.visibility = View.GONE
                    binding.annotationEditLayout.visibility = View.VISIBLE
                    binding.annotationButton.text = getString(R.string.entry_annotation_update)
                }
                getString(R.string.entry_annotation_edit) -> {
                    binding.annotationText.visibility = View.GONE
                    binding.annotationEditLayout.visibility = View.VISIBLE
                    binding.annotationButton.text = getString(R.string.entry_annotation_update)
                }
                getString(R.string.entry_annotation_update) -> {
                    binding.annotationText.visibility = View.VISIBLE
                    binding.annotationEditLayout.visibility = View.GONE
                    binding.annotationButton.text = getString(R.string.entry_annotation_edit)
                    //ademas hay que guardarlo en la bd
                }
            }
        }
    }

    private fun setAnnotationButtonText() {
        viewModel.addedEntry.observe(viewLifecycleOwner, Observer {
            if (it.annotation == null) {
                binding.annotationButton.text = getString(R.string.entry_annotation_add)
            }
            else {
                binding.annotationButton.text = getString(R.string.entry_annotation_edit)
            }
        })
    }

    private fun selectSateButtonStyle() {
        val stateButton = binding.stateButton

        viewModel.addedEntry.observe(viewLifecycleOwner, Observer {
            Log.d("Dbug", "Observando addedEntry $it")
            when (it.state) {
                getString(R.string.completed_name) -> applyCompletedStyle(stateButton)
                getString(R.string.viewing_name) -> applyViewingStyle(stateButton)
                getString(R.string.waiting_name) -> applyWaitingStyle(stateButton)
                getString(R.string.dropped_name) -> applyDroppedStyle(stateButton)
                else -> { }
            }
        })
    }

    private fun applyCompletedStyle(buttonView: MaterialButton) {
        with(buttonView) {
            setText(R.string.completed_name)
            setTextColor(resources.getColor(R.color.secondaryTextColor))
            setBackgroundColor(resources.getColor(R.color.completedColor))
        }
    }

    private fun applyViewingStyle(buttonView: MaterialButton) {
        Log.d("Dbug", "Aplicando estilo completado")
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
}