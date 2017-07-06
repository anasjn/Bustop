package com.pfc.android.bustop;

import android.location.Location;

/**
 * Created by dr3amsit on 18/06/17.
 */

public class FabLocation {

    private String fablocation;


    /**
     * Create a new FabLocation Object
     *
     * @param location Miwok translation of the word
     */
    public FabLocation(Location location) {
        location = location;
    }

    public String getLocation() {
        return fablocation;
    }

    public void setLocation(String location) {
        this.fablocation = location;
    }
}
