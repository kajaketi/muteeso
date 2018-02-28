package com.nisaidie.nisaidie1.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.activity.EmContactAdd;
import com.nisaidie.nisaidie1.activity.ViewEmergencyContacts;
import com.nisaidie.nisaidie1.adapters.Adapter;
import com.nisaidie.nisaidie1.adapters.EmContactViewAdapter;
import com.nisaidie.nisaidie1.helper.EmContactsDetails;
import com.nisaidie.nisaidie1.helper.EmDetailsThree;
import com.nisaidie.nisaidie1.helper.EmDetailsTwo;
import com.nisaidie.nisaidie1.services.GoogleService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by isaac on 1/4/2016.
 */
public class CategoryFragment extends DialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    //loading the phone numbers in a list
    List<EmContactsDetails> list = new ArrayList<>();
    List<EmDetailsTwo> listtwo = new ArrayList<>();
    List<EmDetailsThree> listthree = new ArrayList<>();
    String emContactOne, emContactTwo, emContactThree;
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
    public Double latitude, longitude;
    Geocoder geocoder;
    //end of trial location vars

    HomeFragment homefrag;
    private Location location;
    public String bestProvider;
    public double newlong2;
    public double newlat2;
    public static String Last_location;
    //these variables are for the sms functionality
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    ListView listView;
    String[] nisaidie_categories = {"Robbery", "Fire", "Accident"};
    int[] categoriesImages;
    Adapter adapter;
    ProgressBar bar;
    public static int next_database_to_call = 2;

    GoogleService gpsVar;

    String category_id;

    Button cancel;
    Context curContext;

    GoogleService gpsvar = new GoogleService();

    public static String MAIN_CATEGORY_SELECTED_ITEM = "main_category_selected_item";

    public static String MAIN_CATEGORY = "main_category";


    public void populateArrays(String[] categories, int[] categoriesImages) {

        this.categoriesImages = categoriesImages;

    }

    public static CategoryFragment newInstance(String[] categories) {
        CategoryFragment frag = new CategoryFragment();
        categories = categories;
        return frag;
    }


    public CategoryFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.category_dialog, container, false);

        listView = (ListView) rootView.findViewById(R.id.category_listview);
        bar = (ProgressBar) rootView.findViewById(R.id.empty_progress_bar);
        cancel = (Button) rootView.findViewById(R.id.cancel_button_main_category);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        curContext = getActivity().getApplicationContext();


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        new CategoryLoader().execute();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        checkLocation();

        //NOW WE WANT TO GET THE PRIMARY CONTACTS FROM FIREBASE

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(EmContactAdd.Database_Path).child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    EmContactsDetails emContactsDetails = dataSnapshot.getValue(EmContactsDetails.class);
                    list.add(emContactsDetails);
                    emContactOne = emContactsDetails.getPrimaryPhone();
                    emContactOne.toString();

                    EmDetailsTwo emDetailsTwo = dataSnapshot.getValue(EmDetailsTwo.class);
                    listtwo.add(emDetailsTwo);
                    emContactTwo = emDetailsTwo.getPrimaryPhone();
                    emContactTwo.toString();

                    EmDetailsThree emDetailsThree = dataSnapshot.getValue(EmDetailsThree.class);
                    listthree.add(emDetailsThree);
                    emContactThree = emDetailsThree.getPrimaryPhone();
                    emContactThree.toString();

                 //   EmContactViewAdapter watr = new EmContactViewAdapter(getContext(), list);
                    //think of referencing this here.
                //    watr.onBindViewHolder(watr);

                }

             //   adapter = new EmContactViewAdapter(ViewEmergencyContacts.this, list);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

        return rootView;
    }


    public void onStart() {

        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private class CategoryLoader extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            adapter = new Adapter(getActivity(), nisaidie_categories, categoriesImages);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            bar.setVisibility(View.GONE);
            setListView();
        }
    }

    private void setListView() {

        getDialog().setTitle("Select Categories");
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(), nisaidie_categories[i], Toast.LENGTH_SHORT).show();
                Log.i("selected Item", i + "");

                try {
                    sendsms(nisaidie_categories[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //chooseCategory(i);
                dismiss();
            }
        });
    }


    private void chooseCategory(int flag) {
        switch (flag) {
            case 0:
                sendSMSMessage();
                break;
            case 1:
                sendSMSMessage();
                break;
            case 2:
                sendSMSMessage();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //below is the send message method
    protected void sendSMSMessage() {
        //phonenoo = String.valueOf(phoneNo);
        // message = txtMessage.getText().toString();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+256793938467", null, "Victorial University", null, null);
                    Toast.makeText(getActivity(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    public void sendsms(String message) throws IOException{

        checkLocation();
        //onconnected
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

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {


            String errorMessage = "";
            String cityName = "";
            String stateName="";
            String countryName="";
            Double latitu = mLocation.getLatitude();
            Double longi = mLocation.getLongitude();


            List<Address> addresses = null;

            try {

                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                addresses = geocoder.getFromLocation(
                        latitu,
                        longi,
                        // In this sample, get just a single address.
                        1);
                cityName = addresses.get(0).getAddressLine(0);
                stateName = addresses.get(0).getAddressLine(1);
               countryName = addresses.get(0).getAddressLine(2);

                Toast.makeText(getActivity(), "the address is "+addresses, Toast.LENGTH_LONG).show();

            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_long_used);
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + mLocation.getLatitude() +
                        ", Longitude = " +
                        mLocation.getLongitude(), illegalArgumentException);
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(TAG, errorMessage);
                }
                //deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                Log.i(TAG, getString(R.string.address_found));
                /*deliverResultToReceiver(Constants.SUCCESS_RESULT,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments));*/
            }
          //  String city = mLocation.

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emContactOne, null, message+" At Lat. Cordinates"+latitu+" & Long cordinates"+longi+" on "+cityName+" in "+stateName+" city"+", "+countryName, null, null);
            smsManager.sendTextMessage(emContactTwo, null, message+" At Lat. Cordinates"+latitu+" & Long cordinates"+longi+" on "+cityName+" in "+stateName+" city"+", "+countryName, null, null);
            smsManager.sendTextMessage(emContactThree, null, message+" At Lat. Cordinates"+latitu+" & Long cordinates"+longi+" on "+cityName+" in "+stateName+" city"+", "+countryName, null, null);
            Toast.makeText(getActivity(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "xxxx Location not Detected", Toast.LENGTH_SHORT).show();
        }
        //end of on connected

        startLocationUpdates();



        }



    //gps methods again

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
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
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }

    /* ---- Preparing Intents To Check While Sms Sent & Delivered ---- */

}




