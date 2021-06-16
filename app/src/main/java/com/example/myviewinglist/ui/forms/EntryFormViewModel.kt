package com.example.myviewinglist.ui.forms

import android.util.Log
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
            Log.d("Dbug", "Inicio de la corutina")
            try {
                if (!service.checkEntryExist(data["lc_name"], data["type"])!!) {

                    val addedId = CompletableDeferred<String?>()
                    addedId.complete(service.addNewEntry(data))

                    addedId.await()
                    lastEntryId = addedId.toString()

                    Log.d("Dbug", "Se ha comprobado y no hay ninguna entrada igual")

                    if (lastEntryId != "") {
                        _entryAdded.value = 1
                        Log.d("Dbug", "Se ha añadido con exito la entrada a la BD")
                    }
                    else {
                        _entryAdded.value = -1
                        Log.d("Dbug", "No se ha añadido con exito la entrada a la BD")
                    }
                } else {
                    Log.d("Dbug", "Se ha comprobado y si hay entrada igual")
                    _entryAdded.value = -1
                }
            } catch (e: Exception) {
                Log.d("Dbug", "Fallo en viewModel, exception $e")
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