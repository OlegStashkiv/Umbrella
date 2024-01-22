package com.olegstashkiv.umbrella.presentation.ui.location

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.olegstashkiv.umbrella.MainActivity
import com.olegstashkiv.umbrella.UmbrellaApplication
import com.olegstashkiv.umbrella.data.entity.Location
import com.olegstashkiv.umbrella.databinding.FragmentLocationsBinding
import com.olegstashkiv.umbrella.presentation.adapter.LocationsAdapter

class LocationFragment : Fragment() {

    private val locationsViewModel: LocationsViewModel by activityViewModels {
        LocationsViewModelFactory(
            (activity?.application as UmbrellaApplication).database.locationDao()
        )
    }

    private lateinit var adapterLocations: RecyclerView.Adapter<LocationsAdapter.ViewHolder>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLocationsBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = locationsViewModel

        binding.searchCities.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                locationsViewModel.insertLocation(Location(locationName = binding.searchCities.text.toString()))

                binding.searchCities.text = null
                binding.searchCities.isEnabled = false
                binding.searchCities.clearFocus()

                binding.searchCities.isEnabled = true

                true
            } else {
                false
            }
        }

        locationsViewModel.locations.observe(viewLifecycleOwner, Observer { locations ->
            locations?.let {
                recyclerView = binding.recyclerLocations
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                locationsViewModel.getWeatherForLocations()

                locationsViewModel.weatherForLocation.observe(viewLifecycleOwner, Observer { weather ->
                    weather?.let {
                        adapterLocations = LocationsAdapter(
                            locationsViewModel.weatherForLocation.value!!, locationsViewModel)
                        recyclerView.adapter = adapterLocations
                    }
                })

                val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        locationsViewModel.deleteLocationAt(position)
                    }
                }

                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
        })
        return binding.root
    }
}
