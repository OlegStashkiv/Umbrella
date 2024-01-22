package com.olegstashkiv.umbrella.domain.model

import android.os.Parcel
import android.os.Parcelable

data class WeatherDay(
    val datetime: String,
    val tempmax: Double,
    val tempmin: Double,
    val temp: Double,
    val humidity: Double,
    val windspeed: Double,
    val conditions: String,
    val icon: String,
    val hours: List<WeatherHour>,
    val precipprob: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        mutableListOf<WeatherHour>().apply {
            parcel.readTypedList(this, WeatherHour)
        },
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(datetime)
        parcel.writeDouble(tempmax)
        parcel.writeDouble(tempmin)
        parcel.writeDouble(temp)
        parcel.writeDouble(humidity)
        parcel.writeDouble(windspeed)
        parcel.writeString(conditions)
        parcel.writeString(icon)
        parcel.writeTypedList(hours)
        parcel.writeDouble(precipprob)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherDay> {
        override fun createFromParcel(parcel: Parcel): WeatherDay {
            return WeatherDay(parcel)
        }

        override fun newArray(size: Int): Array<WeatherDay?> {
            return arrayOfNulls(size)
        }
    }
}
