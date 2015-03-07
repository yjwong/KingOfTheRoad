package com.ejay.kingoftheroad;

import java.util.ArrayList;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class Route {

    private String id;
    private String name;
    private Integer distance;
    private String creatorID;

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

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getCreatorID() {
        return creatorID;
    }
}
