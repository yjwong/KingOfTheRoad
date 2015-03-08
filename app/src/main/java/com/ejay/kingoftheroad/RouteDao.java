package com.ejay.kingoftheroad;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class RouteDao {

    private RequestQueue mRequestQueue;

    public RouteDao(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public interface FetchedCallback {
        void onFetchSuccess(Route route);
        void onFetchFail();
    }

    public interface FetchedArrayCallback{
        void onFetchArraySuccess(ArrayList<Route> list);
        void onFetchArrayFail();
    }

    public void fetch(String id, final FetchedCallback callback){
        final Route route = new Route();
        String url = Constants.API_URL + "/api/routes" + id + "?include=waypoints";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject jsObj = response.getJSONObject("data");
                            route.setId(jsObj.get("id").toString());
                            route.setName(jsObj.get("name").toString());
                            route.setDistance(Integer.parseInt(jsObj.get("distance").toString()));
                            route.setCreatorID(jsObj.get("creatorID").toString());
                            ArrayList<Waypoint> arrayList = new ArrayList<Waypoint>();
                            JSONArray waypointsArray = jsObj.getJSONObject("links").getJSONArray("waypoints");
                            for(int i=0; i<waypointsArray.length(); i++){
                                Waypoint waypoint = new Waypoint();
                                waypoint.setId(waypointsArray.getJSONObject(i).get("id").toString());
                                waypoint.setLatitude(waypointsArray.getJSONObject(i).getLong("latitude"));
                                waypoint.setLongitude(waypointsArray.getJSONObject(i).getLong("longitude"));
                                arrayList.add(waypoint);
                            }
                            route.setArrayWaypoints(arrayList);
                            callback.onFetchSuccess(route);

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
        mRequestQueue.add(jsObjRequest);
    }

    public void save(final Route route, final FetchedCallback callback){
        // Instantiate the RequestQueue.

        String url = Constants.API_URL + "/api/routes";


        //to store the route info
        JSONObject requestBody = new JSONObject();
        JSONObject data = new JSONObject();




        try {
            data.put("id", route.getId());
            data.put("name", route.getName());
            data.put("distance", route.getDistance());
            data.put("creatorID",route.getCreatorID());
            requestBody.put("data", data);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Request a string response from the provided URL.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        callback.onFetchSuccess(null);
                        String url2 = Constants.API_URL + "/api/waypoints";
                        //to store the waypoints of the route
                        JSONObject requestBody2 = new JSONObject();
                        JSONObject data2 = new JSONObject();
                        try {
                            data2.put("waypoints", route.getWaypoints());
                            requestBody2.put("data", data2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url2, requestBody2,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        callback.onFetchSuccess(null);

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                callback.onFetchFail();
                            }
                        });

                    }
                    public void onErrorResponse(VolleyError error) {
                        callback.onFetchFail();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onFetchFail();
            }
        });
    }
    public void fetchAll(final FetchedArrayCallback callback) {

        String url = Constants.API_URL + "/api/routes?include=waypoints";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            ArrayList<Route> list = new ArrayList<Route>();
                            JSONArray jsObj = response.getJSONArray("data");
                            for (int i = 0; i < jsObj.length(); ++i) {
                                Route route = new Route();
                                JSONObject jsonRoute = jsObj.getJSONObject(i);

                                route.setId(jsonRoute.get("id").toString());
                                route.setName(jsonRoute.get("name").toString());
                                route.setDistance((jsonRoute.getDouble("distance")));
                                route.setCreatorID(jsonRoute.get("CreatorId").toString());

                                ArrayList<Waypoint> arrayList = new ArrayList<Waypoint>();
                                JSONArray waypointsArray = jsonRoute.getJSONObject("links").getJSONArray("waypoints");
                                for (int j = 0; j < waypointsArray.length(); j++) {
                                    Waypoint waypoint = new Waypoint();
                                    waypoint.setId(waypointsArray.getJSONObject(j).get("id").toString());
                                    waypoint.setLatitude(waypointsArray.getJSONObject(j).getLong("latitude"));
                                    waypoint.setLongitude(waypointsArray.getJSONObject(j).getLong("longitude"));
                                    arrayList.add(waypoint);
                                }
                                route.setArrayWaypoints(arrayList);
                                list.add(route);
                            }

                            callback.onFetchArraySuccess(list);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFetchArrayFail();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        callback.onFetchArrayFail();
                    }
                });
        mRequestQueue.add(jsObjRequest);
    }

    public void fetchAllByCreator(String creatorID, final FetchedArrayCallback callback) {


        String url = Constants.API_URL + "/api/users/" + creatorID + "/routes";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            ArrayList<Route> list = new ArrayList<Route>();
                            JSONObject jsObj = response.getJSONObject("data");
                            Iterator<String> keys = jsObj.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Route route = new Route();
                                try {
                                    JSONObject jsonRoute = jsObj.getJSONObject(key);
                                    route.setId(jsonRoute.get("id").toString());
                                    route.setName(jsonRoute.get("name").toString());
                                    route.setDistance(Integer.parseInt(jsonRoute.get("distance").toString()));
                                    route.setCreatorID(jsonRoute.get("creatorID").toString());
                                    list.add(route);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    callback.onFetchArrayFail();
                                }
                            }

                            callback.onFetchArraySuccess(list);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFetchArrayFail();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        callback.onFetchArrayFail();
                    }
                });
    }
}