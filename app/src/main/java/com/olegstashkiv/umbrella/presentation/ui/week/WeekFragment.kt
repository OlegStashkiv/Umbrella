package com.olegstashkiv.umbrella.presentation.ui.week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.olegstashkiv.umbrella.R
import com.olegstashkiv.umbrella.data.db.DataStoreManager
import com.olegstashkiv.umbrella.databinding.FragmentWeekBinding
import com.olegstashkiv.umbrella.domain.usecases.WeatherIcon
import com.olegstashkiv.umbrella.presentation.adapter.FutureAdapter
import com.olegstashkiv.umbrella.presentation.ui.WeatherViewModel
import com.olegstashkiv.umbrella.presentation.ui.WeatherViewModelFactory

class WeekFragment : Fragment() {
    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory(
            DataStoreManager(requireContext())
        )
    }

    private lateinit var adapterFuture: RecyclerView.Adapter<FutureAdapter.ViewHolder>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWeekBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getWeeklyWeatherData()
        viewModel.weeklyWeatherData.observe(viewLifecycleOwner, Observer { weatherResponse ->
            weatherResponse?.let {
                binding.tomorrowCondition.text = it.days[1].conditions
                val weatherIcon = WeatherIcon.getIconByCode(it.days[1].icon)

                Glide.with(requireContext())
                    .load(weatherIcon.iconResourceId)
                    .into(binding.tomorrowImage)
                binding.tomorrowTemp.text = getString(R.string.label_temperature, it.days[1].temp.toString())
                binding.rainText.text = getString(R.string.label_rain, it.days[1].precipprob.toString())
                binding.humidityText.text = getString(R.string.label_humidity, it.days[1].humidity.toString())
                binding.windSpeedText.text = getString(R.string.label_wind_speed, it.days[1].windspeed.toString())

/*                binding.backButton.setOnClickListener {
                    findNavController().navigate(R.id.action_weeklyWeatherFragment_to_dailyWeatherFragment)
                }*/

                recyclerView = binding.recyclerWeatherEveryDay
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                val hourlyDataFromCurrentHour = it.days.subList(2, it.days.size)
                adapterFuture = FutureAdapter(hourlyDataFromCurrentHour)
                recyclerView.adapter = adapterFuture
            }
        })

        return binding.root
    }
}