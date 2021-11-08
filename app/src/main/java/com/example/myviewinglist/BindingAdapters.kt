package com.example.myviewinglist

/*
@BindingAdapter("imageSrc")
fun bindImage(imgView: ImageView, imageSrc: String?) {
    imageSrc?.let {
        val imgUri = imageSrc.toUri().buildUpon().scheme("https").build()
        imgView.load(imageSrc) --> esto es una StorageReference
    }
}*/

/*
@BindingAdapter("serviceStatus")
fun bindStatus(statusImageView: ImageView, status: ServiceStatus?) {
    when (status) {
        ServiceStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        ServiceStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        ServiceStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}*/
