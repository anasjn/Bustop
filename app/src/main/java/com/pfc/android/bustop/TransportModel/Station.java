package com.pfc.android.bustop.TransportModel;


import com.google.android.gms.maps.model.Circle;

import java.util.jar.Attributes;

/**
 * Created by dr3amsit on 01/07/17.
 */

public class Station {

    private String _name;
    private String _id;
    private String _type;
    private String _xmlns;


    public Station() {
        super();
    }

    public String getXmlns() {
        return _xmlns;
    }

    public void setXmlns(String xmlns) {
        this._xmlns = xmlns;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        this._type = type;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }
}
