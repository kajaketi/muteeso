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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    Context mContext = this;

    private EditText phoneNumber;
    private EditText mVerificationField;
    private Button mSubmitNumber;
    Session session;
    private String phoneLogin;

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

    //phone auth
    private static final String TAG = "PhoneAuthActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ViewGroup mPhoneNumberView;
    private ViewGroup mVerificationViews;
    private TextView mDetailText;
    private Button mVerifyButton;
    private Button mResendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Read previously saved   userName from sharedPreferences  and populate in the editText
        loginpreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

       // String value = loginpreferences.getString(Phonee, null);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

            // the key does not exist
            setContentView(R.layout.activity_login);
            //Check if user is logged in
            //session = new Session(LoginActivity.this);

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

            // Assign views
            mPhoneNumberView = (ViewGroup) findViewById(R.id.submitting_phone_number);
            mVerificationViews = (ViewGroup) findViewById(R.id.verification_field_and_buttons);

            mDetailText = (TextView) findViewById(R.id.details_text_field);
            phoneNumber = (EditText) findViewById(R.id.input_phone);
            mVerificationField = (EditText) findViewById(R.id.field_verification_code);
            mVerifyButton = (Button) findViewById(R.id.button_verify_phone_number);
            mResendButton = (Button) findViewById(R.id.button_resend_code);
            mSubmitNumber = (Button) findViewById(R.id.btn_login);
            // Assign click listeners
            mSubmitNumber.setOnClickListener(this);
            mVerifyButton.setOnClickListener(this);
            mResendButton.setOnClickListener(this);

            // [START initialize_auth]
            mAuth = FirebaseAuth.getInstance();
            // [END initialize_auth]

            // Initialize phone auth callbacks
            // [START phone_auth_callbacks]
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verificaiton without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                    // [START_EXCLUDE silent]
                    mVerificationInProgress = false;
                    // [END_EXCLUDE]

                    // [START_EXCLUDE silent]
                    // Update the UI and attempt sign in with the phone credential
                    updateUI(STATE_VERIFY_SUCCESS, credential);
                    // [END_EXCLUDE]
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e);
                    // [START_EXCLUDE silent]
                    mVerificationInProgress = false;
                    // [END_EXCLUDE]

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // [START_EXCLUDE]
                        phoneNumber.setError("Invalid phone number.");
                        // [END_EXCLUDE]
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // [START_EXCLUDE]
                        Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                                Snackbar.LENGTH_SHORT).show();
                        // [END_EXCLUDE]
                    }

                    // Show a message and update the UI
                    // [START_EXCLUDE]
                    updateUI(STATE_VERIFY_FAILED);
                    // [END_EXCLUDE]
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:" + verificationId);

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;

                    // [START_EXCLUDE]
                    // Update UI
                    updateUI(STATE_CODE_SENT);
                    // [END_EXCLUDE]
                }
            };
            // [END phone_auth_callbacks]

           /* mSubmitNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone=phoneNumber.getText().toString();
                    //Validate form
                    if(phone.trim().length()>=10){
                        Intent intent = new Intent(LoginActivity.this,EmContactAdd.class);
                        startActivity(intent);
                    }else {
                        Snackbar.make(v,"Please enter your phone number",Snackbar.LENGTH_LONG).show();
                    }
                    //save prefs
                    loginpreferences.edit().putString(Phonee, phone).apply();
                }
            });
*/
       /*  else {
            // handle the value

            Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
            startActivity(intent);
        }*/

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]
    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                            Intent intent = new Intent(LoginActivity.this,EmContactAdd.class);
                            startActivity(intent);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]
    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mSubmitNumber, phoneNumber);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(mVerificationViews);
                disableViews(mPhoneNumberView);
                mDetailText.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show options to input number
                enableViews(mSubmitNumber, phoneNumber);
                disableViews(mVerificationViews);
                mDetailText.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mSubmitNumber, phoneNumber, mVerificationViews);
                mDetailText.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
               /* if (cred != null) {
                   *//* if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                    }*//*
                   phoneLogin = phoneNumber.getText().toString();
                   login(phoneLogin);
                }*/

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

    }

 /*   private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
    }*/

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
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

/*

    private void login(final String phone){
        String tag_string_reg="reg_login";
       // progressDialog.setMessage("logging in");
        showProgressDialog();
//        session.setLogin(true);
        Intent intent = new Intent(LoginActivity.this,ParentActivity.class);
        startActivity(intent);
        hideProgressDialog();
        */
/*
                this can be adpted for a log in logic, server if one is in place
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
                hideProgressDialog();
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("tag","login");
                params.put("phonenumber",phone);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReg, tag_string_reg);*//*

    }
*/

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_login:
                String phone=phoneNumber.getText().toString();
                if (phone.trim().length()>=10) {
                    startPhoneNumberVerification(phoneNumber.getText().toString());
                    //save prefs
                    loginpreferences.edit().putString(Phonee, phone).apply();
                } else {
                    Snackbar.make(view,"Please enter your phone number",Snackbar.LENGTH_LONG).show();
                    return;
                }

                break;
            case R.id.button_verify_phone_number:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(phoneNumber.getText().toString(), mResendToken);
                break;
        }

    }
}
