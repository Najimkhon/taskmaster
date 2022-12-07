package com.hfad.taskmaster2.models

import android.os.Parcel
import android.os.Parcelable

data class Task (
    var title: String = "",
    val createdBy: String = "",
    val cardList: ArrayList<Card> = ArrayList()
        ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    ) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int)= with(dest) {
        writeString(title)
        writeString(createdBy)
        writeTypedList(cardList)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}