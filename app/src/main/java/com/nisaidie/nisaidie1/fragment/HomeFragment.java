package com.nisaidie.nisaidie1.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.animation.RippleAnimation;
import com.nisaidie.nisaidie1.listeners.PictureCapturingListener;
import com.nisaidie.nisaidie1.services.APictureCapturingService;
import com.nisaidie.nisaidie1.services.GoogleService;
import com.nisaidie.nisaidie1.services.PictureCapturingServiceImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

//import android.location.LocationManager;

public class HomeFragment extends Fragment implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    //trial location vars
    private static final String TAG = "HomeFragment";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private Address address;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 20 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;
    public Double newlat, newlong;
    //end of trial location vars

    //<><><><><><><><><><><> take pics / rec videos vars <><><><><><><><>
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private ImageView uploadBackPhoto; //can be used if we want to display the pics here
    private ImageView uploadFrontPhoto; //can be used if we want to display the pics here

    //service
    private APictureCapturingService pictureService;

    //gps service
    private GoogleService gugu;

    //<><><><><><><><><><><><><><> end of take pics vars <><><><><><><><><><><><><><><><><><><>


    public static String message3;

    //audio recorder variables
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "nisaidie_audio";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    int recTimeOut = 15;
    int timer = 0;
    //end of audio recorder variables

    //ripple
    RippleAnimation rippleBackground1, rippleBackground2;
    ImageView btn;
    FragmentManager fragmentManager;

    CategoryFragment categoryFragment;

    //these variables are for the sms functionality
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 100;

    //gps vars
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    public Double latitude, longitude;
    Geocoder geocoder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        init(view);// to initialize widgets
        doRippleBackground(); //start ripple background work..
        btn = (ImageView) view.findViewById(R.id.panicbutton);

        checkPermissions(); //for taking pics

        //checking gps stuff
       /* Toast.makeText(getActivity(), "nLat is" + location.getLatitude(), Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(), "nLat is" + location.getLongitude(), Toast.LENGTH_LONG).show();*/
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        checkLocation(); //check whether location service is enable or not in your  phone
        //end of gps stuff


        //create a directory for audio files, creating Derectory on SD card
        File nfile = new File(Environment.getExternalStorageDirectory() + "/nisaidie");
        nfile.mkdir();

        //audio recoder
        random = new Random();

        //gps service
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        medit = mPref.edit();

        //<><><><><>getting instance of the Service from PictureCapturingServiceImpl <><><><><><>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pictureService = PictureCapturingServiceImpl.getInstance(getActivity());
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Camera functions wont work because you have a lower version phone OS", Toast.LENGTH_SHORT).show();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialogFragment(); //categories of crimes

                //<><><><><><><><><><><> Start pic capturing <><><><><><><><><><><><>
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    showToast("Starting pic capture!");
                    pictureService.startCapturing(HomeFragment.this);
                }

                //audio recorder activation

                if (checkPermission()) {
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nisaidie/" +
                            CreateRandomAudioFileName(5) + "nisaidieRec.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Toast.makeText(getActivity(), "Audio Recording started", Toast.LENGTH_LONG).show();

                } else {
                    requestPermission();
                }


                //this is to stop recording
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    Toast.makeText(getActivity(), "Audio recording stopped", Toast.LENGTH_LONG).show();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }


               /* try {
                    GmailSender sender = new GmailSender("nisaidieapp@gmail.com", "kajaketi12345");
                    sender.sendMail("This is Subject",
                            "This is Body",
                            "nisaidieapp@gmail.com",
                            "isikopeter@yahoo.co.uk");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }*/
            }
                //end of audio recorder activation
        });

        categoryFragment = new CategoryFragment();

        fragmentManager = getActivity().getSupportFragmentManager();

        return view;
    }




    //trying to activate wake lock feature for the app

    private void init(View view) {
        //root ripple background initialization
        rippleBackground1 = (RippleAnimation) view.findViewById(R.id.content);
        rippleBackground1.setVisibility(View.VISIBLE);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

   public void doRippleBackground() {

        startAnimation();

        //handler created to handle cardStack as well as timer...
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                callCardStack();
            }
        }, 500000);
    }


    //start the background ripple animation...
    private void startAnimation() {
        //if it's not running
        if (!rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.startRippleAnimation();//start root ripple animation
        }
    }

    //this method will stop background ripple animation. if it's running.
    private void stopAnimation() {
        if (rippleBackground1.isRippleAnimationRunning()) {
            rippleBackground1.stopRippleAnimation();
            // rippleBackground2.stopRippleAnimation();
        }
    }

    public void callCardStack() {
        rippleBackground1.setVisibility(View.GONE);

        stopAnimation();//start the ripple background animation.
    }

    private void callDialogFragment() {

        categoryFragment.show(fragmentManager, "Categories");
       // categoryFragment.show(fragmentManager, "Categories");

    }

    @Override
    public void onStart() {

        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


        if (boolean_permission) {

            if (mPref.getString("service", "").matches("")) {
                medit.putString("service", "service").commit();
                Intent intent = new Intent(getActivity().getApplicationContext(), GoogleService.class);
                getActivity().startService(intent);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
            }
        } else if (LocationManager.GPS_PROVIDER == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
        }
        fn_permission();
        sms_permission();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    // location permissions
    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;
        }
    }

    // sending sms permissions
    private void sms_permission() {
        if ((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS
                        },
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            boolean_permission = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //<><><><><><><><><><><><><><> video permissions <><><><><><><><><><><>
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }

        //audio permissions
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getActivity(), "Audio Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Audio Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }

        //SEND SMS PERMISSIONS
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please allow the SMS permission", Toast.LENGTH_LONG).show();

                }
            }
        }


        //GPS PERMISSIONS
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);


                // tv_area.setText(addresses.get(0).getAdminArea());
                Toast.makeText(getActivity(), "Your city is " + cityName, Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "Your state is " + stateName, Toast.LENGTH_LONG).show();
                //  tv_locality.setText(stateName);
                Toast.makeText(getActivity(), "Your country is " + countryName, Toast.LENGTH_LONG).show();
                //tv_address.setText(countryName);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Toast.makeText(getActivity(), "Your lat is " + latitude, Toast.LENGTH_LONG).show();
            // tv_latitude.setText(latitude+"");

            Toast.makeText(getActivity(), "Your long is " + longitude, Toast.LENGTH_LONG).show();
            // tv_longitude.setText(longitude+"");
            //  tv_address.getText();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
    }

/*

public Double dispLat(){
    //latituder = latitude;
    return latitude;
}

*/

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    //audio recorder methods
    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));
            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    //<<<<<<<<<<<<<<<<<<<< End of audio recording >>>>>>>>>>>>>>>>>>>>>>>>>//

//<><><><><><>><><><><><><><><><><><><><Video / picture take <><><><><><><><><><><><<><><><><><>

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {

        getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                                        }
                                    }
        );
    }

    /**
     * We've finished taking pictures from all phone's cameras
     */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("Done capturing all photos!");
            return;
        }
        showToast("No camera detected!");
    }

    /**
     * Displaying the pictures taken.
     */
    @Override
    public void onCaptureDone(final String pictureUrl, final byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                    final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                /*if (pictureUrl.contains("0_pic.jpg")) {
                    uploadBackPhoto.setImageBitmap(scaled);
                } else if (pictureUrl.contains("1_pic.jpg")) {
                    uploadFrontPhoto.setImageBitmap(scaled);
                }*/
                }

                ;
                // showToast("Picture saved to "+pictureUrl);
            });
        }
    }

        /*public boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED &&
                    result1 == PackageManager.PERMISSION_GRANTED;
        }*/

    /**
     * checking  permissions at Runtime.
     */
    //@TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] requiredPermissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(getActivity(), "nLocation not Detected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i(TAG, "nConnection failed. Error: " + connectionResult.getErrorCode());

    }
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, (LocationListener) this);
        Log.d("reque", "--->>>>");
    }


    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        newlat = location.getLatitude();
        newlong = location.getLongitude();
        Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gc.getFromLocation(newlat, newlong, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        if(addresses!=null)
        {
            if (addresses.size() > 0) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    sb.append(address.getAddressLine(i)).append("\n");
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
            }
                }



        Toast.makeText(getActivity(), "nLat is" + location.getLatitude(), Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(), "nLat is" + location.getLongitude(), Toast.LENGTH_LONG).show();

        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }


    /*
    *
    *  @Override
    public void onLocationChanged(Location location) {


        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gc.getFromLocation(latitude, longtitude, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        if(addresses!=null)
        {
        if (addresses.size() > 0) {
             address = addresses.get(0);
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                sb.append(address.getAddressLine(i)).append("\n");
            sb.append(address.getLocality()).append("\n");
            sb.append(address.getPostalCode()).append("\n");
            sb.append(address.getCountryName());
        }
        mdatabase.child(fireuser.getUid()).child("Last Known Location").setValue(sb.toString()+"Time:"+ df.format(date));
            markerString=sb.toString();
        curlocality=address.getLocality().toString();
        policenum=hm.get(curlocality);
        Last_location=sb.toString()+"\n"+"Last Active on: "+df.format(date);

    }}
    *
    *
    * */
}

