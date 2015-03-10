package com.ejay.kingoftheroad;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by JohnKuan on 7/3/2015.
 */
public class UserDao {

    private RequestQueue mRequestQueue;

    public UserDao(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public interface FetchedCallback {
        void onFetchSuccess(User user);
        void onFetchFail();
    }

    public interface FetchedArrayCallback{
        void onFetchArraySuccess(ArrayList<User> list);
        void onFetchArrayFail();
    }

    public void fetch(String id, final FetchedCallback callback){
        final User user = new User();
        String url = Constants.API_URL + "/api/users" + id;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject jsObj = response.getJSONObject("data");

                            user.setFirstname(jsObj.get("firstname").toString());
                            user.setLastname(jsObj.get("lastname").toString());
                            user.setRazerid(jsObj.get("razerid").toString());
                            user.setNickName(jsObj.get("nickname").toString());
                            user.setAvatarUrl(jsObj.get("avatarurl").toString());
                            user.setBirthDay(jsObj.get("birthday").toString());
                            user.setBirthMonth(jsObj.get("birthmonth").toString());
                            user.setBirthYear(jsObj.get("birthyear").toString());
                            user.setGender(jsObj.get("gender").toString());
                            user.setHeight(jsObj.get("height").toString());
                            user.setWeight(jsObj.get("weight").toString());
                            user.setUnit(jsObj.get("unit").toString());


                            callback.onFetchSuccess(user);

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

    public static void save(User user, final FetchedCallback callback){
        String url = Constants.API_URL + "/api/users";
        JSONObject requestBody = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("firstname", user.getFirstname());
            data.put("lastname", user.getLastname());
            data.put("razerid", user.getRazerid());
            data.put("avatarurl",user.getAvatarUrl());
            data.put("birthday",user.getBirthDay());
            data.put("birthmonth",user.getBirthMonth());
            data.put("birthyear",user.getBirthYear());
            data.put("gender",user.getGender());
            data.put("height",user.getHeight());
            data.put("weight",user.getWeight());
            data.put("unit",user.getUnit());

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

    public void fetchAllUser(final FetchedArrayCallback callback){

        String url = Constants.API_URL + "/api/users";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            ArrayList<User> list = new ArrayList<User>();
                            JSONObject jsObj = response.getJSONObject("data");
                            Iterator<String> keys = jsObj.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                User user = new User();
                                try {
                                    JSONObject jsUser = jsObj.getJSONObject(key);

                                    user.setFirstname(jsUser.get("firstname").toString());
                                    user.setLastname(jsUser.get("lastname").toString());
                                    user.setRazerid(jsUser.get("razerid").toString());
                                    user.setNickName(jsUser.get("nickname").toString());
                                    user.setAvatarUrl(jsUser.get("avatarurl").toString());
                                    user.setBirthDay(jsUser.get("birthday").toString());
                                    user.setBirthMonth(jsUser.get("birthmonth").toString());
                                    user.setBirthYear(jsUser.get("birthyear").toString());
                                    user.setGender(jsUser.get("gender").toString());
                                    user.setHeight(jsUser.get("height").toString());
                                    user.setWeight(jsUser.get("weight").toString());
                                    user.setUnit(jsUser.get("unit").toString());
                                    list.add(user);
                                }catch (Exception e) {
                                    e.printStackTrace();
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
        mRequestQueue.add(jsObjRequest);

    }

}


