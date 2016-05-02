package edu.mit.outnabout;

/**
 * Created by csparano on 4/27/16.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.List;

public class nearMeCustomList extends ArrayAdapter<String> implements GoogleApiClient.OnConnectionFailedListener{
    private final FragmentActivity context;
    private final List<String> locationNames;
    private final List<String> addresses;
    private final List<String> placeIds;
    private GoogleApiClient mGoogleApiClient;
    String myTag = "Christopher Sparano";
    private boolean[] positionSetUp;
//    private List<Bitmap> photosByPosition;
//    private List<ImageView> imageViewByPosition;

    public nearMeCustomList(FragmentActivity context, List<String> locationNames, List<String> addresses, List<String> placeIds, GoogleApiClient mGoogleApiClient) {
        super(context, R.layout.near_me_cell, locationNames);
        this.context = context;
        this.locationNames = locationNames;
        this.addresses = addresses;
        this.placeIds = placeIds;
        this.mGoogleApiClient = mGoogleApiClient;
        this.positionSetUp = new boolean[5];
//        this.photosByPosition = new ArrayList<>();
//        this.imageViewByPosition = new ArrayList<>();
//        for(String name : locationNames){
//            Log.e(myTag, name);
//            photosByPosition.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholderimage));
//            imageViewByPosition.add(null);
//        }

    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.near_me_cell, null, true);
        TextView locationTitle = (TextView) rowView.findViewById(R.id.near_me_location);

//        ImageView imageView = (ImageView) rowView.findViewById(R.id.near_me_image);
//        imageViewByPosition.set(position, imageView);

        Log.e(myTag, String.valueOf(position) + " " + locationNames.get(position));

//        if(!positionSetUp[position]){
////            imageViewByPosition.set(position, imageView);
//            placePhotosTask(placeIds.get(position),position, imageView);
//
//            positionSetUp[position] = true;
//        }else{
////            imageViewByPosition.set(position, imageView);
//            Log.e(myTag, position + imageViewByPosition.get(position).toString());
//            imageViewByPosition.get(position).setImageBitmap(photosByPosition.get(position));
//        }


        TextView addressView = (TextView) rowView.findViewById(R.id.near_me_address);

        locationTitle.setText(locationNames.get(position));

        addressView.setText(addresses.get(position));

        return rowView;
    }

//    private void placePhotosTask(String input, final int position, final ImageView cellImageView ) {
////        Log.e(myTag, input);
//        new PhotoTask(100, 100) {
//            @Override
//            protected void onPreExecute() {
//                Bitmap currBitmap = photosByPosition.get(position);
//                imageViewByPosition.get(position).setImageBitmap(currBitmap);
//            }
//
//            @Override
//            protected void onPostExecute(AttributedPhoto attributedPhoto) {
//                if (attributedPhoto != null) {
//                    Log.e(myTag, "set the photo");
//                    photosByPosition.set(position, attributedPhoto.bitmap);
//                    Log.e(myTag, imageViewByPosition.get(position).toString());
//                    imageViewByPosition.get(position).setImageBitmap(attributedPhoto.bitmap);
//                    imageViewByPosition.get(position).postInvalidate();
//                    Log.e(myTag, String.valueOf(position));
//                }
//            }
//        }.execute(input);
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
//
//    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {
//        private int mHeight;
//        private int mWidth;
//
//        public PhotoTask(int width, int height) {
//            mHeight = height;
//            mWidth = width;
//        }
//
//        @Override
//        protected AttributedPhoto doInBackground(String... params) {
//            String myTag = "Christopher Sparano";
//            for(String currParam : params){
//                Log.e(myTag,"param:  " + currParam);
//            }
//            if (params.length != 1) {
////                Log.e(myTag,"nothing in params");
//                return null;
//            }
//            final String placeId = params[0];
//            AttributedPhoto attributedPhoto = null;
//
//            PlacePhotoMetadataResult result = Places.GeoDataApi
//                    .getPlacePhotos(mGoogleApiClient, placeId).await();
////            Log.e(myTag,result.toString());
//
//            if (result.getStatus().isSuccess()) {
//                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
//                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
//                    // Get the first bitmap and its attributions.
//                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0).freeze();
//                    photoMetadataBuffer.release();
//                    CharSequence attribution = photo.getAttributions();
//                    // Load a scaled bitmap for this photo.
//                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
//                            .getBitmap();
//
//                    attributedPhoto = new AttributedPhoto(attribution, image);
//                }else{
//                    photoMetadataBuffer.release();
//                }
//                // Release the PlacePhotoMetadataBuffer.
//            }
//            if(result.getPhotoMetadata() != null){
////                Log.e(myTag,"I hate this shit");
//                result.getPhotoMetadata().release();
//            }
//
//            return attributedPhoto;
//        }
//
//        /**
//         * Holder for an image and its attribution.
//         */
//        class AttributedPhoto {
//
//            public final CharSequence attribution;
//
//            public final Bitmap bitmap;
//
//            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
//                this.attribution = attribution;
//                this.bitmap = bitmap;
//            }
//        }
//    }

}
