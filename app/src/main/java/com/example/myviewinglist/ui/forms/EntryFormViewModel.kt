package com.example.myviewinglist.ui.forms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviewinglist.model.Entry
import com.example.myviewinglist.network.EntryService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.util.*

class EntryFormViewModel: ViewModel() {

    private val service = EntryService()

    private val _entry = MutableLiveData<Entry>()
    val entry: LiveData<Entry> = _entry

    private var lastEntryId: String = ""

    private val _entryAdded = MutableLiveData<Int>()
    val entryAdded = _entryAdded

     fun createNewEntry(name: String, type: String, publication: String) {
        val data = hashMapOf(
            "lc_name" to name.toLowerCase(Locale.getDefault()),
            "name" to name,
            "type" to  type,
            "publication" to publication)

        viewModelScope.launch {
            try {
                if (!service.checkEntryExists(data["lc_name"], data["type"])!!) {

                    val addedId = CompletableDeferred<String?>()
                    addedId.complete(service.addEntry(data))

                    addedId.await()
                    lastEntryId = addedId.toString()

                    if (lastEntryId != "") {
                        _entryAdded.value = 1
                    }
                    else {
                        _entryAdded.value = -1
                    }
                } else {
                    _entryAdded.value = -1
                }
            } catch (e: Exception) {
                _entryAdded.value = -1
            }
        }
    }

    fun undoNewEntry(): Boolean {
        return false
    }

    fun resetEntryAdded() {
        _entryAdded.value = 0
    }
}