package edu.mit.outnabout;

import android.Manifest;
import android.app.NotificationManager;
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


public class MyService extends Service implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private Bitmap placePhoto = null;
    String hardcodedContent = "The Ray & Maria Stata Center\n\n1/10 MIT locations discovered";
    // Acquire a reference to the system Location Manager
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private static final String TAG = "ServiceExample";
    PlaceFilter filter = new PlaceFilter();
    public MyService() {
    }

    private void setNotification(String name, byte[] picture, boolean hasPicture) {
        mGoogleApiClient.disconnect();
        Bitmap photo = null;
        if (hasPicture) {
            if (picture != null) {
                photo = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            } else {
                photo = BitmapFactory.decodeResource(getResources(), R.drawable.mit);
            }
            //  sendNotification(R.drawable.notification_icon, name, hardcodedContent, photo, photo);
        }
        Log.e(TAG, "setting notification");
        sendNotification(R.drawable.notification_icon, name, hardcodedContent, photo, photo);
    }

    
    public void sendNotification(int smallIcon, String title, String contentText, Bitmap largeIcon, Bitmap backgroundPhoto) {
        Log.e(TAG, "sending notification");
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
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
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
        int time = 6000;// * 60 * 60;
        int distance = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager != null ) {
          /*  if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)){

                Log.e(TAG, "using network");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, locationListener);
        } else {*/
            if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, locationListener);
                Log.e(TAG, "using gps");
            }

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
        Log.e(TAG,"in data");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG,"returned early");
            return;
        }
        Log.e(TAG,"got passed return");

        //PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
        //        .getCurrentPlace(mGoogleApiClient, filter);
        Log.e(TAG,"waiting for places");
        PlaceLikelihoodBuffer likelyPlaces = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, filter).await();
        Log.e(TAG,"before callback");
                Log.e(TAG,"callback");
                if (likelyPlaces.getCount() > 0){
                    Log.e(TAG,"got places");
                    PlaceLikelihood best = likelyPlaces.get(0);
                    if (likelyPlaces.getCount()>1) {
                        best = likelyPlaces.get(1);
                    }
                    Log.e(TAG," before frozen place");
                    Place frozen = best.getPlace().freeze();
                    Log.e(TAG,"before photo task");
                    likelyPlaces.release();
                    placePhotosTask(frozen.getId(), (String) frozen.getName());
                    Log.e(TAG,"after photo task");
                }
        Log.e(TAG,"after callback");
    }

    private void placePhotosTask(String input, final String name) {
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
                    setNotification(name, byteArray, true);

                }else{
                    setNotification(name, null, false);
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