package com.ejay.kingoftheroad;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class Waypoint {

    private String id;
    private float latitude;
    private float longitude;

    public Waypoint(){}

    public static Waypoint fetch(String id){
        return null;
    }

    public static void save(Waypoint wp){

    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }
    public void setLatitude(float lat){
        latitude = lat;
    }

    public void setLongitude(float lon){
        longitude = lon;
    }

    public void setId(String iD) {
        id = iD;
    }
}
