package com.example.cinemaproject.data

data class FilmInfo(
    val titleRu: String,
    val imageUrl: String,
)

object FilmCatalog {
    // Map backend filmId -> Russian title and image
    private val map: Map<String, FilmInfo> = mapOf(
        // Custom provided posters
        "f1" to FilmInfo(
            "Дюна: Часть вторая",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/5013_mid_3650.jpg"
        ),
        "f2" to FilmInfo(
            "Оппенгеймер",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/5014_mid_8482.jpg"
        ),
        "f3" to FilmInfo(
            "Джокер: Безумие на двоих",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/5002_mid_4304.jpg"
        ),
        "f4" to FilmInfo(
            "Человек-паук: Через вселенные",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/4996_mid_7884.jpg"
        ),
        "f5" to FilmInfo(
            "Холодное сердце 2",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/5031_mid_9039.jpg"
        ),
        // Use provided domain for the rest too (reusing some posters for demo)
        "f6" to FilmInfo(
            "Гран Туризмо",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/5013_mid_3650.jpg"
        ),
        "f7" to FilmInfo(
            "Гарри Поттер и философский камень",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/5014_mid_8482.jpg"
        ),
        "f8" to FilmInfo(
            "Интерстеллар",
            "https://kinomax.tomsk.ru/upload/filmbase/posters/5002_mid_4304.jpg"
        ),
    )

    fun getInfo(filmId: String): FilmInfo? = map[filmId]
}

