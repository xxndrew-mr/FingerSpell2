package com.example.fingerspell.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Finger(
    val name : String,
    val photo : Int
):Parcelable

