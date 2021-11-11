package com.example.myviewinglist.model

import com.example.myviewinglist.R

enum class EntryType(val stringId: Int) {
    VIDEOGAME(R.string.videogame_name),
    ANIME(R.string.anime_name),
    MANGA(R.string.manga_name),
    FILM(R.string.film_name),
    SERIE(R.string.serie_name),
    BOOK(R.string.book_name),
}

data class Entry(val id: String? = null,
                 val name: String? = null,
                 val type: EntryType? = null,
                 val cover: String? = null,
                 val publication: String? = null)
