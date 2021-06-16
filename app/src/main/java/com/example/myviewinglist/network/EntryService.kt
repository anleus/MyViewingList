package com.example.myviewinglist.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myviewinglist.model.Entry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred

class EntryService {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
            .addOnFailureListener{ exception ->
                Log.d("Service", "Exception reading: $exception")
            }
        return mutableData
    }
/*
    fun getEntriesByName(name: String?) : LiveData<MutableList<Entry>> {
        val mutableData = MutableLiveData<MutableList<Entry>>()
        val entriesList = mutableListOf<Entry>()

        FirebaseFirestore.getInstance().collection("entries").where(name >= )
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val name = document.getString("name")
                        val type = document.getString("type")
                        val publication = document.getTimestamp("publication")

                        val entry = Entry(document.id, name!!, type!!, "", publication!!)
                        entriesList.add(entry)
                    }
                    mutableData.value = entriesList
                }
            }
            .addOnFailureListener{ exception ->
                Log.d("Service", "Exception reading: $exception")
            }
        return mutableData
    }
*/

    fun getEntryById(entryId: String) : Entry? {
        return null
    }

    fun getUserEntries(userId: String) : List<Entry> {
        return listOf()
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
                Log.d("Dbug", "Checkeo con nombre: $name y tipo $type")
                if (!documents.isEmpty) {
                    Log.d("Dbug", "Checking if documents are the same")
                    for (doc in documents) {
                        Log.d("Dbug", "name: ${doc.getString("lc_name")} and type: ${doc.getString("type")}")
                    }

                    entryExist.complete(true)

                } else {
                    entryExist.complete(false)
                }
            }
            .addOnFailureListener{ exception ->
                Log.d("Dbug", "Exception comparing: $exception")
                entryExist.complete(true)
            }

        return entryExist.await()
    }
}