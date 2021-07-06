package com.example.myviewinglist.ui.entry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviewinglist.model.AddedEntry
import com.example.myviewinglist.model.Entry
import com.example.myviewinglist.network.EntryService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class EntryDetailsViewModel : ViewModel() {

    private val service = EntryService()

    private val _entry = MutableLiveData<Entry>()
    val entry: LiveData<Entry> = _entry

    private val _addedEntry = MutableLiveData<AddedEntry>()
    val addedEntry: LiveData<AddedEntry> = _addedEntry

    private val _operationState = MutableLiveData<Int>()
    val operationState = _operationState

    var showLoading: Boolean = true

    private suspend fun getEntry(entryId: String): Boolean {
        var success = false

        try {
            val reqEntry = CompletableDeferred<Entry?>()
            reqEntry.complete(service.getEntryById(entryId))

            _entry.value = reqEntry.await()

            if (entry.value != null) success = true

        } catch (e: Exception) {
            return false
        }
        return success
    }

    private suspend  fun getAddedEntry(entryId: String): Boolean{
        var success = false

        try {
            val reqAddedEntry = CompletableDeferred<AddedEntry?>()
            reqAddedEntry.complete(service.getUserAddedEntry(entryId))

            _addedEntry.value = reqAddedEntry.await()

            if (addedEntry.value != null) success = true
        } catch (e: Exception) {
            return false
        }
        return success
    }

    fun updateAddedEntry(state: String? = null,  completeDate: String? = null, annotation: String? = null) {
        val data = hashMapOf<String, String>()
        var isNewAddedEntry = false

        if (_addedEntry.value?.state == null) {
            isNewAddedEntry = true
            _addedEntry.value = AddedEntry(entry.value?.id)
        }

        if (state != null) {
            _addedEntry.value?.state = state
            data["state"] = state
        }
        if (completeDate != null) {
            _addedEntry.value?.completeDate = completeDate
            data["completeDate"] = completeDate
        }
        if (annotation != null) {
            _addedEntry.value?.annotation = annotation
            data["annotation"] = annotation
        }

        viewModelScope.launch {
            val successfulOperation = CompletableDeferred<Boolean?>()

            try {
                if (isNewAddedEntry) {
                    successfulOperation.complete(service.addAddedEntry(_addedEntry.value?.entryId!!, data))
                    operationState(0, successfulOperation.await())
                } else {
                    successfulOperation.complete(service.updateAddedEntry(_addedEntry.value?.entryId!!, data))
                    operationState(1, successfulOperation.await())
                }
            } catch (e: Exception) {
                successfulOperation.complete(false)
                operationState(-1, successfulOperation.await())
            }
        }
    }

    private fun operationState(isUpdate: Int, success: Boolean?) {
        when (isUpdate) {
            0 -> {
                if (success!!) _operationState.value = 1
                else _operationState.value = -1
            }
            1 -> {
                if (success!!) _operationState.value = 2
                else _operationState.value = -2
            }
            else -> {
                _operationState.value = -3
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = 0
    }


     fun initializeDetailsPage(id: String) {
        viewModelScope.launch {
            val entryLoaded = async { getEntry(id) }
            val addedEntryLoaded = async { getAddedEntry(id) }

            if (entryLoaded.await() && addedEntryLoaded.await()) {
                Log.d("Service", "Todos los datos de las entradas han sido cargados")
                showLoading = false
            }
        }
    }

}