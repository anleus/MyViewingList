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
    val testUserId: String = "PyhdAWstL5Ck8BVaCKNm"

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

    fun addNewEntry(entryData: HashMap<String, String>) : String {
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

     suspend fun checkEntryExist(name: String?, type: String?) : Boolean? {
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