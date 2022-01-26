package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidAdapter
import timber.log.Timber

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.contentDescription =
            context.getString(R.string.potentially_hazardous_content_description)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription =
            context.getString(R.string.not_hazardous_asteroid_content_description)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription =
            context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("pictureOfDay")
fun bindPictureOfDayImage(imageView: ImageView, pictureOfDay: PictureOfDay?) {
    val context = imageView.context
    pictureOfDay?.let { picture ->
        var url = picture.url
        if (picture.thumbnailUrl != null) {
            url = picture.thumbnailUrl
        }
        val picasso = Picasso.Builder(context).listener { _, _, exception ->
            Timber.e(
                exception
            )
        }.build()
        picasso.load(url)
            .placeholder(R.drawable.placeholder_picture_of_day).fit().centerCrop().into(imageView)
    }
}

@BindingAdapter("pictureOfDayTitle")
fun bindPictureOfDayImage(textView: TextView, title: String?) {
    title?.let {
        textView.text = title
    }
}

@BindingAdapter("pictureContentDesc")
fun bindPictureOfDayContentDesc(imageView: ImageView, contentDesc: String?) {
    val context = imageView.context
    contentDesc?.let {
        imageView.contentDescription = String.format(
            context.getString(R.string.nasa_picture_of_day_content_description_format),
            contentDesc
        )
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("list_data")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidAdapter
    adapter.submitList(data)
}

@BindingAdapter("updateStatus")
fun bindSaveButton(button: FloatingActionButton, asteroid: Asteroid?) {
    val context = button.context
    asteroid?.let {
        if (it.isSaved) {
            button.contentDescription = context.getString(R.string.unsave_asteroid)
            button.setImageResource(R.drawable.ic_baseline_delete_24)
        } else {
            button.contentDescription = context.getString(R.string.save_asteroid)
            button.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }
}
