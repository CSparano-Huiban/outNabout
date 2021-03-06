package edu.mit.outnabout;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.widget.ListView;

public class Achieve extends AppCompatActivity {
    ListView list;
    String[] locationName = {
            "Boston",
            "Chicago",
            "New York",
            "San Fransisco"
    } ;
    String[] historic = {
            "1/20 historic locations",
            "3/15 historic locations",
            "4/7 historic locations",
            "2/10 historic locations"
    } ;
    String[] modern = {
            "4/7 modern locations",
            "5/10 modern locations",
            "3/15 modern locations",
            "2/10 modern locations"
    } ;
    String[] total = {
            "6/50 total locations",
            "20/75 total locations",
            "30/80 total locations",
            "26/50 total locations"
    } ;
    Integer[] imageId = {
            R.drawable.boston,
            R.drawable.chicago,
            R.drawable.newyork,
            R.drawable.sanfran
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achieve);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AchieveCustomList adapter = new
                AchieveCustomList(Achieve.this, locationName, imageId, historic, modern, total);
        list = (ListView)findViewById(R.id.acheveList);
        if(list != null)
            list.setAdapter(adapter);
    }

}
