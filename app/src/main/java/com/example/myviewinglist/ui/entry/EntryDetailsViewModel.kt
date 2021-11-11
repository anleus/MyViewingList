package com.example.myviewinglist.ui.entry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviewinglist.model.AddedEntry
import com.example.myviewinglist.model.AddedEntryState
import com.example.myviewinglist.model.Entry
import com.example.myviewinglist.network.EntryService
import com.example.myviewinglist.network.ServiceStatus
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

enum class DetailsOpState {
    NONE,
    SAVE_COMPLETED,
    SAVE_ERROR,
    UPDATE_COMPLETED,
    UPDATE_ERROR,
    IMPOSSIBLE_OPERATION
}

class EntryDetailsViewModel : ViewModel() {

    private val service = EntryService()

    private val _entry = MutableLiveData<Entry>()
    val entry: LiveData<Entry> = _entry

    private val _addedEntry = MutableLiveData<AddedEntry>()
    val addedEntry: LiveData<AddedEntry> = _addedEntry

    private val _operationStatus = MutableLiveData<DetailsOpState>()
    val operationStatus = _operationStatus
    
    private val _requestStatus = MutableLiveData<ServiceStatus>()
    val requestStatus = _requestStatus

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
            Log.d("debug", "added entry esite")

            val reqAddedEntry = CompletableDeferred<AddedEntry?>()
            reqAddedEntry.complete(service.getUserAddedEntry(entryId))

            _addedEntry.value = reqAddedEntry.await()

            if (addedEntry.value != null) {
                success = true
            }
        } catch (e: Exception) {
            return false
        }
        return success
    }

    fun updateAddedEntry(state: AddedEntryState? = null,  completeDate: String? = null, annotation: String? = null) {
        val data = hashMapOf<String, String>()
        var isNewAddedEntry = false

        if (_addedEntry.value?.state == null) {
            isNewAddedEntry = true
            _addedEntry.value = AddedEntry(entry.value?.id)
        }

        if (state != null) {
            _addedEntry.value?.state = state
            data["state"] = state.ordinal.toString()
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
                if (success!!) _operationStatus.value = DetailsOpState.SAVE_COMPLETED
                else _operationStatus.value = DetailsOpState.SAVE_ERROR
            }
            1 -> {
                if (success!!) _operationStatus.value = DetailsOpState.UPDATE_COMPLETED
                else _operationStatus.value = DetailsOpState.UPDATE_ERROR
            }
            else -> {
                _operationStatus.value = DetailsOpState.IMPOSSIBLE_OPERATION
            }
        }
    }

    fun resetOperationState() {
        _operationStatus.value = DetailsOpState.NONE
    }

     fun initializeDetailsPage(item: Entry) {
         _entry.value = item

        viewModelScope.launch {
            _requestStatus.value = ServiceStatus.LOADING
            try {
                if (service.checkAddedEntryExists(item.id!!)!!) {
                    val addedEntryLoaded = CompletableDeferred<Boolean?>()

                     addedEntryLoaded.complete(getAddedEntry(item.id))

                    if (addedEntryLoaded.await()!!) {
                        _requestStatus.value = ServiceStatus.DONE
                    }
                } else {
                    _requestStatus.value = ServiceStatus.DONE
                }

            } catch (e: Exception) {
                _requestStatus.value = ServiceStatus.ERROR
                //hacer que refresque o decir que no hay conexion a internet
            }
        }
    }

}