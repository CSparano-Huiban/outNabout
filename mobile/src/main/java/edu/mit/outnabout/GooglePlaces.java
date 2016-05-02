package edu.mit.outnabout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.common.api.GoogleApiClient;

import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


public class GooglePlaces extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = GooglePlaces.class.getSimpleName();
    private String name = null;
    private Bitmap placePhoto = null;
    private Intent returnIntent = new Intent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        getData();
        Log.e("googleplaces","in google places");

      //  done();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }



    public void getData() {
        Log.e("googleplaces","in data");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("googleplaces","returned early");
            return;
        }
        Log.e("googleplaces","got passed return");
        PlaceFilter filter = new PlaceFilter();
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, filter);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                Log.e("googleplaces","callback");
                if (likelyPlaces.getCount() > 0){
                    Log.e("googleplaces","got places");
                    PlaceLikelihood best = likelyPlaces.get(0);
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        if (best.getLikelihood() < placeLikelihood.getLikelihood()){
                            best = placeLikelihood;
                        }
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    }
                    if (likelyPlaces.getCount()>1) {
                        best = likelyPlaces.get(1);
                    }
                    Log.e("googleplaces"," before frozen place");
                    Place frozen = best.getPlace().freeze();
                    //TextView text = (TextView) findViewById(R.id.textView);
                    //text.setText(frozen.getName());
                    name = (String)frozen.getName();
                    returnIntent.putExtra("name", name);
                    Log.e("googleplaces","before photo task");
                    placePhotosTask(frozen.getId());
                    Log.e("googleplaces","after photo task");
                    //Intent returnIntent = new Intent();
                    //returnIntent.putExtra("name", name);
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //placePhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //byte[] byteArray = stream.toByteArray();
                    //returnIntent.putExtra("photo", byteArray);
                    //setResult(RESULT_OK, returnIntent);
                    likelyPlaces.release();
                }
            }
        });
    }


    private void placePhotosTask(String input) {
        final String placeId = input; // Australian Cruise Group

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(200, 200) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                //mImageView.setImageResource(R.drawable.empty_photo);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                Log.v("I hate the world", "I retruned a photo");
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.

                //    mImageView.setImageBitmap(attributedPhoto.bitmap);
                    placePhoto = attributedPhoto.bitmap;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    placePhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    returnIntent.putExtra("photo", byteArray);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    // Display the attribution as HTML content if set.
                 /*  if (attributedPhoto.attribution == null) {
                       mText.setVisibility(View.GONE);
                   } else {
                       mText.setVisibility(View.VISIBLE);
                       mText.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                   }*/
                }else{
                    setResult(RESULT_OK, returnIntent);
                    Log.e("googleplaces","finished async task");
                    finish();
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
