package com.vp6.anish.madguysmarketers;

import java.util.Comparator;
import java.util.List;

/**
 * Created by anish on 16-01-2017.
 */

public class ListItem{
    String name;
    String type;
    String id;
    String image_url;
    String lat;
    String lng;
    String address;
    String phone;
    String status;

    public void setData(String name, String type, String id,String image_url,String lat,String lng,String address,String phone,String status) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.image_url = image_url;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.phone = phone;
        this.status = status;

    }



    Comparator<ListItem> comparatorName = new Comparator<ListItem>() {
        @Override
        public int compare(ListItem o1, ListItem o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    };

    Comparator<ListItem> comparatorType = new Comparator<ListItem>() {
        @Override
        public int compare(ListItem o1, ListItem o2) {
            return o1.status.compareTo(o2.status);
        }
    };
}
