package com.ejay.kingoftheroad;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new MapHandler());
        MapsInitializer.initialize(getActivity());

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
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17),1500,null);
        }
    }
}
