package com.olegstashkiv.umbrella.domain.model

import android.os.Parcel
import android.os.Parcelable

data class WeatherResponse(
    val timezone: String,
    val days: List<WeatherDay>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createTypedArrayList(WeatherDay)!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(timezone)
        parcel.writeTypedList(days)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherResponse> {
        override fun createFromParcel(parcel: Parcel): WeatherResponse {
            return WeatherResponse(parcel)
        }

        override fun newArray(size: Int): Array<WeatherResponse?> {
            return arrayOfNulls(size)
        }
    }
}
