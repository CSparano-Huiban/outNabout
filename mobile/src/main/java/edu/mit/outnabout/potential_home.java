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
//    List<Bitmap> imageList;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = GooglePlaces.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potential_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        find();

        Log.v("I hate this shit","I am doing something");

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
        Log.v("I hate this shit","I have failed you");
        Log.v("I hate this shit",connectionResult.toString());
    }

    ArrayList<Place> results = new ArrayList<Place>();

    public void searchNearMeSetUp() {
        final potential_home tempThis = this;

        ListView theListView = (ListView) findViewById(R.id.listView);

        List<String> places = new ArrayList<String>();

        nameList = new ArrayList<String>();
        idList = new ArrayList<String>();
        latLongList = new ArrayList<LatLng>();
//        imageList = new ArrayList<Bitmap>();


        ArrayAdapter<String> resultsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,places);

        String placeName;
        String placeId;
        Bitmap placeImage;
        if (results != null) {

            for (int i = 0; i < Math.min(results.size(), 5); i++) {
                try {
                    Place currentPlace = (Place) results.get(i);
                    //JSONObject tempJsonParse = currentPlace.getJSONObject("fields");
                    placeName = (String) currentPlace.getName();
                    placeId = (String) currentPlace.getId();
                    LatLng latLong = currentPlace.getLatLng();

//                    placeImage = currentPlace.;
                    places.add(placeName);
                    nameList.add(placeName);
                    idList.add(placeId);
                    latLongList.add(latLong);
//                    imageList.add(placeImage);

                } catch (Exception e) {
                    Log.e("Json Error", e.getMessage());
                    e.printStackTrace();
                }

            }  // end the for loop
            theListView.setAdapter(resultsAdapter);
        }

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(tempThis, WebViewers.class);
                String currPlaceName = nameList.get(position);
                String currPlaceId = idList.get(position);
                LatLng currLatLong = latLongList.get(position);
//                Bitmap currPlaceImage = imageList.get(position);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                currPlaceImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();

                intent.putExtra("place_name", currPlaceName);
                intent.putExtra("place_id", currPlaceId);
                intent.putExtra("place_lat", currLatLong.latitude);
                intent.putExtra("place_long", currLatLong.longitude);

//                intent.putExtra("image",byteArray);

                startActivity(intent);
            }
        });
    }
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
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, filter);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                results.clear();
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
                    // Place frozen = best.getPlace().freeze();
                    //TextView text = (TextView) findViewById(R.id.textView);
                    //text.setText(frozen.getName());
                    //placePhotosTask(frozen.getId());
                    //photo(frozen);
                    likelyPlaces.release();
                }
                Log.v("Booooo",likelyPlaces.toString());
                searchNearMeSetUp();
            }
        });

    }
    private void placePhotosTask(String input) {
        final String placeId = input; // Australian Cruise Group
        final ImageView mImageView = (ImageView) findViewById(R.id.locationImage);
        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(mImageView.getWidth(), mImageView.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                //mImageView.setImageResource(R.drawable.empty_photo);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    mImageView.setImageBitmap(attributedPhoto.bitmap);

                    // Display the attribution as HTML content if set.
                 /*  if (attributedPhoto.attribution == null) {
                       mText.setVisibility(View.GONE);
                   } else {
                       mText.setVisibility(View.VISIBLE);
                       mText.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                   }
*/
                }
            }
        }.execute(placeId);
    }
    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first (and only) parameter.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        /**
         * Holder for an image and its attribution.
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }
}
