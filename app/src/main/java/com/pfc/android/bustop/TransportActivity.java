package com.pfc.android.bustop;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pfc.android.bustop.TransportModel.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.category;
import static android.R.attr.key;
import static android.R.attr.name;
import static android.R.attr.type;
import static android.R.attr.x;
import static android.media.CamcorderProfile.get;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

//import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class TransportActivity extends AppCompatActivity {

    public static final String TAG = TransportActivity.class.getSimpleName();

    Resources res = getResources();
    String app_id = res.getString(R.string.api_transport_id);
    String app_key = res.getString(R.string.api_transport_key);

    // json object response url
    private String urlJsonObj = "https://api.tfl.gov.uk/Stoppoint?lat=51.513395&lon=-0.089095&stoptypes=NaptanMetroStation,NaptanRailStation,NaptanBusCoachStation,NaptanFerryPort,NaptanPublicBusCoachTram";

    //final String URL = "https://api.tfl.gov.uk/Stoppoint?lat=51.513395&lon=-0.089095&stoptypes=NaptanMetroStation,NaptanRailStation,NaptanBusCoachStation,NaptanFerryPort,NaptanPublicBusCoachTram";

    // json array Gets the list of available StopPoint additional information categories
    private String urlListAvailableStopPoints = "https://api.tfl.gov.uk/StopPoint/Meta/Categories?app_id=" + app_id + "&app_key=" + app_key;

    //Live bus and river bus arrivals API (instant)
    private String urlLiveBus = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?app_id=" + app_id + "&app_key=" + app_key;


    //Live get station by zone
    //private String urlStationByZone = "https://data.tfl.gov.uk/tfl/syndication/feeds/stations-facilities.xml?app_id={0}&app_key={1}";

    //Live get station withing a ratio:51.49598,-0.14191,250
    //private String urlStationInARatio = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?Circle=51.49598,-0.14191,250&StopPointState=0&ReturnList=StopCode1,StopPointName,Bearing,StopPointIndicator,StopPointType,Latitude,Longitude";

    //private Button btnMakeObjectRequest, btnMakeArrayRequest;
    private Button btnMakeArrayRequest, btnMakeArrayRequest2;

    // Progress dialog
    private ProgressDialog pDialog;

    private TextView txtResponse;

    // temporary string to show the parsed response
    private String jsonResponse;


    //final JSONObject jsonBody= new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "URL JSON");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        //btnMakeObjectRequest = (Button) findViewById(R.id.btnObjRequest);
        btnMakeArrayRequest = (Button) findViewById(R.id.btnArrayRequest);
        btnMakeArrayRequest2 = (Button) findViewById(R.id.btnObjRequest);
        txtResponse = (TextView) findViewById(R.id.txtResponse);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

//        btnMakeObjectRequest.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // making json object request
//                makeJsonObjectRequest();
//            }
//        });

        btnMakeArrayRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // making json array request
                makeJsonArrayRequest();
            }
        });

        btnMakeArrayRequest2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // making json request to TfL
                //GetStopsInARadius(51.49598, -0.14191,250);
                makeJsonObjectRequest();
            }
        });

        Log.v(TAG, "URL JSON2");
    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeJsonObjectRequest() {
        showpDialog();
        //Error 405 trying with POST method
        // Post params to be sent to the server
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("token","");
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
        urlJsonObj = "https://api.tfl.gov.uk/Stoppoint?lat=51.513395&lon=-0.089095&radius=100&stoptypes=NaptanMetroStation,NaptanRailStation,NaptanBusCoachStation,NaptanFerryPort,NaptanPublicBusCoachTram";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

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
                    for (int i = 0; i < stopPoints.length(); i++) {

                        // Parsing json object response
                        // response will be a json object
                        instant = (JSONObject) stopPoints.get(i);
                        naptanId = instant.getString("naptanId");
                        commonName = instant.getString("commonName");
                        distance = instant.getDouble("distance");
                        additionalProperties = instant.getJSONArray("additionalProperties");
                        for (int j = 0; j < additionalProperties.length(); j++) {
                            address = (JSONObject) additionalProperties.getJSONObject(j);
                            if (address.get("key").equals("Address")) {
                                addressValue = address.getString("value");
                            }
                        }

                        Log.v(TAG, "addressValue: " + addressValue);

                        jsonResponse += "naptanId: " + naptanId + "\n\n";
                        jsonResponse += "commonName: " + commonName + "\n\n";
                        jsonResponse += "distance: " + distance + "\n\n";
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

    /**
     * Method to make json array request where response starts with [
     */
    private void makeJsonArrayRequest() {
        showpDialog();

        JsonArrayRequest req = new JsonArrayRequest(urlListAvailableStopPoints,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject stop = (JSONObject) response
                                        .get(i);

                                String type = stop.getString("$type");
                                String category = stop.getString("category");
                                JSONArray keys = stop
                                        .getJSONArray("availableKeys");


                                jsonResponse += "type: " + type + "\n\n";
                                jsonResponse += "category: " + category + "\n\n";
                                jsonResponse += "keys: " + keys + "\n\n";

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
                hidepDialog();
            }
        });

        // Adding request to request queue
        TransportController.getInstance().addToRequestQueue(req);
    }

    private void GetStationsByZone(String zoneid) {

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
    private void GetStopsInARadius(double dlatitude, double dlongitude, int dradius) {
        showpDialog();

        //Live get station withing a ratio:lat:51.49598,long:-0.14191,radius in meters:250
        //String urlStationInARatio = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?Circle=" + dlatitude + "," + dlongitude + "," + dradius + "&StopPointState=0&ReturnList=StopCode1,StopPointName,Bearing,StopPointIndicator,StopPointType,Latitude,Longitude";
        // String urlStationInARatio = "https://api.tfl.gov.uk/StopPoint/490006170N1/Arrivals";
        //String urlStationInARatio2="https://api.tfl.gov.uk/Vehicle/LTZ1573/Arrivals";
        String urlStationInARatio = "https://api.tfl.gov.uk/Stoppoint?lat=" + dlatitude + "&lon=" + dlongitude + "&radius=" + dradius + "&stoptypes=NaptanMetroStation,NaptanRailStation,NaptanBusCoachStation,NaptanFerryPort,NaptanPublicBusCoachTram";
        Log.d(TAG, urlStationInARatio);

        JsonArrayRequest req = new JsonArrayRequest(urlStationInARatio,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            // Parsing json array response
                            // loop through each json object
                            jsonResponse = "";

                            //first entry is the URA version, so we are going to start with the second entry
                            //format of the first entry [4,"1.0",1498938823111]
                            for (int i = 0; i < response.length(); i++) {

                                //JSONObject instant = (JSONObject) response.get(i);

                                JSONArray instant = (JSONArray) response.get(i);
                                Log.v(TAG, response.get(i).toString());
//                                String stopCode1 = instant.get(0).toString();
//                                String stopPointName =   instant.get(1).toString();
//                                String bearing = instant.get(2).toString();
//                                String stopPointIndicator = instant.get(3).toString();
//                                String stopPointType = instant.get(4).toString();
//                                String latitude = instant.get(5).toString();
//                                String longitude = instant.get(6).toString();
//
//
//                                jsonResponse += "stopcode1: " + stopCode1 + "\n\n";
//                                jsonResponse += "stopPointName: " + stopPointName + "\n\n";
//                                jsonResponse += "bearing: " + bearing + "\n\n";
//                                jsonResponse += "stopPointIndicator: " + stopPointIndicator + "\n\n";
//                                jsonResponse += "stopPointType: " + stopPointType + "\n\n";
//                                jsonResponse += "latitude: " + latitude + "\n\n";
//                                jsonResponse += "longitude: " + longitude + "\n\n";

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
                hidepDialog();
            }
        });
        // Adding request to request queue
        TransportController.getInstance().addToRequestQueue(req);

    }


    private void GetStationById(String stationid) {

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
