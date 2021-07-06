package com.example.myviewinglist.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.myviewinglist.R

class StatePickerDialogFragment : DialogFragment() {

    lateinit var listener: StatePickerListener

    var statePickedPos: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.states_title)
            .setSingleChoiceItems(resources.getStringArray(R.array.entry_state), 1)
            { _, which ->
                statePickedPos = which
            }
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            }

        return builder.create()
    }


    interface StatePickerListener {
        fun selectState(state: String)
    }
}