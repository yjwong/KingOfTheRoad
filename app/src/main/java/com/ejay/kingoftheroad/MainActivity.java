package com.ejay.kingoftheroad;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.razer.android.nabuopensdk.AuthCheckCallback;
import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.BandListListener;
import com.razer.android.nabuopensdk.interfaces.NabuAuthListener;
import com.razer.android.nabuopensdk.models.NabuBand;
import com.razer.android.nabuopensdk.models.Scope;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {
    private final static String TAG = "MainActivity";
    private final static String[] NABU_OPENSDK_SCOPE = new String[] { Scope.COMPLETE };

    private final static String STATE_KEY_NABU_SDK_AUTHORIZED = "nabuSDKAuthorized";
    private final static String STATE_KEY_NABU_SDK_AUTHORIZATION_IN_PROGRESS = "nabuSDKAuthorizationInProgress";
    private final static String STATE_KEY_NABU_BANDS = "nabuBands";
    private final static String STATE_KEY_NABU_GET_BANDS_IN_PROGRESS = "nabuGetBandsInProgress";

    private NabuOpenSDK mNabuSDK;
    private boolean mNabuSDKAuthorized;
    private boolean mNabuSDKAuthorizationInProgress;
    private NabuBand[] mNabuBands;
    private boolean mNabuGetBandsInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Nabu OpenSDK.
        mNabuSDK = NabuOpenSDK.getInstance(this);

        // Check if we already have the list of bands.
        if (savedInstanceState != null) {
            mNabuSDKAuthorized = savedInstanceState.getBoolean(STATE_KEY_NABU_SDK_AUTHORIZED);
            mNabuSDKAuthorizationInProgress = savedInstanceState.getBoolean(STATE_KEY_NABU_SDK_AUTHORIZATION_IN_PROGRESS);
            mNabuBands = (NabuBand[]) savedInstanceState.getParcelableArray(STATE_KEY_NABU_BANDS);
            mNabuGetBandsInProgress = savedInstanceState.getBoolean(STATE_KEY_NABU_GET_BANDS_IN_PROGRESS);
        }

        // At this point, we are not sure if we have been authorized. So let's check.
        if (!mNabuSDKAuthorized) {
            if (!mNabuSDKAuthorizationInProgress) {
                mNabuSDKAuthorizationInProgress = true;
                mNabuSDK.checkAppAuthorized(this, new MyNabuAuthCheckCallback());
            }
        } else {
            getNabuBandList();
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

    private void getNabuBandList() {
        if (mNabuBands == null && !mNabuGetBandsInProgress) {
            mNabuGetBandsInProgress = true;
            mNabuSDK.getBandList(this, new MyNabuBandListListener());
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
            mNabuSDKAuthorized = true;
            mNabuSDKAuthorizationInProgress = false;
            getNabuBandList();
        }

        @Override
        public void onFailed(String errorMessage) {
            Log.d(TAG, "onFailed: " + errorMessage);
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
            Log.d(TAG, "onAuthSuccess: " + s);
            mNabuSDKAuthorized = true;
            mNabuSDKAuthorizationInProgress = false;
            getNabuBandList();
        }

        @Override
        public void onAuthFailed(String s) {
            Log.d(TAG, "onAuthFailed");
            mNabuSDKAuthorizationInProgress = false;
        }
    }

    private class MyNabuBandListListener implements BandListListener {
        private final static String TAG = "MyNabuBandListListener";

        @Override
        public void onReceiveData(NabuBand[] nabuBands) {
            mNabuBands = nabuBands;
            mNabuGetBandsInProgress = false;

            // Print debugging information.
            for (NabuBand band : nabuBands) {
                Log.v(TAG, "Band detected: " + band.name + " (" + band.bandId + ")");
            }
        }

        @Override
        public void onReceiveFailed(String s) {
            Log.d(TAG, "onReceiveFailed: " + s);
            mNabuGetBandsInProgress = false;
        }
    }
}
