package com.olegstashkiv.umbrella.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.olegstashkiv.umbrella.R
import com.olegstashkiv.umbrella.domain.model.WeatherDay
import com.olegstashkiv.umbrella.domain.usecases.WeatherIcon
import java.text.SimpleDateFormat
import java.util.Locale

const val DEGREES = "°"

class FutureAdapter(private val items: List<WeatherDay>) : RecyclerView.Adapter<FutureAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.day_text)
        val imageView: ImageView = itemView.findViewById(R.id.image_day_weather)
        val statusText: TextView = itemView.findViewById(R.id.status_text)
        val lowText: TextView = itemView.findViewById(R.id.low_text)
        val highText: TextView = itemView.findViewById(R.id.high_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_future, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(item.datetime)

        val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayOfWeek = outputFormat.format(date)

        holder.dayText.text = dayOfWeek
        holder.statusText.text = item.conditions
        holder.lowText.text = item.tempmin.toString() + DEGREES
        holder.highText.text = item.tempmax.toString() + DEGREES

        val context = holder.itemView.context
        val weatherIcon = WeatherIcon.getIconByCode(item.icon)

        Glide.with(context)
            .load(weatherIcon.iconResourceId)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}