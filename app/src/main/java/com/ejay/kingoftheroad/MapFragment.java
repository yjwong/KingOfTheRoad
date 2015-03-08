package com.ejay.kingoftheroad;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

//package com.ejay.kingoftheroad;
/**
 * Created by JohnKuan on 25/1/2015.
 */

public class MapFragment extends Fragment {
    private final static String TAG = "MapActivity";
    protected GoogleMap mMap;
    private MapView mMapView;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;

    public class MapHandler implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap map) {
            Log.e(TAG, "Testing!");
            mMap = map;
            prepareMapIfReady();
        }
    }

    public void updateMapFromSlideMap(){
        //TODO

    }

    class GoogleApiClientHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle bundle) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            prepareMapIfReady();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(getActivity(), "Failed to connect to Google Play Services", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.map);
        SwipeMapInfoFragment sf = new SwipeMapInfoFragment();

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new MapHandler());
        MapsInitializer.initialize(getActivity());

        // Set up the ViewPager.
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewPager);
        RouteDao dao = new RouteDao(getActivity());
        dao.fetchAll(new RouteDao.FetchedArrayCallback() {
            @Override
            public void onFetchArraySuccess(ArrayList<Route> list) {
                Log.i(TAG, "Fetched routes from server!");
                MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager(), list);
                pager.setAdapter(adapter);
            }

            @Override
            public void onFetchArrayFail() {
                Log.d(TAG, "Failed fetching routes from server.");
            }
        });

        // Obtain an instance of Google API client.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClientHandler())
                .addOnConnectionFailedListener(new GoogleApiClientHandler())
                .build();


        return rootView;
   }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    private void prepareMapIfReady() {
        if (mLocation != null && mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                public void onMyLocationChange(Location arg0) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position((new LatLng(arg0.getLatitude(), arg0.getLongitude()))).title("I am here!"));

                }
            });

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 8
            ));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 1500, null);


        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Route> mRoutes;

        public MyPagerAdapter(FragmentManager fm, ArrayList<Route> routes) {
            super(fm);
            mRoutes = routes;
        }

        @Override
        public Fragment getItem(int position) {
            return SwipeMapInfoFragment.newInstance(mRoutes.get(position));
//
//            //obtain a list of record using user_id from server
//           //TODO
//
//            MapRecord mR = new MapRecord();
//            MapRecord mR1 = new MapRecord("456","Mr La", "13.30min", "2.4Km", new ArrayList<MarkerOptions>());
//            final Route temp;
//            new RouteDao().fetch("1", new RouteDao.FetchedCallback() {
//                @Override
//                public void onFetchSuccess(Route route) {
//                    temp = route;
//                }
//
//                @Override
//                public void onFetchFail() {
//
//                }
//            });
//
//            switch (position) {
//                case 0:
//                    return SwipeMapInfoFragment.newInstance(mR);
//                case 1:
//                    return SwipeMapInfoFragment.newInstance(mR1);
//
//            }
//            return null;
        }


        @Override
        public int getCount() {
            return mRoutes.size();
        }
    }

    public static class SwipeMapInfoFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.swipe_map_info_fragment, container, false);

            TextView tv = (TextView) v.findViewById(R.id.titleText);
            tv.setText(getArguments().getString("title"));
            tv = (TextView) v.findViewById(R.id.id);
            tv.setText(getArguments().getString("id"));
            tv = (TextView) v.findViewById(R.id.kingName);
            tv.setText(getArguments().getString("kingName"));
            tv = (TextView) v.findViewById(R.id.bestTiming);
            tv.setText(getArguments().getString("bestTiming"));
            tv = (TextView) v.findViewById(R.id.distance);
            tv.setText(Double.toString(getArguments().getDouble("distance")));

            return v;
        }

        public static SwipeMapInfoFragment newInstance(Route route) {

            SwipeMapInfoFragment f = new SwipeMapInfoFragment();
            Bundle b = new Bundle();

            b.putString("title", "Route");
            b.putString("id", route.getId());
            b.putString("kingName", route.getKingName());
            b.putLong("bestTiming", route.getBestTiming());
            b.putDouble("distance", route.getDistance());


            f.setArguments(b);

            return f;
        }
    }
}
