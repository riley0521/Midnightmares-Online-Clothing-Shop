package com.teampym.onlineclothingshopapplication.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class LogEntity(
    val id: String,
    val userId: String,
    val position: String,
    val type: String,
    val description: String,
    val dateLog: Date
): Parcelable