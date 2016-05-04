package edu.mit.outnabout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class potential_home extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    List<String> nameList;
    List<String> idList;
    List<LatLng> latLongList;
    List<String> addressList;
//    List<Bitmap> imageList;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = GooglePlaces.class.getSimpleName();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop(){

        result.cancel();
        if (likelyPlaces != null)
        likelyPlaces.release();
        mGoogleApiClient.disconnect();
        Log.e(TAG, "leaving");

        super.onStop();
    }
    @Override
    protected void onPause(){
        result.cancel();
        if (likelyPlaces != null)
        likelyPlaces.release();
        mGoogleApiClient.disconnect();
        Log.e(TAG, "leaving");
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potential_home);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                //.enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
        /*new Thread (new Runnable() {
            public void run() {
                find();
            }
        }).start();*/
        find();

        Log.v("I hate this shit","I am doing something");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    ArrayList<Place> results = new ArrayList<Place>();

    public void searchNearMeSetUp() {
        final potential_home tempThis = this;

        ListView theListView = (ListView) findViewById(R.id.listView);

        List<String> places = new ArrayList<String>();

        nameList = new ArrayList<String>();
        idList = new ArrayList<String>();
        latLongList = new ArrayList<LatLng>();
        addressList = new ArrayList<String>();

        String placeName;
        String placeId;

        if (results != null) {

            for (int i = 0; i < Math.min(results.size(), 5); i++) {
                try {
                    Place currentPlace = (Place) results.get(i);
                    currentPlace.getAddress();
                    placeName = (String) currentPlace.getName();
                    placeId = (String) currentPlace.getId();
                    LatLng latLong = currentPlace.getLatLng();

                    places.add(placeName);
                    nameList.add(placeName);
                    idList.add(placeId);
                    latLongList.add(latLong);
                    addressList.add(currentPlace.getAddress().toString());


                } catch (Exception e) {
                    Log.e("Json Error", e.getMessage());
                    e.printStackTrace();
                }

            }
            nearMeCustomList listAdapter = new nearMeCustomList(this, nameList, addressList, idList, mGoogleApiClient);
            Log.e("work?","The list view called");
            theListView.setAdapter(listAdapter);
        }

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(tempThis, WebViewers.class);
                String currPlaceName = nameList.get(position);
                String currPlaceId = idList.get(position);
                LatLng currLatLong = latLongList.get(position);

                intent.putExtra("place_name", currPlaceName);
                intent.putExtra("place_id", currPlaceId);
                intent.putExtra("place_lat", currLatLong.latitude);
                intent.putExtra("place_long", currLatLong.longitude);

                startActivity(intent);
            }
        });
    }
    PendingResult<PlaceLikelihoodBuffer> result = null;
    PlaceLikelihoodBuffer likelyPlaces = null;
    public void find() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        PlaceFilter filter = new PlaceFilter();
        result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, filter);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlace) {
                results.clear();
                likelyPlaces = likelyPlace;
                if (likelyPlaces.getCount() > 0) {
                    PlaceLikelihood best = likelyPlaces.get(0);

                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        results.add(placeLikelihood.getPlace().freeze());
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                    if (likelyPlaces.getCount() > 1) {
                        best = likelyPlaces.get(1);
                    }
                    likelyPlaces.release();
                }
                Log.e("work?", "I hope i only get called once");
                searchNearMeSetUp();
            }
        });

    }
}
