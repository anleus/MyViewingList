package com.example.myviewinglist.ui.forms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviewinglist.model.Entry
import com.example.myviewinglist.model.EntryType
import com.example.myviewinglist.network.EntryService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.util.*

enum class FormOpState {
    NONE,
    ADD_COMPLETED,
    ADD_FAIL,
}

class EntryFormViewModel: ViewModel() {

    private val service = EntryService()

    private val _lastEntry = MutableLiveData<Entry>()
    val lastEntry: LiveData<Entry> = _lastEntry

    private val lastEntryId = MutableLiveData<String>("")


    private val _operationState = MutableLiveData<FormOpState>()
    val operationState = _operationState

     fun createNewEntry(name: String, type: EntryType, publication: String) {
        val data = hashMapOf(
            "lc_name" to name.toLowerCase(Locale.getDefault()),
            "name" to name,
            "type" to  type.ordinal.toString(),
            "publication" to publication)

        viewModelScope.launch {
            try {
                if (!service.checkEntryExists(data["lc_name"], data["type"])!!) {

                    val addedId = CompletableDeferred<String?>()
                    addedId.complete(service.addEntry(data))

                    addedId.await()
                    lastEntryId.value = addedId.toString()

                    if (lastEntryId.value != "") {
                        _operationState.value = FormOpState.ADD_COMPLETED
                        _lastEntry.value = Entry(lastEntryId.value, name, type, null, publication)
                    }
                    else {
                        _operationState.value = FormOpState.ADD_FAIL
                    }
                } else {
                    _operationState.value = FormOpState.ADD_FAIL
                }
            } catch (e: Exception) {
                _operationState.value = FormOpState.ADD_FAIL
            }
        }
    }

    /*fun undoNewEntry() {
        viewModelScope.launch {
            try {
                val success = CompletableDeferred<Boolean?>()
                success.complete(service.deleteEntry(_lastEntryId.value!!))

                if (success.await() == true) {
                    _operationState.value = 2
                } else {
                    _operationState.value = -2
                }
            } catch (e: Exception) {
                _operationState.value = -2
            }
        }
    }*/

    fun resetOperationState() {
        _operationState.value = FormOpState.NONE
    }
}