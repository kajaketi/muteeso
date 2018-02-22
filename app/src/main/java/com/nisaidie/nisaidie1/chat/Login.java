/*
package com.nisaidie.nisaidie1.chat;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.support.v4.app.Fragment;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.nisaidie.nisaidie1.R;

        import org.json.JSONException;
        import org.json.JSONObject;

public class Login extends Fragment {
    EditText username, password;
    Button loginButton;
    String user, pass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_login, container, false);


        //registerUser = (TextView)findViewById(R.id.register);
        username = (EditText)view.findViewById(R.id.username);
        password = (EditText)view.findViewById(R.id.password);
        loginButton = (Button)view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if(user.equals("")){
                    username.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else{
                    String url = "https://nisaidieapp-73ade.firebaseio.com";
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(getActivity(), "user not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(getActivity(), "user not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        startActivity(new Intent(getActivity(), Users.class));
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(getActivity());
                    rQueue.add(request);
                }

            }
        });

        return view;
    }
}*/
