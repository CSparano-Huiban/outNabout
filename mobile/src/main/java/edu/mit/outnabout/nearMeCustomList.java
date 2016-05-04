package edu.mit.outnabout;

/**
 * Created by csparano on 4/27/16.
 */

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class nearMeCustomList extends ArrayAdapter<String>{
    private final FragmentActivity context;
    private final List<String> locationNames;
    private final List<String> addresses;
    String myTag = "Christopher Sparano";

    public nearMeCustomList(FragmentActivity context, List<String> locationNames, List<String> addresses) {
        super(context, R.layout.near_me_cell, locationNames);
        this.context = context;
        this.locationNames = locationNames;
        this.addresses = addresses;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.near_me_cell, null, true);

        TextView locationTitle = (TextView) rowView.findViewById(R.id.near_me_location);
        TextView addressView = (TextView) rowView.findViewById(R.id.near_me_address);

        locationTitle.setText(locationNames.get(position));
        addressView.setText(addresses.get(position));

        return rowView;
    }
}
