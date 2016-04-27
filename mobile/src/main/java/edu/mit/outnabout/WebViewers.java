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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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

    private String LOG_MESSAGE = "WebAPIExample";

    private String apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        currentLocationImage = BitmapFactory.decodeResource(getResources(), R.drawable.mit);
        locationImage.setImageBitmap(currentLocationImage);
        getWebResult();
    }

    public void displayFromExtras(Bundle extras){
        currentLocation = extras.getString("place_name");
        String locationId = extras.getString("place_id");
        placePhotosTask(locationId);
        currentLatitude = String.valueOf(extras.getDouble("place_lat"));
        currentLongitude = String.valueOf(extras.getDouble("place_long"));

        currentDescription = "Our description for " + currentLocation + " is not available yet please use the google button below to learn more";
        titleTextView.setText(currentLocation);
        descriptionTextView.setText(currentDescription);
        getWebResult();
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
        String uriString = String.format("geo:0,0?q=" + currentLatitude + ","+ currentLongitude);
        Log.v("I hate the world",uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        startActivity(intent);
    }

    private void placePhotosTask(String input) {
        new PhotoTask(500, 500) {

            @Override
            protected void onPreExecute() {
                currentLocationImage = BitmapFactory.decodeResource(getResources(), R.drawable.placeholderimage);
                locationImage.setImageBitmap(currentLocationImage);
                Log.v("I hate the world", "I am trying to work on photos");
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    Log.v("I hate the world", "This sucks");
                    locationImage.setImageBitmap(attributedPhoto.bitmap);
                }
            }
        }.execute(input);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("I hate the world","I failed");
    }

    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;
        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

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

    public void getWebResult() {
        String encodedInput = null;
        try {
            encodedInput = URLEncoder.encode(currentLocation, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_MESSAGE, "Encoding exception");
            e.printStackTrace();
        }
        if (encodedInput != null) {
            Log.v("I hate the world", "I am trying to work on wiki");
            apiUrl = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + encodedInput;
            new CallAPI().execute(apiUrl);
        }
    }
    private class CallAPI extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0]; // URL to call

            HttpURLConnection urlConnection = null;

            InputStream in = null;
            StringBuilder sb = new StringBuilder();

            char[] buf = new char[4096];

            // do the HTTP Get
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());

                Log.i(LOG_MESSAGE, "got input stream");

                int read;
                while ((read = reader.read(buf)) != -1) {
                    sb.append(buf, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    // releases any system resources associated with the stream
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    Log.i(LOG_MESSAGE + " Error:", e.getMessage());
                }
            }
            Log.i(LOG_MESSAGE, "Finished reading");
            return sb.toString();
        }


        protected void onPostExecute(String result) {
            Log.i(LOG_MESSAGE, "starting onPostExecute");

            JSONObject foodEntries;
            JSONObject page;
            JSONObject pageFromId;
            String description = "Our description for " + currentLocation + " is not available yet please use the google button below to learn more";;

            // separate this out so people can work on it.
            try {
                JSONObject jObject = new JSONObject(result);
                foodEntries = jObject.getJSONObject("query");
                page = foodEntries.getJSONObject("pages");
                pageFromId = page.getJSONObject((String) page.names().get(0));
                description = pageFromId.getString("extract");

            } catch (JSONException e) {
                Log.e(LOG_MESSAGE, "Could not do JSON result");
                Log.i(LOG_MESSAGE, e.getMessage());
            }

            showFoodEntries1(description);
        }

    }

    private void showFoodEntries1(String description) {
        TextView tv = (TextView) findViewById(R.id.locationDescription);
        tv.setText(description);
    }
}
