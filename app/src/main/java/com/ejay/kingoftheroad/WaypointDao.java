package com.ejay.kingoftheroad;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class WaypointDao {

    public WaypointDao(){}

    public interface FetchedCallback {
        void onFetchSuccess(ArrayList<Waypoint> arrList);
        void onFetchFail();
    }

    public void fetch(String id, final FetchedCallback callback){

        String url = Constants.API_URL + "/api/routes/" + id;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray list = response.getJSONArray("data");
                            ArrayList<Waypoint> arrList = new ArrayList<Waypoint>();
                            for(int i=0;i<list.length();i++ ){
                                Waypoint waypoint = new Waypoint();
                                waypoint.setId(list.getJSONObject(i).get("id").toString());
                                waypoint.setLatitude(list.getJSONObject(i).getLong("latitude"));
                                waypoint.setLongitude(list.getJSONObject(i).getLong("longitude"));
                                arrList.add(waypoint);
                            }

                            callback.onFetchSuccess(arrList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFetchFail();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        callback.onFetchFail();
                    }
                });
    }

    public void save(ArrayList<Waypoint> wp, final FetchedCallback callback){

        String url = Constants.API_URL + "/api/routes/";
        JSONArray list = new JSONArray();
        JSONObject data = new JSONObject();
        for(int i=0; i <wp.size(); i++ ) {
            JSONObject jsObj = new JSONObject();
            try {
                jsObj.put("id", wp.get(i).getId());
                jsObj.put("latitude", wp.get(i).getLatitude());
                jsObj.put("longitude", wp.get(i).getLongitude());
                list.put(jsObj);
            } catch (JSONException e) {
                callback.onFetchFail();
                e.printStackTrace();
            }

        }
        try {
            data.put("data", list);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFetchFail();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        callback.onFetchSuccess(null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFetchFail();
            }
        });

    }
    public ArrayList<Waypoint> fetchAll(){
        return null;
    }



}
