package com.pfc.android.bustop;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;


/**
 * Created by dr3amsit on 27/06/17.
 */
public class TransportController extends Application {
    //Log or request TAG
    public static final String TAG = TransportController.class.getSimpleName();
    // A singleton instance of the application class for easy access in other places
    private static TransportController mInstance;
    //Global request queue for Volley
    private RequestQueue mRequestQueue;

    //return transport singleton instance
    public static synchronized TransportController getInstance() {
        Log.v(TAG, "getInstance ");
        if (mInstance == null) {
            mInstance = new TransportController();
            Log.v(TAG, "ourInstance if " + mInstance);
        }
        return mInstance;
    }


//    private TransportController() {
//        mRequestQueue = getRequestQueue();
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        // getApplicationContext() is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        if (mRequestQueue == null) {
            Log.v(TAG, "mRequestQueue " + mRequestQueue);
//            Cache cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 20 * 1024 * 1024);
//            Network network = new BasicNetwork(new HurlStack());
//            mRequestQueue = new RequestQueue(cache, network);
//            mRequestQueue.start();
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        Log.v(TAG, "addTo");
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        Log.v(TAG, "ourInstance if2 " + req.getUrl());
        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

//    public ImageLoader getImageLoader() {
//        return mImageLoader;
//    }


//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//       // setContentView(R.layout.transport);
//
//        ourInstance = this;

    // mRequestQueue = Volley.newRequestQueue(this);
//        String url = "http://www.google.com";
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        mTextView.setText("Response is: "+ response.substring(0,500));
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mTextView.setText("That didn't work!");
//            }
//        });
//// Add the request to the TransportController.
//        queue.add(stringRequest);
}
