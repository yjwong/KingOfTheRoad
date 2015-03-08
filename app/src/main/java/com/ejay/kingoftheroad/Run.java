package com.ejay.kingoftheroad;

import java.util.ArrayList;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class Run {

    private String id;
    private String runnerId;
    private String routeID;
    private long timing;

    public Run(){}

    public static Run fetch(String id){
        return null;
    }

    public static void save(Run run){

    }

    public static ArrayList<Run> fetchAll(){
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(String runnerId) {
        this.runnerId = runnerId;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public long getTiming() {
        return timing;
    }

    public void setTiming(long timing) {
        this.timing = timing;
    }


}
