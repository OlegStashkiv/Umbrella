package com.olegstashkiv.umbrella.domain.model

import android.os.Parcel
import android.os.Parcelable

data class WeatherHour(
    val datetime: String,
    val temp: Double,
    val icon: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(datetime)
        parcel.writeDouble(temp)
        parcel.writeString(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherHour> {
        override fun createFromParcel(parcel: Parcel): WeatherHour {
            return WeatherHour(parcel)
        }

        override fun newArray(size: Int): Array<WeatherHour?> {
            return arrayOfNulls(size)
        }
    }
}

