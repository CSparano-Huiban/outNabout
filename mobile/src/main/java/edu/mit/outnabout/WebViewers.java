package edu.mit.outnabout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WebViewers extends AppCompatActivity {

    String currentLatitude = "42.359189";
    String currentLongitude = "-71.093635";
    String currentLocation = "Massachusetts Institute of Technology";
    String currentDescription = "The Massachusetts Institute of Technology (MIT) is a private research university in Cambridge, Massachusetts. Founded in 1861 in response to the increasing industrialization of the United States, MIT adopted a European polytechnic university model and stressed laboratory instruction in applied science and engineering. Researchers worked on computers, radar, and inertial guidance during World War II and the Cold War. Post-war defense research contributed to the rapid expansion of the faculty and campus under James Killian. The current 168-acre (68.0 ha) campus opened in 1916 and extends over 1 mile (1.6 km) along the northern bank of the Charles River basin.";
    TextView titleTextView;
    TextView descriptionTextView;
    ImageView locationImage;
    Bitmap currentLocationImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        titleTextView = (TextView) findViewById(R.id.locationTitle);
        descriptionTextView = (TextView) findViewById(R.id.locationDescription);
        locationImage = (ImageView) findViewById(R.id.locationImage);

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
}
