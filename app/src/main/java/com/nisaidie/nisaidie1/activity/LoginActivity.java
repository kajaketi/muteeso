package com.nisaidie.nisaidie1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.app.AppController;
import com.nisaidie.nisaidie1.app.AppURLs;
import com.nisaidie.nisaidie1.session.Session;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText phoneNumber;
    Button loginn;
    Session session;

    //variables for the shared preferences
    public static final String MyPrefs = "MyPrefs" ;
    public static final String Phonee = "phoneKey";

    SharedPreferences loginpreferences;

private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Check if user is logged in
        session = new Session(LoginActivity.this);

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
                loginpreferences.edit().putString(Phonee, phone).commit();
            }
        });

        //Read previously saved   userName from sharedPreferences  and populate in the editText
        loginpreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        phoneNumber.setText(loginpreferences.getString(Phonee, ""));
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
        progressDialog.setMessage("loging in");
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
            progressDialog.show();
    }

    private void hideDialog(){
        if(!progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
