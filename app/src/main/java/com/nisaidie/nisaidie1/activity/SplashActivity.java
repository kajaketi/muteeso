package com.nisaidie.nisaidie1.activity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.fragment.LocationFragment;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    LocationFragment lFrag;
    LocationManager locationManager;
    Location location;
    public double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //gps
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();


        lFrag = LocationFragment.newInstance();

        //getSupportFragmentManager().beginTransaction().add(R.layout.activity_splash, lFrag).commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        }, 10000);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

