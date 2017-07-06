package com.pfc.android.bustop;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by dr3amsit on 25/06/17.
 */

public class MarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    public static final String TAG = MarkerActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // A default location (London, UK) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(51.509865, -0.118092);
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates;
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        Log.v(TAG, "SupportMapFragment");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near London, UK
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.v(TAG, "onMapReady");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.v(TAG, "onMapReady -mFusedLocationClient");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.v(TAG, "onMapReady -if");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.v(TAG, "onMapReady -else");
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.v(TAG, "onMapReady -onSuccess");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.v(TAG, "onMapReady -onSuccess if");
                            //Toast.makeText(getApplicationContext(), "FabLocation: "+location.toString(),Toast.LENGTH_SHORT).show();
                            // Add a marker in location,
                            // and move the map's camera to the same location.
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .title("Favorite").zIndex(1.0f));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

                        } else {
                            Log.v(TAG, "onMapReady -onSuccess else");
                            Toast.makeText(getApplicationContext(), "FabLocation: " + mDefaultLocation.toString(), Toast.LENGTH_SHORT).show();
                            // Add a marker in my default place,
                            // and move the map's camera to the same location.
                            googleMap.addMarker(new MarkerOptions().position(mDefaultLocation)
                                    .title("Favorite"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15));

                        }
                    }
                });
    }
}
