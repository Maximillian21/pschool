package com.example.photosearch.views

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.photosearch.R
import com.example.photosearch.databinding.FragmentMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment() {
    private val REQUEST_LOCATION_PERMISSION = 1
    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding
        get() = _binding!!

    private var marker: Marker? = null

    private val args: MapsFragmentArgs by navArgs()

    lateinit var map: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        googleMap.setOnMapLongClickListener {
            marker?.remove()
            createMarker(it)
            marker?.tag = "Chosen point"
            binding.btnMapConfirm.isEnabled = true
        }

        getLocation()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
        } else {
            val client = LocationServices.getFusedLocationProviderClient(requireActivity())
            client.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    createMarker(LatLng(location.latitude, location.longitude))
                    marker?.tag = "Location point"
                    binding.btnMapConfirm.isEnabled = true
                    Log.d("MapsFragmentLat", location.latitude.toString())
                    Log.d("MapsFragmentLon", location.longitude.toString())
                }
            }
            Log.d("MapsFragment", "getLocation: permissions granted")
        }
    }

    private fun createMarker(latLng: LatLng) {
        marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .draggable(true)
        )

        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(7f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
            else {
                Log.d("MapsFragment", "not granted")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(marker != null) binding.btnMapConfirm.isEnabled = true

        binding.btnMapConfirm.setOnClickListener {
            findNavController().navigate(MapsResultsFragmentDirections.showGeoResults(
                args.account,
                marker?.position!!)
            )
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}