package edu.mit.outnabout;

/**
 * Created by csparano on 4/26/16.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AcheveCustomList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] locationName;
    private final Integer[] imageId;
    private final String[] historic;
    private final String[] modern;
    private final String[] total;

    public AcheveCustomList(Activity context, String[] locationName, Integer[] imageId, String[] historic, String[] modern, String[] total) {
        super(context, R.layout.acheve_cell, locationName);
        this.context = context;
        this.locationName = locationName;
        this.imageId = imageId;
        this.historic = historic;
        this.modern = modern;
        this.total = total;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.acheve_cell, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.acheveLocation);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.acheveImage);

        TextView historicView = (TextView) rowView.findViewById(R.id.historicSeen);
        TextView modernView = (TextView) rowView.findViewById(R.id.modernSeen);
        TextView totalView = (TextView) rowView.findViewById(R.id.totalSeen);

        txtTitle.setText(locationName[position]);

        imageView.setImageResource(imageId[position]);
        historicView.setText(historic[position]);
        modernView.setText(modern[position]);
        totalView.setText(total[position]);

        return rowView;
    }
}
