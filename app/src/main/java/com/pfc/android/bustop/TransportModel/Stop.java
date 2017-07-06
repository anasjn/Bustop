package com.pfc.android.bustop.TransportModel;

/**
 * Created by dr3amsit on 02/07/17.
 */

public class Stop {

    double _lat;
    double _long;

    public Stop(double _long, double _lat) {
        this._long = _long;
        this._lat = _lat;
    }

    public double get_lat() {
        return _lat;
    }

    public void set_lat(double _lat) {
        this._lat = _lat;
    }

    public double get_long() {
        return _long;
    }

    public void set_long(double _long) {
        this._long = _long;
    }


}
