package com.ejay.kingoftheroad;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.BandListListener;
import com.razer.android.nabuopensdk.interfaces.NabuAuthListener;
import com.razer.android.nabuopensdk.models.NabuBand;
import com.razer.android.nabuopensdk.models.Scope;


public class MainActivity extends ActionBarActivity {
    public final static String TAG = "MainActivity";
    public final static String[] NABU_OPENSDK_SCOPE = new String[] { Scope.COMPLETE };

    private NabuOpenSDK mNabuSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Nabu OpenSDK.
        mNabuSDK = NabuOpenSDK.getInstance(this);
        mNabuSDK.initiate(this, Constants.NABU_OPENSDK_CLIENT_ID, NABU_OPENSDK_SCOPE,
                new MyNabuAuthListener());
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

    /**
     * Authentication listener for the Nabu SDK.
     */
    private class MyNabuAuthListener implements NabuAuthListener {
        @Override
        public void onAuthSuccess(String s) {
            Log.d(TAG, "onAuthSuccess");
            Toast.makeText(MainActivity.this, "Nabu authentication successful: " + s,
                    Toast.LENGTH_SHORT).show();
            mNabuSDK.getBandList(MainActivity.this, new MyNabuBandListListener());
        }

        @Override
        public void onAuthFailed(String s) {
            Log.d(TAG, "onAuthFailed");
            Toast.makeText(MainActivity.this, "Nabu authentication failed: " + s,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class MyNabuBandListListener implements BandListListener {
        @Override
        public void onReceiveData(NabuBand[] nabuBands) {
            for (NabuBand band : nabuBands) {
                Log.d(TAG, "Band detected: " + band.name);
            }
        }

        @Override
        public void onReceiveFailed(String s) {

        }
    }
}
