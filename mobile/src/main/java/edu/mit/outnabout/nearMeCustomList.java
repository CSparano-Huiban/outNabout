package edu.mit.outnabout;

/**
 * Created by csparano on 4/27/16.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.List;

public class nearMeCustomList extends ArrayAdapter<String> implements GoogleApiClient.OnConnectionFailedListener{
    private final FragmentActivity context;
    private final List<String> locationNames;
    private final List<String> addresses;
    private final List<String> placeIds;
    private GoogleApiClient mGoogleApiClient;

    public nearMeCustomList(FragmentActivity context, List<String> locationNames, List<String> addresses, List<String> placeIds, GoogleApiClient mGoogleApiClient) {
        super(context, R.layout.near_me_cell, locationNames);
        this.context = context;
        this.locationNames = locationNames;
        this.addresses = addresses;
        this.placeIds = placeIds;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.near_me_cell, null, true);
        TextView locationTitle = (TextView) rowView.findViewById(R.id.near_me_location);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.near_me_image);

        placePhotosTask(placeIds.get(position), imageView);

        TextView addressView = (TextView) rowView.findViewById(R.id.near_me_address);

        locationTitle.setText(locationNames.get(position));

        addressView.setText(addresses.get(position));

        return rowView;
    }

    private void placePhotosTask(String input, final ImageView cellImageView ) {

        new PhotoTask(100, 100) {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    cellImageView.setImageBitmap(attributedPhoto.bitmap);
                }
            }
        }.execute(input);
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
