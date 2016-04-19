package edu.mit.outnabout;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v4.app.NotificationCompat;

public class HomeActivity extends AppCompatActivity {

    String hardcodedTitle = "New Location Found!";
    String hardcodedContent = "The Ray & Maria Stata Center\n\n1/10 MIT locations discovered";


    public void moveToMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void notify(View view) {

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.stata);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(hardcodedTitle)
                        .setContentText(hardcodedContent)
                        .setLargeIcon(bmp)
                        .setPriority(1) // High Priority: should enable heads-up notification
                        .setColor(Color.argb(0,50, 200, 200))
                        .extend(new NotificationCompat.WearableExtender().setBackground(bmp));

        // Set an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
