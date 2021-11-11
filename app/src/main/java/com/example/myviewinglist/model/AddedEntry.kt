package com.example.myviewinglist.model

import com.example.myviewinglist.R

enum class AddedEntryState(val stringId: Int) {
    COMPLETED(R.string.completed_name),
    VIEWING(R.string.viewing_name),
    WAITING(R.string.waiting_name),
    DROPPED(R.string.dropped_name),
    NONE(R.string.save_name)
}

data class AddedEntry(val entryId: String? = null,
                      var state: AddedEntryState? = null,
                      var completeDate: String? = null,
                      var annotation: String? = null)
