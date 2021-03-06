package com.vp6.anish.madguysmarketers;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by anish on 22-11-2016.
 */
public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    String marker_title;
    String marker_snippet;


    public MyItem(double lat, double lng,String marker_snippet, String title) {

        mPosition = new LatLng(lat, lng);
        this.marker_title = title;
        this.marker_snippet = marker_snippet;

    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle(){
        return marker_title;
    }

    public String getMarker_snippet(){
        return  marker_snippet;
    }
}
