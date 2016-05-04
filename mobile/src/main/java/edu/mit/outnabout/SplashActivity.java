package edu.mit.outnabout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread timer=new Thread()
        {
            public void run() {
                try {
                    sleep(5000); // show splash screen for at least 5 seconds. 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {
                    Intent i=new Intent(SplashActivity.this, HomeActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        };
        timer.start();
    }
}
