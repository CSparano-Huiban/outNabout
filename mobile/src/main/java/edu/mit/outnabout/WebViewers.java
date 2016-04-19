package edu.mit.outnabout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class WebViewers extends AppCompatActivity {

    String currentLatitude = "42.359189";
    String currentLongitude = "-71.093635";
    String currentLocation = "Massachusetts Institute of Technology";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
