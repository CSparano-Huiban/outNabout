package edu.mit.outnabout;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.support.v4.app.NotificationCompat;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

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

    String hardcodedTitle = "New Location Found!";
    String hardcodedContent = "The Ray & Maria Stata Center\n\n1/10 MIT locations discovered";
    String name;
    Bitmap photo;


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
    public void getPlaceAndNotify() {
        Intent intent = new Intent(this, GooglePlaces.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
//            Button text = (Button) findViewById(R.id.geofence_button);
            mGPSLargeText.setText("I am done exploring");
            mGPSSmallText.setText("Stop OutNabout from sending notifications.");

            if (resultCode == Activity.RESULT_OK) {
                name = data.getStringExtra("name");
                byte[] byteArray = getIntent().getByteArrayExtra("photo");
                if (byteArray != null) {
                    photo = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                } else {
                    photo = BitmapFactory.decodeResource(getResources(), R.drawable.mit);
                }

                sendNotification(R.drawable.notification_icon, name, hardcodedContent, photo, photo);
            }
        }
    }//onActivityResult

    public void notify(View view) {
        getPlaceAndNotify();
    }

    public void sendNotification(int smallIcon, String title, String contentText, Bitmap largeIcon, Bitmap backgroundPhoto) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setLargeIcon(largeIcon)
                        .setPriority(1) // High Priority: should enable heads-up notification
                        .setColor(Color.argb(0, 50, 200, 200))
                        .extend(new NotificationCompat.WearableExtender().setBackground(backgroundPhoto));

        // Set an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

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
