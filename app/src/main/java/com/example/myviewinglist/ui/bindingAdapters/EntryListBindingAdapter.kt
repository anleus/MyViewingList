package com.example.myviewinglist.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.firebase.storage.FirebaseStorage

val storageRef = FirebaseStorage.getInstance().reference

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imgUrl: String?) {
    /*
    if (imgUrl != null) {
        //Log.d("Dbug", "cargando la imagen")
        imageView.load(storageRef.child(imgUrl))
        //add lo de error
        //va mal
    }
    */
}

/*@BindingAdapter("android:text")
fun bindName(textView: TextView, name: String?) {
    name.let {
        textView.text = it
    }
}

@BindingAdapter("data")
fun bindRecyclerView(recyclerView: RecyclerView?, data: MutableList<Entry>?) {
    val adapter = recyclerView?.adapter

    if (adapter is EntryListAdapter && data != null) {
        adapter.setListData(data)
    }
}


@BindingAdapter("apiStatus")
fun bindStatus(statusText: TextView, status: EntriesServiceStatus?) {
    when (status) {
        EntriesServiceStatus.LOADING -> {
            statusText.visibility = View.VISIBLE
            statusText.text = "Se esta cargando"
        }
        EntriesServiceStatus.ERROR -> {
            statusText.visibility = View.VISIBLE
            statusText.text = "Esto no va"
        }
        EntriesServiceStatus.DONE -> {
            statusText.visibility = View.GONE
        }
    }
}*/