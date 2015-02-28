package com.ejay.kingoftheroad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothGatt;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.razer.android.nabuopensdk.AuthCheckCallback;
import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.BandListListener;
import com.razer.android.nabuopensdk.interfaces.LiveDataListener;
import com.razer.android.nabuopensdk.interfaces.NabuAuthListener;
import com.razer.android.nabuopensdk.models.NabuBand;
import com.razer.android.nabuopensdk.models.NabuFitness;
import com.razer.android.nabuopensdk.models.Scope;


public class MainActivity extends ActionBarActivity {
    private final static String TAG = "MainActivity";
    private final static String[] NABU_OPENSDK_SCOPE = new String[] { Scope.COMPLETE };

    private final static String STATE_KEY_NABU_SDK_AUTHORIZED = "nabuSDKAuthorized";
    private final static String STATE_KEY_NABU_SDK_AUTHORIZATION_IN_PROGRESS = "nabuSDKAuthorizationInProgress";
    private final static String STATE_KEY_NABU_BANDS = "nabuBands";
    private final static String STATE_KEY_NABU_GET_BANDS_IN_PROGRESS = "nabuGetBandsInProgress";
    private final static String STATE_KEY_NABU_CONNECTED_BANDS = "nabuConnectedBands";
    private final static String STATE_KEY_NABU_GET_CONNECTED_BANDS_IN_PROGRESS = "nabuGetConnectedBandsInProgress";
    private final static String STATE_KEY_NABU_LIVE_FITNESS_ENABLED = "nabuLiveFitnessEnabled";

    private NabuOpenSDK mNabuSDK;
    private boolean mNabuSDKAuthorized;
    private boolean mNabuSDKAuthorizationInProgress;
    private NabuBand[] mNabuBands;
    private boolean mNabuGetBandsInProgress;
    private NabuBand[] mNabuConnectedBands;
    private boolean mNabuGetConnectedBandsInProgress;
    private boolean mNabuLiveFitnessEnabled;
    protected GoogleMap mMap;

    private String[] drawerListViewItems;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Nabu OpenSDK.
        Log.i(TAG, "Initializing Nabu OpenSDK...");
        mNabuSDK = NabuOpenSDK.getInstance(this);

        // Check if we already have the list of bands.
        if (savedInstanceState != null) {
            mNabuSDKAuthorized = savedInstanceState.getBoolean(STATE_KEY_NABU_SDK_AUTHORIZED);
            mNabuSDKAuthorizationInProgress = savedInstanceState.getBoolean(STATE_KEY_NABU_SDK_AUTHORIZATION_IN_PROGRESS);
            mNabuBands = (NabuBand[]) savedInstanceState.getParcelableArray(STATE_KEY_NABU_BANDS);
            mNabuGetBandsInProgress = savedInstanceState.getBoolean(STATE_KEY_NABU_GET_BANDS_IN_PROGRESS);
            mNabuConnectedBands = (NabuBand[]) savedInstanceState.getParcelableArray(STATE_KEY_NABU_CONNECTED_BANDS);
            mNabuGetConnectedBandsInProgress = savedInstanceState.getBoolean(STATE_KEY_NABU_GET_CONNECTED_BANDS_IN_PROGRESS);
            mNabuLiveFitnessEnabled = savedInstanceState.getBoolean(STATE_KEY_NABU_LIVE_FITNESS_ENABLED);
        }

        // At this point, we are not sure if we have been authorized. So let's check.
        if (!mNabuSDKAuthorized) {
            Log.i(TAG, "Nabu OpenSDK not authorized, performing authorization...");
            if (!mNabuSDKAuthorizationInProgress) {
                mNabuSDKAuthorizationInProgress = true;
                mNabuSDK.checkAppAuthorized(this, new MyNabuAuthCheckCallback());
            }
        } else {
            nabuGetAllBandList();
        }

        // get list items from strings.xml
        drawerListViewItems = getResources().getStringArray(R.array.items);
        // get ListView defined in activity_main.xml
        drawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerListViewItems));

        // 2. App Icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // 2.1 create ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // 2.2 Set actionBarDrawerToggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // 2.3 enable and show "up" arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // just styling option
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this, ((TextView) view).getText(), Toast.LENGTH_LONG).show();
            displayView(position);

        }
    }

    private void displayView(int position) {

        Fragment fragment = null;
        switch(position) {
            case 0:
                fragment = new HomeScreen();
                break;
            case 1:
                fragment = new MapFragment();
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            drawerListView.setItemChecked(position, true);
            drawerListView.setSelection(position);
            setTitle(drawerListViewItems[position]);
            drawerLayout.closeDrawer(drawerListView);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save our authorization state and list of bands.
        outState.putBoolean(STATE_KEY_NABU_SDK_AUTHORIZED, mNabuSDKAuthorized);
        outState.putBoolean(STATE_KEY_NABU_SDK_AUTHORIZATION_IN_PROGRESS, mNabuSDKAuthorizationInProgress);
        outState.putParcelableArray(STATE_KEY_NABU_BANDS, mNabuBands);
        outState.putBoolean(STATE_KEY_NABU_GET_BANDS_IN_PROGRESS, mNabuGetBandsInProgress);
        outState.putParcelableArray(STATE_KEY_NABU_CONNECTED_BANDS, mNabuConnectedBands);
        outState.putBoolean(STATE_KEY_NABU_GET_CONNECTED_BANDS_IN_PROGRESS, mNabuGetConnectedBandsInProgress);
        outState.putBoolean(STATE_KEY_NABU_LIVE_FITNESS_ENABLED, mNabuLiveFitnessEnabled);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Pause live fitness data if it's enabled.
        if (mNabuLiveFitnessEnabled && mNabuBands != null && mNabuBands.length > 0) {
            mNabuSDK.disableFitness(this, mNabuBands[0], new MyNabuLiveDataListener());
        }

        mNabuSDK.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNabuSDK.onResume(this);

        // Resume live fitness data.
        if (mNabuLiveFitnessEnabled && mNabuBands != null && mNabuBands.length > 0) {
            mNabuSDK.enableFitness(this, mNabuBands[0], new MyNabuLiveDataListener());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNabuSDK.onDestroy(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private void nabuGetAllBandList() {
        nabuGetBandList();
        nabuGetConnectedBandList();
    }

    private void nabuGetBandList() {
        if (mNabuBands == null && !mNabuGetBandsInProgress) {
            mNabuGetBandsInProgress = true;
            mNabuSDK.getBandList(this, new MyNabuBandListListener());
        }
    }

    private void nabuGetConnectedBandList() {
        if (mNabuConnectedBands == null && !mNabuGetConnectedBandsInProgress) {
            mNabuGetConnectedBandsInProgress = true;
            mNabuSDK.getConnectedBandList(this, new MyNabuConnectedBandListListener());
        }
    }



    /**
     * Callback that is invoked when the authorization check is complete.
     */
    private class MyNabuAuthCheckCallback implements AuthCheckCallback {
        private final static String TAG = "MyNabuAuthCheckCallback";

        @Override
        public void onSuccess(boolean isAuthorized) {
            Log.v(TAG, "onSuccess");
            if (BuildConfig.DEBUG && !isAuthorized) {
                throw new AssertionError("isAuthorized != false");
            }

            // We are authorized! Let's get the list of bands.
            mNabuSDK.initiate(MainActivity.this, Constants.NABU_OPENSDK_CLIENT_ID,
                    NABU_OPENSDK_SCOPE, new MyNabuAuthListener());
            //mNabuSDKAuthorized = true;
            //mNabuSDKAuthorizationInProgress = false;
            //nabuGetAllBandList();
        }

        @Override
        public void onFailed(String errorMessage) {
            Log.w(TAG, "onFailed: " + errorMessage);
            mNabuSDK.initiate(MainActivity.this, Constants.NABU_OPENSDK_CLIENT_ID,
                    NABU_OPENSDK_SCOPE, new MyNabuAuthListener());
        }
    }

    /**
     * Authentication listener for the Nabu SDK.
     */
    private class MyNabuAuthListener implements NabuAuthListener {
        private final static String TAG = "MyNabuAuthListener";

        @Override
        public void onAuthSuccess(String s) {
            Log.v(TAG, "onAuthSuccess: " + s);
            mNabuSDKAuthorized = true;
            mNabuSDKAuthorizationInProgress = false;
            nabuGetAllBandList();
        }

        @Override
        public void onAuthFailed(String s) {
            Log.w(TAG, "onAuthFailed");
            mNabuSDKAuthorizationInProgress = false;
        }
    }

    private class MyNabuBandListListener implements BandListListener {
        private final static String TAG = "MyNabuBandListListener";

        @Override
        public void onReceiveData(NabuBand[] nabuBands) {
            Log.v(TAG, "onReceiveData");

            mNabuBands = nabuBands;
            mNabuGetBandsInProgress = false;

            // Print debugging information.
            for (NabuBand band : nabuBands) {
                Log.d(TAG, "Band detected: " + band.toString());
            }
        }

        @Override
        public void onReceiveFailed(String s) {
            Log.w(TAG, "onReceiveFailed: " + s);
            mNabuGetBandsInProgress = false;
        }
    }

    private class MyNabuConnectedBandListListener implements BandListListener {
        private final static String TAG = "MyNabuConnectedBandListListener";

        @Override
        public void onReceiveData(NabuBand[] nabuBands) {
            Log.v(TAG, "onReceiveData");

            mNabuConnectedBands = nabuBands;
            mNabuGetConnectedBandsInProgress = false;

            // Print debugging information.
            for (NabuBand band : nabuBands) {
                Log.v(TAG, "Connected band detected: " + band.toString());
            }

            // Use the first band and listen for live fitness data.
            // This should be in the connected band list, but for some reason the API isn't working.
            if (nabuBands.length > 0) {
                mNabuSDK.enableFitness(MainActivity.this, nabuBands[0], new MyNabuLiveDataListener());
                mNabuLiveFitnessEnabled = true;
            }
        }

        @Override
        public void onReceiveFailed(String s) {
            Log.w(TAG, "onReceiveFailed: " + s);
            mNabuGetConnectedBandsInProgress = false;
        }
    }

    private class MyNabuLiveDataListener implements LiveDataListener {
        private final static String TAG = "MyNabuLiveDataListener";

        @Override
        public void onConnectionStateChanged(NabuBand band, int status) {
            Log.v(TAG, "onConnectionStateChanged: band=" + band + ", status=" + status);
            if (status != BluetoothGatt.STATE_CONNECTED) {
                mNabuLiveFitnessEnabled = false;
            }
        }

        @Override
        public void onLiveDataReceived(NabuFitness nabuFitness) {
            Log.v(TAG, "onLiveDataReceived: " + nabuFitness.toString());
        }

        @Override
        public void onError(String errorMessage) {
            Log.w(TAG, "onError: " + errorMessage);
            mNabuLiveFitnessEnabled = false;
        }
    }
}