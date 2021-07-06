package com.example.myviewinglist.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myviewinglist.model.AddedEntry
import com.example.myviewinglist.model.Entry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred

class EntryService {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val testUserId: String = "PyhdAWstL5Ck8BVaCKNm"

    //add paginacion
     fun getAllEntries() : LiveData<MutableList<Entry>> {
        val mutableData = MutableLiveData<MutableList<Entry>>()
        val entriesList = mutableListOf<Entry>()

        db.collection("entries")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name")
                    val type = document.getString("type")
                    val publication = document.getString("publication")
                    val cover = document.getString("cover")

                    val entry = Entry(document.id, name!!, type!!, cover, publication!!)
                    entriesList.add(entry)
                }
                entriesList.sortBy { it.name }
                mutableData.value = entriesList
            }
            .addOnFailureListener { exception ->
                Log.d("Service", "Exception reading: $exception")
            }
        return mutableData
    }

    suspend fun getEntryById(entryId: String) : Entry? {
        val reqEntry = CompletableDeferred<Entry?>()

        db.collection("entries").document(entryId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                val type = document.getString("type")
                val publication = document.getString("publication")
                val cover = document.getString("cover")

                reqEntry.complete(Entry(document.id, name!!, type!!, cover, publication!!))
            }
            .addOnFailureListener { exception ->
                Log.d("Service", "Exception reading: $exception")
                reqEntry.complete(null)
            }
        return reqEntry.await()
    }

    fun getUserEntries(userId: String) : List<Entry> {
        return listOf()
    }

    suspend fun getUserAddedEntry(entryId: String) : AddedEntry? {
        val reqAddedEntry = CompletableDeferred<AddedEntry?>()

        db.collection("users").document(testUserId)
            .collection("added_entries").document(entryId)
            .get()
            .addOnSuccessListener { document ->
                val state = document.getString("state")
                val completeDate = document.getString("completeDate")
                val annotation = document.getString("annotation")

                reqAddedEntry.complete(AddedEntry(entryId, state, completeDate, annotation))
            }
            .addOnFailureListener { exception ->
                Log.d("Service", "Exception reading: $exception")
                reqAddedEntry.complete(null)
            }

        return reqAddedEntry.await()
    }

    fun addEntry(entryData: HashMap<String, String>) : String {
        var entryId = ""

            db.collection("entries")
                .add(entryData)
                .addOnSuccessListener { docRef ->
                    entryId = docRef.id
                    Log.d("Escritura","Entrada creada con id $entryId")
                }
                .addOnFailureListener { e ->
                    Log.w("Escritura", "Error: $e")
                }
        return entryId
    }

    suspend fun addAddedEntry(entryId: String, addedEntryData: HashMap<String, String>): Boolean? {
        val success = CompletableDeferred<Boolean?>()

        db.collection("users").document(testUserId)
            .collection("added_entries")
            .document(entryId).set(addedEntryData)
            .addOnSuccessListener {
                success.complete(true)
            }
            .addOnFailureListener {
                success.complete(false)
            }

        return success.await()
    }

    suspend fun updateAddedEntry(entryId: String, addedEntryData: HashMap<String, String>) : Boolean? {
        val success = CompletableDeferred<Boolean?>()

        val addedEntryRef = db.collection("users").document(testUserId)
            .collection("added_entries").document(entryId)

        for ((field, value) in addedEntryData) {
            addedEntryRef.update(field, value)
                .addOnSuccessListener {
                    success.complete(true)
                }
                .addOnFailureListener {
                    success.complete(false)
                }
        }

        return success.await()
    }

    suspend fun checkAddedEntryExists(entryId: String) : Boolean? {
        val addedEntryExist = CompletableDeferred<Boolean?>()

        db.collection("users").document(testUserId)
            .collection("added_entries")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.id == entryId) {
                        addedEntryExist.complete(true)
                    } else {
                        addedEntryExist.complete(false)
                    }
                }
            }
        return addedEntryExist.await()
    }

     suspend fun checkEntryExists(name: String?, type: String?) : Boolean? {
        val entriesRef = db.collection("entries")
        val query = entriesRef.whereEqualTo("lc_name", name).whereEqualTo("type", type)

        val entryExist = CompletableDeferred<Boolean?>()

        query.get()
            .addOnSuccessListener { documents ->
                entryExist.complete(!documents.isEmpty)
            }
            .addOnFailureListener{
                entryExist.complete(true)
            }

        return entryExist.await()
    }
}