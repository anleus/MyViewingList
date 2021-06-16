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
                 service.getAllEntries().observeForever {
                     _entries.value = it
                 }
             } catch (e: Exception) {
                 _entries.value = mutableListOf<Entry>()
             }
         }
        /*
        viewModelScope.launch {
            _status.value = EntriesServiceStatus.LOADING
            try {
                _entries.value = service.getAllEntries()
                //Log.d("Service", "entry n1 is ${entries.value?.get(0)?.name}")
                _status.value = EntriesServiceStatus.DONE
            } catch (e: Exception) {
                Log.d("Service", "exception in viewModel, exception: $e")
                _entries.value = listOf()
                _status.value = EntriesServiceStatus.ERROR
            }
        }
        */
    }
}