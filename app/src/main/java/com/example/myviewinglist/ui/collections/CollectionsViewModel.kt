package com.example.myviewinglist.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myviewinglist.model.Entry
import com.example.myviewinglist.network.EntryService
import kotlinx.coroutines.launch

enum class EntriesServiceStatus { LOADING, ERROR, DONE }

class CollectionsViewModel : ViewModel() {

    private val service = EntryService()

    private val _entries = MutableLiveData<MutableList<Entry>>()
    val entries: LiveData<MutableList<Entry>> = _entries

    private val _status = MutableLiveData<EntriesServiceStatus>()
    val status: LiveData<EntriesServiceStatus> = _status

    init {
        getAllEntries()
    }

    private fun getAllEntries() {
         viewModelScope.launch {
             try {
                 service.getEntries().observeForever {
                     _entries.value = it //-> en teoria concatena
                 }

             } catch (e: Exception) {
                 _entries.value = mutableListOf<Entry>()
             }
         }
    }
}