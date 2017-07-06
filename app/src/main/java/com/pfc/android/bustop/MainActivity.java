package com.pfc.android.bustop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pfc.android.bustop.TransportModel.Stop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.pfc.android.bustop.R.id.txtResponse;
import static com.pfc.android.bustop.R.layout.location;

/**
 * Created by dr3amsit on 02/07/17.
 */

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // A default location (London, UK) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(51.509865, -0.118092);
    //only for testing purposes
    private final LatLng myLocation = new LatLng(51.509865, -0.118092);
    ArrayList<Marker> mStops = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private Marker mStop;
    private Marker mLocation;

    // Progress dialog
    private ProgressDialog pDialog;
    private TextView txtResponse;

    // temporary string to show the parsed response
    //private String jsonResponse;
    private ArrayList<Stop> stopPosition = new ArrayList<>();
    private String stopLon;
    private String stopLa;

    private LatLngBounds.Builder build = null;

    private Circle mAdelaideCircle;
    private Marker mHobartMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtResponse = (TextView) findViewById(R.id.firsttime);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationIntent = new Intent(MainActivity.this, MarkerActivity.class);
                startActivity(locationIntent);
                Snackbar.make(view, "Favourite added to your list", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_location:
                Intent locationIntent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(locationIntent);
                return true;
            case R.id.action_map:
                Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mMap = googleMap;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

//                          makeJsonObjectRequest(location.getLatitude(),location.getLongitude(),250);
//                          only for testing: adding london center as a position
                            makeJsonObjectRequest(myLocation.latitude, myLocation.longitude, 250);
//                           mLocation=mMap.addMarker(new MarkerOptions()
//                                  .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                                 .title("My position"));
//                            mLocation.setTag(0);
                            //only for testing: adding london center as a position
                            // Add some markers to the map, and add a data object to each marker.

                            // Create bounds that include all locations of the map.

                            mLocation = mMap.addMarker(new MarkerOptions()
                                    .position(myLocation)
                                    .title("My position"));
                            mLocation.setTag(0);
                            mStops.add(mLocation);
                            Log.v(TAG, "stopPosition.size()" + stopPosition.size());

                            for (int i = 0; i < stopPosition.size(); i++) {
                                mStop = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(stopPosition.get(i).get_lat(), stopPosition.get(i).get_long()))
                                        .title("Bus Stop"));
                                mStop.setTag(i);
                                mStops.add(mStop);
                            }
                        } else {
                            // Add a marker in my default place,
                            makeJsonObjectRequest(mDefaultLocation.latitude, mDefaultLocation.longitude, 250);
                            mLocation = mMap.addMarker(new MarkerOptions()
                                    .position(mDefaultLocation)
                                    .title("My Default Location"));
                            mLocation.setTag(0);
                            mStops.add(mLocation);
                            Log.v(TAG, "stopPosition.size()" + stopPosition.size());
                            for (int i = 0; i < stopPosition.size(); i++) {
                                mStop = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(stopPosition.get(i).get_lat(), stopPosition.get(i).get_long()))
                                        .title("Bus Stop"));
                                mStop.setTag(i);
                                mStops.add(mStop);
                            }
                        }
//                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                        Log.v(TAG,"mStops" + mStops.size());
//                        for (Marker marker : mStops) {
//                            Log.v(TAG,"markers:" + marker.getPosition());
//                            builder.include(marker.getPosition());
//                        }
//                        LatLngBounds bounds = builder.build();
//                        int padding = 10; // offset from edges of the map in pixels
//                        // move the map's camera
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
//                        // Add a circle, a ground overlay, a marker, a polygon and a polyline to the map.
//                        if(myLocation!=null) {
//                            // move the map's camera to the same location.
//                            addObjectsToMap(myLocation);
//                        }else{
//                            addObjectsToMap(mDefaultLocation);
//                        }

                    }
                });
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    private void mapLocate() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mStops.size() != 0) {
            Log.v(TAG, "mStops" + mStops.size());
            for (Marker marker : mStops) {
                Log.v(TAG, "markers:" + marker.getPosition());
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 10; // offset from edges of the map in pixels
            // move the map's camera
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            // Add a circle, a ground overlay, a marker, a polygon and a polyline to the map.
            if (myLocation != null) {
                // move the map's camera to the same location.
                addObjectsToMap(myLocation);
            } else {
                addObjectsToMap(mDefaultLocation);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        mapLocate();
    }

    private void addObjectsToMap(LatLng location) {
        // A circle centered on Adelaide.
        mAdelaideCircle = mMap.addCircle(new CircleOptions()
                .center(location)
                .radius(250)
                .fillColor(Color.argb(150, 66, 173, 244))
                .strokeColor(Color.rgb(66, 173, 244))
                .clickable(true));
        mAdelaideCircle.setTag("Near me");


        // A marker at Hobart.
        mHobartMarker = mMap.addMarker(new MarkerOptions().position(location));
        mHobartMarker.setTag("My Location");

    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    /**
     * Method to get information about the stops availables near to one location.
     * The response filter the stops within a given radius (in meters) from a specified latitude and longitude. (WGS84 coordinate system)
     * Circle=Latitude, Longitude,Radius (in m),e.g. Circle=12.3121412,14.1231241,100
     *
     * @param dlatitude
     * @param dlongitude
     * @param dradius
     */
    private void makeJsonObjectRequest(double dlatitude, double dlongitude, int dradius) {
        showpDialog();
        //Error 405 trying with POST method
        // Post params to be sent to the server
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("token","AbCdEfGh123456");
//        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            VolleyLog.v("Response:%n %s", response.toString(4));
//                            Log.v(TAG, "req JSON response: " + response.toString(4));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error: ", error.getMessage());
//            }
//        });

        //I try with GET
        //urlJsonObj = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?Circle=51.49598,-0.14191,250&StopPointState=0&ReturnList=StopCode1,StopPointName,Bearing,StopPointIndicator,StopPointType,Latitude,Longitude";
        //String urlJsonObj = "https://api.tfl.gov.uk/Stoppoint?lat=51.513395&lon=-0.089095&radius=100&stoptypes=NaptanBusCoachStation,NaptanPublicBusCoachTram";
        String urlJsonObj = "https://api.tfl.gov.uk/Stoppoint?lat=" + dlatitude + "&lon=" + dlongitude + "&radius=" + dradius + "&stoptypes=NaptanBusCoachStation,NaptanPublicBusCoachTram";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray stopPoints = response.getJSONArray("stopPoints");

                    Log.v(TAG, "length: " + stopPoints.length());

                    JSONObject instant;
                    String naptanId;
                    String commonName;
                    JSONArray additionalProperties;
                    JSONObject address;
                    String addressValue = "";
                    double distance;
                    String jsonResponse = "";
                    Stop stopBus = null;
                    for (int i = 0; i < stopPoints.length(); i++) {
                        // Parsing json object response
                        // response will be a json object
                        instant = (JSONObject) stopPoints.get(i);
                        naptanId = instant.getString("naptanId");
                        commonName = instant.getString("commonName");
                        //distance = instant.getDouble("distance");
                        additionalProperties = instant.getJSONArray("additionalProperties");
                        for (int j = 0; j < additionalProperties.length(); j++) {
                            address = (JSONObject) additionalProperties.getJSONObject(j);
                            if (address.get("key").equals("Address")) {
                                addressValue = address.getString("value");
                            }
                            if (address.get("key").equals("Towards")) {
                                addressValue += "Towards " + address.getString("value");
                            }
                        }

                        stopBus = new Stop(instant.getDouble("lat"), instant.getDouble("lon"));
                        stopPosition.add(i, stopBus);
                        jsonResponse += "naptanId: " + naptanId + "\n\n";
                        jsonResponse += "commonName: " + commonName + "\n\n";
                        //jsonResponse += "distance: " + distance + "\n\n";
                        jsonResponse += "addressValue: " + addressValue + "\n\n";


                    }
                    txtResponse.setText(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        TransportController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

