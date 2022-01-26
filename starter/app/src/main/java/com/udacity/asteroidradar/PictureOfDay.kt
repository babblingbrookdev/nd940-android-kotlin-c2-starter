package com.udacity.asteroidradar

import com.squareup.moshi.Json

// add thumbnail url to model to handle when videos are posted as "image" of the day
data class PictureOfDay(
    @Json(name = "media_type") val mediaType: String, val title: String,
    val url: String, @Json(name = "thumbnail_url") val thumbnailUrl: String?
)