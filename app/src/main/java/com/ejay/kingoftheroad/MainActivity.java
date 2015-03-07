package com.ejay.kingoftheroad;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.razer.android.nabuopensdk.AuthCheckCallback;
import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.BandListListener;
import com.razer.android.nabuopensdk.interfaces.LiveDataListener;
import com.razer.android.nabuopensdk.interfaces.NabuAuthListener;
import com.razer.android.nabuopensdk.interfaces.UserProfileListener;
import com.razer.android.nabuopensdk.models.NabuBand;
import com.razer.android.nabuopensdk.models.NabuFitness;
import com.razer.android.nabuopensdk.models.Scope;
import com.razer.android.nabuopensdk.models.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;


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
    private final static String STATE_KEY_NABU_USER_PROFILE = "nabuUserProfile";
    private final static String STATE_KEY_NABU_GET_USER_PROFILE_IN_PROGRESS = "nabuGetUserProfileInProgress";

    private NabuOpenSDK mNabuSDK;
    private boolean mNabuSDKAuthorized;
    private boolean mNabuSDKAuthorizationInProgress;
    private NabuBand[] mNabuBands;
    private boolean mNabuGetBandsInProgress;
    private NabuBand[] mNabuConnectedBands;
    private boolean mNabuGetConnectedBandsInProgress;
    private boolean mNabuLiveFitnessEnabled;
    private ParcelableUserProfile mNabuUserProfile;
    private boolean mNabuGetUserProfileInProgress;

    private Toolbar mToolbar;
    private Spinner mDrawerBandSelectionSpinner;
    private DrawerBandSelectionAdapter mDrawerBandSelectionAdapter;
    private String[] mDrawerListViewItems;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawer;
    private ListView mDrawerListView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mTitle = mDrawerTitle = getTitle();

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
            mNabuGetUserProfileInProgress = savedInstanceState.getBoolean(STATE_KEY_NABU_GET_USER_PROFILE_IN_PROGRESS);
            mNabuUserProfile = savedInstanceState.getParcelable(STATE_KEY_NABU_USER_PROFILE);
        }

        // At this point, we are not sure if we have been authorized. So let's check.
        if (!mNabuSDKAuthorized) {
            Log.i(TAG, "Nabu OpenSDK not authorized, performing authorization...");
            if (!mNabuSDKAuthorizationInProgress) {
                mNabuSDKAuthorizationInProgress = true;
                mNabuSDK.checkAppAuthorized(this, new MyNabuAuthCheckCallback());
            }
        } else {
            nabuRunPostAuthActions();
        }

        // get list items from strings.xml
        mDrawerListViewItems = getResources().getStringArray(R.array.items);
        // get ListView defined in activity_main.xml
        mDrawerListView = (ListView) findViewById(R.id.drawer_list_view);
        mDrawer = (LinearLayout) findViewById(R.id.drawer);

        // Set the adapter for the list view
        mDrawerListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.drawer_list_view_header, null, false));
        mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerListViewItems));

        // Set up the navigation drawer.
        mDrawerBandSelectionAdapter = new DrawerBandSelectionAdapter(this);
        mDrawerBandSelectionSpinner = (Spinner) findViewById(R.id.drawer_band_selection_spinner);
        mDrawerBandSelectionSpinner.setAdapter(mDrawerBandSelectionAdapter);

        // 2. App Icon
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // 2.1 create ActionBarDrawerToggle
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        }
        ;

        // 2.2 Set mActionBarDrawerToggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        // 2.3 enable and show "up" arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // just styling option
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerListView.setItemChecked(position, true);
            mDrawerListView.setSelection(position);
            setTitle(mDrawerListViewItems[position]);
            mDrawerLayout.closeDrawer(mDrawer);
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
        outState.putBoolean(STATE_KEY_NABU_GET_USER_PROFILE_IN_PROGRESS, mNabuGetUserProfileInProgress);
        outState.putParcelable(STATE_KEY_NABU_USER_PROFILE, mNabuUserProfile);
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
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            mDrawerLayout.closeDrawer(mDrawer);
        } else {
            super.onBackPressed();
        }
    }

    /* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // toggle nav drawer on selecting action bar app icon/title
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void nabuRunPostAuthActions() {
        nabuGetBandList();
        nabuGetConnectedBandList();
        nabuGetUserProfile();
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

    private void nabuGetUserProfile() {
        if (!mNabuGetUserProfileInProgress) {
            mNabuGetUserProfileInProgress = true;
            mNabuSDK.getUserProfile(this, new MyNabuUserProfileListener());
        }
    }

    private void checkIfNabuLoaded() {
        if (mNabuBands != null && mNabuConnectedBands != null && mNabuUserProfile != null) {
            Log.d(TAG, "All data from Nabu loaded.");

            // Populate the user avatar.
            Picasso.with(this).load(mNabuUserProfile.getUserProfile().avatarUrl)
                    .into((ImageView) findViewById(R.id.drawer_user_avatar));

            // Populate the band list.
            mDrawerBandSelectionAdapter.addAll(mNabuBands);

            // Stop the progress bar on the navigation area.
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.drawer_user_progress_bar);
            AlphaAnimation progressBarOutAnimation = new AlphaAnimation(1.0f, 0.0f);
            progressBarOutAnimation.setDuration(500);
            progressBarOutAnimation.setFillAfter(true);
            progressBarOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });

            progressBar.startAnimation(progressBarOutAnimation);
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
            //nabuRunPostAuthActions();
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
            nabuRunPostAuthActions();
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
            checkIfNabuLoaded();

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
            checkIfNabuLoaded();

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

    private class MyNabuUserProfileListener implements UserProfileListener {
        private final static String TAG = "MyNabuUserProfileListener";

        @Override
        public void onReceiveData(UserProfile profile) {
            mNabuUserProfile = new ParcelableUserProfile(profile);
            mNabuGetUserProfileInProgress = false;
            checkIfNabuLoaded();
        }

        @Override
        public void onReceiveFailed(String s) {

        }
    }

    private class DrawerBandSelectionAdapter extends ArrayAdapter<NabuBand> {
        public DrawerBandSelectionAdapter(Context context) {
            super(context, R.layout.drawer_band_selection_spinner_view, R.id.band_name);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NabuBand band = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_band_selection_spinner_view, parent, false);
            }

            TextView tvBandName = (TextView) convertView.findViewById(R.id.band_name);
            TextView tvBandModel = (TextView) convertView.findViewById(R.id.band_model);
            tvBandName.setText(band.name);
            tvBandModel.setText(band.model);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            NabuBand band = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_band_selection_spinner_drop_down_view, parent, false);
            }

            TextView tvBandName = (TextView) convertView.findViewById(R.id.band_name);
            tvBandName.setText(band.name);

            return convertView;
        }
    }

    private static class ParcelableUserProfile implements Parcelable {
        private UserProfile mUserProfile = new UserProfile();

        public ParcelableUserProfile(UserProfile profile) {
            mUserProfile = profile;
        }

        private ParcelableUserProfile(Parcel in) {
            mUserProfile.razerID = in.readString();
            mUserProfile.avatarUrl = in.readString();
            mUserProfile.birthDay = in.readString();
            mUserProfile.birthMonth = in.readString();
            mUserProfile.birtyYear = in.readString();
            mUserProfile.firstname = in.readString();
            mUserProfile.lastname = in.readString();
            mUserProfile.nickName = in.readString();
            mUserProfile.gender = in.readString();
            mUserProfile.height = in.readString();
            mUserProfile.weight = in.readString();
            mUserProfile.unit = in.readString();
        }

        public UserProfile getUserProfile() {
            return mUserProfile;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ParcelableUserProfile> CREATOR =
                new Creator<ParcelableUserProfile>() {
                    @Override
                    public ParcelableUserProfile createFromParcel(Parcel source) {
                        return new ParcelableUserProfile(source);
                    }

                    @Override
                    public ParcelableUserProfile[] newArray(int size) {
                        return new ParcelableUserProfile[size];
                    }
                };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mUserProfile.razerID);
            dest.writeString(mUserProfile.avatarUrl);
            dest.writeString(mUserProfile.birthDay);
            dest.writeString(mUserProfile.birthMonth);
            dest.writeString(mUserProfile.birtyYear);
            dest.writeString(mUserProfile.firstname);
            dest.writeString(mUserProfile.lastname);
            dest.writeString(mUserProfile.nickName);
            dest.writeString(mUserProfile.gender);
            dest.writeString(mUserProfile.height);
            dest.writeString(mUserProfile.weight);
            dest.writeString(mUserProfile.unit);
        }
    }
}