package edu.mit.outnabout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PotentialHome extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    List<String> nameList;
    List<String> idList;
    List<LatLng> latLongList;
    List<String> addressList;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "PLACES_LIST";

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
                .build();
        mGoogleApiClient.connect();
        find();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    ArrayList<Place> results = new ArrayList<Place>();

    public void searchNearMeSetUp() {
        final PotentialHome tempThis = this;

        ListView theListView = (ListView) findViewById(R.id.listView);

        nameList = new ArrayList<>();
        idList = new ArrayList<>();
        latLongList = new ArrayList<>();
        addressList = new ArrayList<>();

        String placeName;
        String placeId;

        if (results != null) {

            for (int i = 0; i < Math.min(results.size(), 5); i++) {
                try {
                    Place currentPlace = results.get(i);
                    currentPlace.getAddress();
                    placeName = (String) currentPlace.getName();
                    placeId = currentPlace.getId();
                    LatLng latLong = currentPlace.getLatLng();

                    nameList.add(placeName);
                    idList.add(placeId);
                    latLongList.add(latLong);
                    addressList.add(currentPlace.getAddress().toString());

                } catch (Exception e) {
                    Log.e("Json Error", e.getMessage());
                    e.printStackTrace();
                }

            }
            NearMeCustomList listAdapter = new NearMeCustomList(this, nameList, addressList);

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
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        results.add(placeLikelihood.getPlace().freeze());
                    }
                    likelyPlaces.release();
                }
                searchNearMeSetUp();
            }
        });

    }
}
