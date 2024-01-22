package com.olegstashkiv.umbrella.presentation.ui.today

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.olegstashkiv.umbrella.MainActivity
import com.olegstashkiv.umbrella.R
import com.olegstashkiv.umbrella.UmbrellaApplication
import com.olegstashkiv.umbrella.data.db.DataStoreManager
import com.olegstashkiv.umbrella.databinding.FragmentTodayBinding
import com.olegstashkiv.umbrella.domain.usecases.WeatherIcon
import com.olegstashkiv.umbrella.presentation.adapter.HourlyAdapter
import com.olegstashkiv.umbrella.presentation.ui.WeatherApiStatus
import com.olegstashkiv.umbrella.presentation.ui.WeatherViewModel
import com.olegstashkiv.umbrella.presentation.ui.WeatherViewModelFactory
import com.olegstashkiv.umbrella.presentation.ui.location.LocationsViewModel
import com.olegstashkiv.umbrella.presentation.ui.location.LocationsViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class TodayFragment : Fragment() {
    private val args: TodayFragmentArgs by navArgs()
    private val handler = Handler(Looper.getMainLooper())
    private val delay: Long = 1000

    private val weatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory(
            DataStoreManager(requireContext())
        )
    }

    private val locationsViewModel: LocationsViewModel by activityViewModels {
        LocationsViewModelFactory(
            (activity?.application as UmbrellaApplication).database.locationDao()
        )
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getUserLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.toast_location_permission_required,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private lateinit var binding: FragmentTodayBinding
    private lateinit var adapterHourly: HourlyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var updateClock: Runnable
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        weatherViewModel.updateStatus(WeatherApiStatus.LOADING)
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        val selectedLocation = args.selectedLocation

        initializeViews()

        if (selectedLocation.isNotBlank()) {
            weatherViewModel.updateLocation(selectedLocation)
        } else {
            requestLocationPermission()
        }

        setupObservers()

        return binding.root
    }

    private fun initializeViews() {
        binding.lifecycleOwner = this
        binding.viewModel = weatherViewModel

        dataStoreManager = DataStoreManager(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        recyclerView = binding.recyclerWeatherEveryHour
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapterHourly = HourlyAdapter(emptyList())
        recyclerView.adapter = adapterHourly

/*        binding.changeLocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.changeLocation.windowToken, 0)

                weatherViewModel.updateLocation(binding.changeLocation.text.toString())

                binding.changeLocation.text = null
                binding.changeLocation.isEnabled = false
                binding.changeLocation.clearFocus()
                binding.changeLocation.isEnabled = true

                true
            } else {
                false
            }
        }*/

        updateClock = object : Runnable {
            override fun run() {
                val currentDateTime = LocalDateTime.now()
                val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("EEE MMM d"))
                val formattedTime = currentDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
                binding.date.text = getString(R.string.label_datetime, formattedDate, formattedTime)
                handler.postDelayed(this, delay)
            }
        }

        handler.postDelayed(updateClock, delay)

/*        binding.nextDayTitle.setOnClickListener {
            findNavController().navigate(R.id.action_dailyWeatherFragment_to_weeklyWeatherFragment)
        }

        binding.savedLocationImage.setOnClickListener {
            findNavController().navigate(R.id.action_dailyWeatherFragment_to_locationsFragment)
        }

        binding.backButtonToSavedLocation.setOnClickListener {
            findNavController().navigate(R.id.action_dailyWeatherFragment_to_locationsFragment)
        }*/
    }

    private fun setupObservers() {
        weatherViewModel.location.observe(viewLifecycleOwner, Observer {
            weatherViewModel.getDailyWeather()
        })

        weatherViewModel.dailyWeatherData.observe(viewLifecycleOwner, Observer { weatherResponse ->
            weatherResponse?.let {
                val refactoredTimezone = it.timezone.replace("/", " | ").replace("_", " ")
                binding.location.text = refactoredTimezone
                val weatherIcon = WeatherIcon.getIconByCode(it.days[0].icon)

                Glide.with(requireContext())
                    .load(weatherIcon.iconResourceId)
                    .into(binding.currentWeatherImage)
                binding.temperature.text =
                    getString(R.string.label_temperature, it.days[0].temp.toString())
                binding.currentMaxAndMinTemperature.text = getString(
                    R.string.label_temp_max_and_min,
                    Math.round(it.days[0].tempmax).toString(),
                    Math.round(it.days[0].tempmin).toString()
                )
                binding.humidityText.text =
                    getString(R.string.label_humidity, it.days[0].humidity.toString())
                binding.rainText.text =
                    getString(R.string.label_rain, it.days[0].precipprob.toString())
                binding.windSpeedText.text =
                    getString(R.string.label_wind_speed, it.days[0].windspeed.toString())

                recyclerView = binding.recyclerWeatherEveryHour
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                val hoursOfCurrentDay =
                    it.days[0].hours.subList(LocalDateTime.now().hour, it.days[0].hours.size)
                val hoursOfNextDay = it.days[1].hours
                adapterHourly = HourlyAdapter((hoursOfCurrentDay + hoursOfNextDay).subList(0, 24))
                recyclerView.adapter = adapterHourly
            }
        })
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        val storedLocation = runBlocking {
            dataStoreManager.getLocation()
        }

        if (storedLocation.isNotBlank()) {
            weatherViewModel.updateLocation(storedLocation)
            weatherViewModel.getDailyWeather()
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val userLocation = "${it.latitude},${it.longitude}"
                        weatherViewModel.updateLocation(userLocation)

                        viewLifecycleOwner.lifecycleScope.launch {
                            dataStoreManager.saveLocation(userLocation)
                        }

                        locationsViewModel.insertLocation(
                            com.olegstashkiv.umbrella.data.entity.Location(
                                1,
                                locationName = userLocation
                            )
                        )
                        weatherViewModel.getDailyWeather()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        R.string.toast_location_permission_required,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateClock)
    }
}
