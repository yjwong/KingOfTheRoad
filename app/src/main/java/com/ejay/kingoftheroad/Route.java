package com.ejay.kingoftheroad;

import java.util.ArrayList;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class Route {

    private String id;
    private String name;
    private double distance;
    private String kingId;
    private String kingName;
    private long bestTiming;
    private String creatorID;
    private ArrayList<Waypoint> waypoints;

    public Route(){}

    public static Route fetch(String id){
        return null;
    }

    public static void save(Route route){

    }

    public static ArrayList<Route> fetchAll(){
        return null;
    }

    public static ArrayList<Route> fetchAllByCreator(String creatorID){
        return null;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setKingId(String kingId) {
        this.kingId = kingId;
    }

    public void setKingsName(String kingName) {
        this.kingName = kingName;
    }

    public void setBestTiming(long bestTiming) {
        this.bestTiming = bestTiming;
    }

    public void setArrayWaypoints(ArrayList<Waypoint> waypoints){
        this.waypoints = waypoints;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getKingId() {
        return kingId;
    }

    public String getKingName() {
        return kingName;
    }

    public long getBestTiming() {
        return bestTiming;
    }
    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }
}
