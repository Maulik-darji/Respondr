package com.app.Respondr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FloatingActionButton fabMyLocation, fabEmergencyLocation;
    private Location lastKnownLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        mapView = view.findViewById(R.id.mapView);
        fabMyLocation = view.findViewById(R.id.fabMyLocation);
        fabEmergencyLocation = view.findViewById(R.id.fabEmergencyLocation);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize map
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Set click listeners
        fabMyLocation.setOnClickListener(v -> moveToMyLocation());
        fabEmergencyLocation.setOnClickListener(v -> markEmergencyLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        
        // Check location permission and request if needed
        checkAndRequestLocationPermission();
        
        // Set default location (San Francisco)
        LatLng defaultLocation = new LatLng(37.7749, -122.4194);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, enable location features
            enableLocationFeatures();
        } else {
            // Permission not granted, show explanation dialog first
            showLocationPermissionDialog();
        }
    }

    private void showLocationPermissionDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Location Access Required")
                .setMessage("Respondr needs access to your location to:\n\n" +
                        "• Show your current location on the map\n" +
                        "• Enable emergency location features\n" +
                        "• Provide accurate location-based services\n\n" +
                        "Your location data is only used for these features and is not shared with third parties.")
                .setPositiveButton("Allow Location", (dialog, which) -> {
                    requestLocationPermission();
                })
                .setNegativeButton("Not Now", (dialog, which) -> {
                    Toast.makeText(getContext(), "Location features will be limited. You can enable location access later in settings.", 
                        Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }

    private void enableLocationFeatures() {
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false); // We'll use our custom button
            getCurrentLocation();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location features
                enableLocationFeatures();
                Toast.makeText(getContext(), "Location access granted! Map features are now enabled.", 
                    Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Location access denied. Map features will be limited. You can enable location access in app settings.", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        lastKnownLocation = location;
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                });
        }
    }

    private void moveToMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            if (lastKnownLocation != null) {
                LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                getCurrentLocation();
            }
        } else {
            Toast.makeText(getContext(), "Location permission required. Please enable location access.", 
                Toast.LENGTH_SHORT).show();
            showLocationPermissionDialog();
        }
    }

    private void markEmergencyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            if (lastKnownLocation != null) {
                LatLng emergencyLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                
                // Add emergency marker
                googleMap.addMarker(new MarkerOptions()
                    .position(emergencyLocation)
                    .title("Emergency Location")
                    .snippet("Emergency reported at this location"));
                
                // Move camera to emergency location
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(emergencyLocation, 16));
                
                Toast.makeText(getContext(), "Emergency location marked!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Unable to get current location for emergency marking.", 
                    Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Location permission required for emergency features.", 
                Toast.LENGTH_SHORT).show();
            showLocationPermissionDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
