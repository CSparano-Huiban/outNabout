package edu.mit.outnabout;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MyService extends Service implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private Bitmap placePhoto = null;

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private static final String TAG = "ServiceExample";
    PlaceFilter filter = new PlaceFilter();
    public MyService() {
    }
//    private List<String> landmarks =  Arrays.asList("Massachusetts Institute of Technology", "MIT Chapel",
//            "MIT Media Lab","David H. Koch Institute for Integrative Cancer Research", "Stratton Student Center",
//            "Morss Hall, Walker Memorial, MIT","Green Bldg", "Research Laboratory of Electronics",
//            "Wang Fitness Center","MIT Stata Center","Delta Kappa Epsilon");
    private List<String> landmarks =  Arrays.asList("Massachusetts Institute of Technology");

    private void setNotification(String name, byte[] picture, boolean hasPicture, Place place) {
        mGoogleApiClient.disconnect();
        Bitmap photo = null;
        if (hasPicture) {
            if (picture != null) {
                photo = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            } else {
                photo = BitmapFactory.decodeResource(getResources(), R.drawable.mit);
            }
        }

        sendNotification(R.drawable.notification_icon, name, place.getAddress().toString(), photo, photo, place);
    }

    
    public void sendNotification(int smallIcon, String title, String contentText, Bitmap largeIcon, Bitmap backgroundPhoto, Place place) {
        Log.e(TAG, "sending notification");
        Intent notificationIntent = new Intent(this, WebViewers.class);
        String myTag = "Debug notif in service";

        Log.e(myTag, title);
        Log.e(myTag, contentText);
        Log.e(myTag, (String) place.getName());
        Log.e(myTag, place.getId());
        Log.e(myTag, String.valueOf(place.getLatLng().latitude));
        Log.e(myTag, String.valueOf(place.getLatLng().longitude));

        notificationIntent.putExtra("place_name", place.getName());
        notificationIntent.putExtra("place_id", place.getId());
        notificationIntent.putExtra("place_lat", place.getLatLng().latitude);
        notificationIntent.putExtra("place_long", place.getLatLng().longitude);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Log.e(myTag,notificationIntent.getExtras().getString("place_name"));

        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setLargeIcon(largeIcon)
                        .setPriority(1) // High Priority: should enable heads-up notification
                        .setColor(Color.argb(0, 50, 200, 200))
                        .setContentIntent(intent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(backgroundPhoto));

        // Set an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onCreate() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                // .enableAutoManage(this, this)
                .build();
        Log.i(TAG, "Service onCreate");
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mGoogleApiClient.connect();
                // Called when a new location is found by the network location provider.
                Log.i(TAG, "attempting notification");
                // creates new thread to get the notification
                new Thread (new Runnable() {
                    public void run() {
                        getData();
                    }
                }).start();
                getLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        Log.i(TAG, "done with Service onCreate");
    }

    public void getLocation() {
        int time = 30000; //* 60 * 60;
        int distance = 30;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null ) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, locationListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        Log.i(TAG, "Service onDestroy");
    }

    public void getData() {
        Log.e(TAG,"in getData");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"returned early");
            return;
        }
        Log.e(TAG,"got passed return");

        //PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
        //        .getCurrentPlace(mGoogleApiClient, filter);
        Log.e(TAG,"waiting for places");
        PlaceLikelihoodBuffer likelyPlaces = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, filter).await();

        ArrayList<Place> result = new ArrayList<>();

        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
            if (landmarks.contains(placeLikelihood.getPlace().getName())){
                result.add(placeLikelihood.getPlace().freeze());
            }
           /* Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                    placeLikelihood.getPlace().getName(),
                    placeLikelihood.getLikelihood()));*/
        }
        likelyPlaces.release();
        Log.e(TAG,"messing with results");
        if (result.size() > 0){
            Place best = result.get(0);
            Log.e(TAG,"got places");
            //PlaceLikelihood best = likelyPlaces.get(0);
            if (result.size() > 1) {
                best = result.get(1);
            }
            Log.e(TAG, "before photo task");
            placePhotosTask(best.getId(), (String) best.getName(), best);
            Log.e(TAG,"after photo task");
        }
        Log.e(TAG,"end getDAta");
    }

    private void placePhotosTask(String input, final String name, final Place place) {
        final String placeId = input; // Australian Cruise Group

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(200, 200) {
            @Override
            protected void onPreExecute() {

            }


            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                Log.e("I hate the world", "I returned a photo");
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.

                    //    mImageView.setImageBitmap(attributedPhoto.bitmap);
                    placePhoto = attributedPhoto.bitmap;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    placePhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    setNotification(name, byteArray, true, place);

                }else{
                    setNotification(name, null, false, place);
                    Log.e(TAG,"finished async task");
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
