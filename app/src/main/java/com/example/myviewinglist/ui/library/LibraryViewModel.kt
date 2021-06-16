package com.example.myviewinglist.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myviewinglist.model.AddedEntry

class LibraryViewModel: ViewModel() {

    private val _addedEntries = MutableLiveData<List<AddedEntry>>()
    val addedEntries: LiveData<List<AddedEntry>> = _addedEntries

    /*private fun getAddedEntries() {
        viewModelScope.launch {
            try {
                _addedEntries.value = EntriesApi.retrofitService.getUserEntries()
            } catch (e: Exception) {
                _addedEntries.value = listOf()
            }
        }
    }*/
}