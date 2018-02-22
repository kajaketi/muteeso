package com.nisaidie.nisaidie1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.app.AppController;
import com.nisaidie.nisaidie1.app.AppURLs;
import com.nisaidie.nisaidie1.session.Session;
import com.nisaidie.nisaidie1.util.SharedPrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    Context mContext = this;

    EditText phoneNumber;
    Button loginn;
    Session session;

    //variables for the google account show
    private TextView mFullNameTextView, mEmailTextView;
    private CircleImageView mProfileImageView;
    private String mUsername, mEmail;
    public SharedPrefManager sharedPrefManager;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;


    //variables for the shared preferences
    public static final String MyPrefs = "MyPrefs" ;
    public static final String Phonee = "phoneKey";

    public static SharedPreferences loginpreferences;

private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Read previously saved   userName from sharedPreferences  and populate in the editText
        loginpreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        String value = loginpreferences.getString(Phonee, null);

        if (value == null) {
            // the key does not exist
            setContentView(R.layout.activity_login);
            //Check if user is logged in
            session = new Session(LoginActivity.this);

//start of profile pic and name show
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            View header = navigationView.getHeaderView(0);

            mFullNameTextView = (TextView) header.findViewById(R.id.fullName);
            mEmailTextView = (TextView) header.findViewById(R.id.email);
            mProfileImageView = (CircleImageView) header.findViewById(R.id.profileImage);


            // create an object of sharedPreferenceManager and get stored user data
            sharedPrefManager = new SharedPrefManager(mContext);
            mUsername = sharedPrefManager.getName();
            mEmail = sharedPrefManager.getUserEmail();
            String uri = sharedPrefManager.getPhoto();
            Uri mPhotoUri = Uri.parse(uri);

            //Set data gotten from SharedPreference to the Navigation Header view
            mFullNameTextView.setText(mUsername);
            mEmailTextView.setText(mEmail);

            Picasso.with(mContext)
                    .load(mPhotoUri)
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(android.R.drawable.sym_def_app_icon)
                    .into(mProfileImageView);

            configureSignIn();
            //end of profile pic and name show

            phoneNumber = (EditText) findViewById(R.id.input_phone);
            loginn = (Button) findViewById(R.id.btn_login);

            loginn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone=phoneNumber.getText().toString();

                    //Validate form
                    if(phone.trim().length()>=10){
                        Intent intent = new Intent(LoginActivity.this,ParentActivity.class);
                        startActivity(intent);

                    }else {
                        Snackbar.make(v,"Please enter your phone number",Snackbar.LENGTH_LONG).show();
                    }
                    //save prefs
                    loginpreferences.edit().putString(Phonee, phone).apply();
                }
            });

        } else {
            // handle the value

            Intent intent = new Intent(LoginActivity.this,ParentActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Saving Activity State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Read previously saved   userName & password from sharedPreferences
            loginpreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
            phoneNumber.setText(loginpreferences.getString(Phonee, ""));

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Read previously saved   userName & password from sharedPreferences
            loginpreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
            phoneNumber.setText(loginpreferences.getString(Phonee, ""));

        }
    }


    private void login(final String phone){
        String tag_string_reg="reg_login";
        progressDialog.setMessage("logging in");
        showDialog();
        StringRequest strReg=new StringRequest(Request.Method.POST, AppURLs.LOGIN, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String userid = jsonObject.getString("userid");
                    if(userid != null){
                        //save login details
                        session.setLogin(true);
                        Intent intent = new Intent(LoginActivity.this,ParentActivity.class);
                        startActivity(intent);
                    }else{
                        //displaying login error
                        String errorMessage = jsonObject.getString("message");
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("tag","login");
                params.put("phonenumber",phone);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReg, tag_string_reg);
    }
    private void showDialog(){
        if(!progressDialog.isShowing())
          //  progressDialog.setMessage(getString(R.string.loading));
         //   progressDialog.setIndeterminate(true);
            progressDialog.show();

    }

    private void hideDialog(){
        if(!progressDialog.isShowing())
            progressDialog.dismiss();
    }


    // This method configures Google SignIn
    public void configureSignIn(){
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }


}
