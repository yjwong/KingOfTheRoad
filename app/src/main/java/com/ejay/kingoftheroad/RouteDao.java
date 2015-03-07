package com.ejay.kingoftheroad;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class RouteDao {

    public RouteDao(){

    }

    public interface FetchedCallback {
        void onFetchSuccess(Route route);
        void onFetchFail();
    }

    public void fetch(String id, final FetchedCallback callback){
        final Route route = new Route();
        String url = Constants.API_URL + "/api/routes" + id;
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
    }

    public void save(Route route, final FetchedCallback callback){
        // Instantiate the RequestQueue.

        String url = Constants.API_URL + "/api/routes";

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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFetchFail();
            }
        });
    }

    public ArrayList<Route> fetchAll(){
        return null;
    }

    public ArrayList<Route> fetchAllByCreator(String creatorID){
        return null;
    }
}
