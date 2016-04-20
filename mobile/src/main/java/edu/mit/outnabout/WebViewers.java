package edu.mit.outnabout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

// takes place_name, place_id, place_lat, place_long as extras
public class WebViewers extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener{

    String currentLatitude = "42.359189";
    String currentLongitude = "-71.093635";
    String currentLocation = "Massachusetts Institute of Technology";
    String currentDescription = "The Massachusetts Institute of Technology (MIT) is a private research university in Cambridge, Massachusetts. Founded in 1861 in response to the increasing industrialization of the United States, MIT adopted a European polytechnic university model and stressed laboratory instruction in applied science and engineering. Researchers worked on computers, radar, and inertial guidance during World War II and the Cold War. Post-war defense research contributed to the rapid expansion of the faculty and campus under James Killian. The current 168-acre (68.0 ha) campus opened in 1916 and extends over 1 mile (1.6 km) along the northern bank of the Charles River basin.";
    TextView titleTextView;
    TextView descriptionTextView;
    ImageView locationImage;
    Bitmap currentLocationImage;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        titleTextView = (TextView) findViewById(R.id.locationTitle);
        descriptionTextView = (TextView) findViewById(R.id.locationDescription);
        locationImage = (ImageView) findViewById(R.id.locationImage);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        if (extras == null) {
            displayDefaults();
        }else{
            displayFromExtras(extras);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void displayDefaults(){
        titleTextView.setText(currentLocation);
        descriptionTextView.setText(currentDescription);
        currentLocationImage = BitmapFactory.decodeResource(getResources(), R.drawable.MIT);
        locationImage.setImageBitmap(currentLocationImage);
    }

    public void displayFromExtras(Bundle extras){
        currentLocation = extras.getString("place_name");
        String locationId = extras.getString("place_id");
        placePhotosTask(locationId);
        currentLatitude = String.valueOf(extras.getDouble("place_lat"));
        currentLongitude = String.valueOf(extras.getDouble("place_long"));

//        byte[] byteArray = getIntent().getByteArrayExtra("image");
//        currentLocationImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //TODO: need to set discription

        currentDescription = currentLocation + " is the best place in all of boston";

        titleTextView.setText(currentLocation);
        descriptionTextView.setText(currentDescription);
    }

    public void googleClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/#q="+ currentLocation.replace(" ","+")));

        startActivity(intent);
    }

    public void wikiClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://en.wikipedia.org/wiki/"+ currentLocation.replace(" ","_")));

        startActivity(intent);
    }

    public void mapClick(View view) {
        String uriString = "geo:" + currentLatitude + ","+ currentLongitude + "?z=15";
        Uri geoUri =  Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void placePhotosTask(String input) {
        final String placeId = input; // Australian Cruise Group
//        final ImageView mImageView = (ImageView) findViewById(R.id.locationImage);
        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(locationImage.getWidth(), locationImage.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                //mImageView.setImageResource(R.drawable.empty_photo);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    locationImage.setImageBitmap(attributedPhoto.bitmap);

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
