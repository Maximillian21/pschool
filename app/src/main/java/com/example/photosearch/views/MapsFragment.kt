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
        googleMap.setOnMapClickListener {
            marker?.remove()
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .draggable(true)
            )
            marker!!.tag = null

            val cameraPosition = CameraPosition.Builder()
                .target(it)
                .zoom(7f)
                .build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

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
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
        } else {
            val client = LocationServices.getFusedLocationProviderClient(requireActivity())
            client.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    moveMap(location.latitude, location.longitude)
                    Log.d("MapsFragment", location.latitude.toString())
                    Log.d("MapsFragmentLon", location.longitude.toString())
                }
            }
            Log.d("MapsFragment", "getLocation: permissions granted")
        }
    }

    fun moveMap(latitude: Double, longitude: Double) {
        Log.v("MapsFragment", "location marker")
        val latlng = LatLng(latitude, longitude)
        marker = map.addMarker(
            MarkerOptions()
                .position(latlng)
                .draggable(true)
        )
        marker!!.tag = null

        val cameraPosition = CameraPosition.Builder()
            .target(latlng)
            .zoom(7f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        binding.btnMapConfirm.isEnabled = true
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