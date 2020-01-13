package com.coupne.bosniaguide;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class PlaceData implements Serializable {
    private String address;
    private double lat;
    private double lng;
    private String longDescription;
    private String name;
    private String img;
    private String key;
    private HashMap<String, Commit> commit;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public PlaceData() {

    }


    public PlaceData(String address, double lat, double lng,
                     String longDescription, String name, String img,
                     HashMap<String, Commit> commits) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.longDescription = longDescription;
        this.name = name;
        this.img = img;
        this.commit = commits;
    }

    public HashMap<String, Commit> getCommit() {
        return commit;
    }

    public void setCommit(HashMap<String, Commit> commit) {
        this.commit = commit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }




}
