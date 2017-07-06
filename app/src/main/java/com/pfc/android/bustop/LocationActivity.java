package com.pfc.android.bustop;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


/**
 * Created by dr3amsit on 18/06/17.
 */

public class LocationActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = LocationActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    /**
     * Request code for location permission request.
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // A default location (London, UK) and default zoom to use when location permission is
    // not granted.
    // not granted
    private final LatLng mDefaultLocation = new LatLng(51.509865, -0.118092);
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates;
    private boolean mLocationPermissionGranted;
    private boolean mPermissionDenied = false;
    private GoogleMap mGoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate1");
        setContentView(R.layout.location);
        Log.v(TAG, "onCreate2");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setOnMyLocationButtonClickListener(this);
        Log.v(TAG, "onMapReady");
        enableMyLocation();

    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
//        Log.v(TAG,"enableMyLocation manifest " + Manifest.permission.ACCESS_FINE_LOCATION);
//        Log.v(TAG,"enableMyLocation manifest2 " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
//        Log.v(TAG,"enableMyLocation packager " + PackageManager.PERMISSION_GRANTED);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.v(TAG, "enableMyLocation if 2");
                Utils.RationaleDialog.newInstance(LOCATION_PERMISSION_REQUEST_CODE, true)
                        .show(this.getSupportFragmentManager(), "dialog");
            } else {
                Log.v(TAG, "enableMyLocation else");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else if (mGoogleMap != null) {
            Log.v(TAG, "enableMyLocation else 2");
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult requestCode " + requestCode);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    enableMyLocation();
                } else {

                    // permission denied,
                    mPermissionDenied = true;
                }
            }
            return;
        }

    }


//    /**
//     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
//     * permission from a runtime permissions request.
//     *
//     * @see android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
//     */
//    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
//                                              String permission) {
//        for (int i = 0; i < grantPermissions.length; i++) {
//            if (permission.equals(grantPermissions[i])) {
//                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
//            }
//        }
//        return false;
//    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {

        Utils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
