package com.pfc.android.bustop;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * Created by dr3amsit on 18/06/17.
 */

public class LocationAdapter extends ArrayAdapter<FabLocation> {


    /**
     * Contructor
     *
     * @param context
     * @param fabLocations
     */
    public LocationAdapter(Activity context, ArrayList<FabLocation> fabLocations) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, fabLocations);
    }


    /**
     * Provides a view for an AdapterView(LIstView,GridView...)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled viwe that is going to be populated
     * @param parent      The parent View that is used for inlfation
     * @return The View for the position in the AdapterView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position,convertView,parent);
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.single_item, parent, false);
        }

        // Get the {@link Location} object located at this position in the list
        final FabLocation currentLocation = getItem(position);
        // Find the TextView in the single_item.xml layout with the ID miwokword
        TextView locationTextView = (TextView) listItemView.findViewById(R.id.fablocation);
        // Get the version name from the current Word object and
        // set this text on the name TextView
        locationTextView.setText(currentLocation.getLocation());

        Log.v("WordAdapter", "create button");
        //  }
        return listItemView;
    }


}


