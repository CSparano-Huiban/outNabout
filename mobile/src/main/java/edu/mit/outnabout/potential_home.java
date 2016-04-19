package edu.mit.outnabout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class potential_home extends AppCompatActivity {

    List<String> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potential_home);
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

    public void searchNearMeSetUp() {
        final potential_home tempThis = this;

        ListView theListView = (ListView) findViewById(R.id.listView);

        List<String> places = new ArrayList<String>();

        messageList = new ArrayList<String>();

        ArrayAdapter<String> resultsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,places);

        String placeName;
        String placeId;

        if (results != null) {

            for (int i = 0; i < Math.min(results.length(), 5); i++) {
                try {
                    JSONObject currentPlace = (JSONObject) results.get(i);
                    JSONObject tempJsonParse = currentPlace.getJSONObject("fields");
                    placeName = currentPlace.name;
                    placeId = currentPlace.placeId;
                    places.add(placeName);
                    messageList.add(placeId);

                } catch (JSONException e) {
                    Log.e("Json Error", e.getMessage());
                    e.printStackTrace();
                }

            }  // end the for loop
            theListView.setAdapter(resultsAdapter);
        }

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(tempThis, WebViewers.class);
                String currPlaceId = messageList.get(position);
                intent.putExtra("place_id", currPlaceId);
                startActivity(intent);
            }
        });
    }
}
