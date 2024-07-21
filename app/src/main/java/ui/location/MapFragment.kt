package ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.couponchest.R
import com.example.couponchest.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(android.content.Context.MODE_PRIVATE))

        locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                setupMap()
                startLocationUpdates()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.location_permission_is_required_to_show_your_position_on_the_map), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            setupMap()
            startLocationUpdates()
        }

        return view
    }

    private fun setupMap() {
        myLocationOverlay = MyLocationNewOverlay(binding.mapView)
        myLocationOverlay.enableMyLocation()
        binding.mapView.overlays.add(myLocationOverlay)
    }

    private fun startLocationUpdates() {
        viewModel.address.observe(viewLifecycleOwner) { address ->
            binding.locationStatusTextView.text = address
            updateMapLocation(address)
        }
    }

    private fun updateMapLocation(address: String) {
        val geoPoint = addressToGeoPoint(address)
        if (geoPoint != null) {
            val currentLocationMarker = Marker(binding.mapView)
            currentLocationMarker.position = geoPoint
            currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            currentLocationMarker.title = getString(R.string.you_are_here)
            currentLocationMarker.snippet = address

            binding.mapView.overlays.clear()
            binding.mapView.overlays.add(myLocationOverlay)
            binding.mapView.overlays.add(currentLocationMarker)
            if(binding.mapView.zoomLevelDouble == 0.0){
                binding.mapView.controller.setZoom(18.0)
                binding.mapView.controller.setCenter(geoPoint)
            }
        } else {
            Log.e("MapFragment", "Failed to convert address to GeoPoint")
        }
    }

    private fun addressToGeoPoint(address: String): GeoPoint? {
        return try {
            val geocoder = Geocoder(requireContext())
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses?.isNotEmpty() == true) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                GeoPoint(latitude, longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MapFragment", "Error converting address to GeoPoint", e)
            null
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
