package edu.mit.outnabout;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class HomeActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "HomeActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    // Button for toggling the process of turning the GPS search on and off.
    private TextView mGPSLargeText;
    private TextView mGPSSmallText;

    private boolean exploring = false;

    private Intent serviceIntent;
    public void toggleGPS(View view){

        if (!exploring) {
            serviceIntent = new Intent(this, MyService.class);
            startService(serviceIntent);
            mGPSLargeText.setText("I am done exploring");
            mGPSSmallText.setText("Stop OutNabout from sending notifications.");
        }else{
            stopService(serviceIntent);
            mGPSLargeText.setText("Begin Exploring");
            mGPSSmallText.setText("Let OutNAbout remind you when you are near something cool.");
        }
        exploring = !exploring;
    }

    public void moveToMaps(View view) {
        Intent intent = new Intent(this, potential_home.class);
        startActivity(intent);
    }

    // call this method in order for google places to set the name and photo global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the UI widget.
        mGPSLargeText = (TextView) findViewById(R.id.geoLarge);
        mGPSSmallText = (TextView) findViewById(R.id.geoSmall);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    public void onResult(Status status) {}

    public void acheveClicked(View view) {
        Intent intent = new Intent(this, acheve.class);
        startActivity(intent);
    }
}
