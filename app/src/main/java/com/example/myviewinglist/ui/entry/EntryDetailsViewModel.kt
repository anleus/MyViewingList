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