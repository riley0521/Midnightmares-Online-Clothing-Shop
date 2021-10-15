package com.teampym.onlineclothingshopapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val id: String,
    val name: String,
    val imageUrl: String,
): Parcelable {
    constructor() : this("", "", "")
}