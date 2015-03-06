package com.ejay.kingoftheroad;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by JohnKuan on 5/3/2015.
 */
public class MapRecord {
    private String id ;
    private String kingName;
    private String bestTiming;
    private String distance;
    private ArrayList<MarkerOptions> listOfMarkers;



    public MapRecord(){
        id = "123";
        kingName = "Blah Blah";
        bestTiming = "10:30min";
        distance = "2.4Km";
        listOfMarkers = null;
    }

    //Constructor to store a record of the activity of user after running
    public MapRecord(String ID, String KING_NAME, String BEST_TIMING, String DISTANCE, ArrayList<MarkerOptions> list){
        id = ID;
        kingName = KING_NAME;
        bestTiming = BEST_TIMING;
        distance = DISTANCE;
        listOfMarkers = list;

    }


    public String getId() {
        return id;
    }

    public String getKingName() {
        return kingName;
    }

    public String getBestTiming() {
        return bestTiming;
    }

    public String getDistance() {
        return distance;
    }

    public ArrayList<MarkerOptions> getListOfMarkers() {
        return listOfMarkers;
    }
}
