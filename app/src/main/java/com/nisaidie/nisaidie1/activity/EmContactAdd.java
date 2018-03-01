package com.nisaidie.nisaidie1.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.helper.EmContactsDetails;
import com.nisaidie.nisaidie1.helper.EmDetailsThree;
import com.nisaidie.nisaidie1.helper.EmDetailsTwo;
import com.nisaidie.nisaidie1.session.EmPrefManager;

public class EmContactAdd extends AppCompatActivity implements TextWatcher {

    int emCount = 0;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    private Button btnNext, btnBack, btnView;
    private EmPrefManager prefManager;
    private EditText first_name, last_name, prim_phone, other_phone, relationship, addresss;
    private EditText first_name2, last_name2, prim_phone2, other_phone2, relationship2, addresss2;
    private EditText first_name3, last_name3, prim_phone3, other_phone3, relationship3, addresss3;
    private String first_name_holder, last_name_holder, prim_phone_holder, other_phone_holder, relationship_holder, address_holder; //hold variables from ET
    private String first_name_holder2, last_name_holder2, prim_phone_holder2, other_phone_holder2, relationship_holder2, address_holder2; //hold variables from ET
    private String first_name_holder3, last_name_holder3, prim_phone_holder3, other_phone_holder3, relationship_holder3, address_holder3; //hold variables from ET

    private Firebase myFirebase;
    private static final String FIREBASE_SERVER_URL = "https://nisaidieapp-73ade.firebaseio.com/";
    DatabaseReference myDatabaseReference;
    // Root Database Name for Firebase Database.
    public static final String Database_Path = "Em_Contact_Details_Database";

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check is it is the first time to launch this activity
        prefManager = new EmPrefManager(this);
        if(!prefManager.isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }
        //let us check if user has em contacts already

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        Firebase.setAndroidContext(EmContactAdd.this);
        myFirebase = new Firebase(FIREBASE_SERVER_URL);

        setContentView(R.layout.activity_emergency_contacts);
        viewPager = (ViewPager)findViewById(R.id.em_view_pager);
        btnNext = (Button) findViewById(R.id.em_btn_next);
        btnBack = (Button) findViewById(R.id.em_btn_back);
        btnView = (Button) findViewById(R.id.em_btn_view);

        //layouts for the three emergency contacts
        layouts = new int[]{
                R.layout.activity_em_contact_add,
                R.layout.em_add_two,
                R.layout.em_add_three
        };

        //make the notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking for last page, if solaunch home
                int current = getItem(+1);
                if(current < layouts.length){
                    //move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                myDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path).child(uid);

                EmContactsDetails emContactsDetails = new EmContactsDetails();
                EmDetailsTwo emDetailsTwo = new EmDetailsTwo();
                EmDetailsThree emDetailsThree = new EmDetailsThree();

                if (current == 1) { //first layout

                    GetDataFromEditText();

                    //add fields into class function objects
                    emContactsDetails.setfName(first_name_holder);
                    emContactsDetails.setlName(last_name_holder);
                    emContactsDetails.setPrimaryPhone(prim_phone_holder);
                    emContactsDetails.setOtherPhone(other_phone_holder);
                    emContactsDetails.setAddress(address_holder);
                    emContactsDetails.setRship(relationship_holder);

                    DatabaseReference pushRefOne = myDatabaseReference.push();
                    String pushIdOne = pushRefOne.getKey();
                    emContactsDetails.setPushId(pushIdOne);
                    pushRefOne.setValue(emContactsDetails);



                } else if (current == 2) { //secondone
                    //second record

                    GetDataFromEditText();

                    //add fields into class function objects
                    emDetailsTwo.setfName(first_name_holder2);
                    emDetailsTwo.setlName(last_name_holder2);
                    emDetailsTwo.setPrimaryPhone(prim_phone_holder2);
                    emDetailsTwo.setOtherPhone(other_phone_holder2);
                    emDetailsTwo.setAddress(address_holder2);
                    emDetailsTwo.setRship(relationship_holder2);

                    DatabaseReference pushRefTwo = myDatabaseReference.push();
                    String pushIdTwo = pushRefTwo.getKey();
                    emDetailsTwo.setPushId(pushIdTwo);
                    pushRefTwo.setValue(emDetailsTwo);



                } else if (current == 3) { //last one

                    //third record

                    GetDataFromEditText();

                    //add fields into class function objects
                    emDetailsThree.setfName(first_name_holder3);
                    emDetailsThree.setlName(last_name_holder3);
                    emDetailsThree.setPrimaryPhone(prim_phone_holder3);
                    emDetailsThree.setOtherPhone(other_phone_holder3);
                    emDetailsThree.setAddress(address_holder3);
                    emDetailsThree.setRship(relationship_holder3);

                    DatabaseReference pushRefThree = myDatabaseReference.push();
                    String pushIdThree = pushRefThree.getKey();
                    emDetailsThree.setPushId(pushIdThree);
                    pushRefThree.setValue(emDetailsThree);

                   /* myDatabaseReference.addValueEventListener(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = (int) dataSnapshot.getChildrenCount();
                            emCount = count;
                            // TODO: show the count in the UI
                        }
                        public void onCancelled(DatabaseError databaseError) { }
                    });*/


                    // Showing Toast message after successfully data submit.
                    //Toast.makeText(EmContactAdd.this,"Data Inserted Successfully into Nisaidie Database", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EmContactAdd.this, ViewEmergencyContacts.class);
                startActivity(intent);
            }
        });

    }

    public void Count() {
        myDatabaseReference.child("a").child("b").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    emCount = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public boolean validate(TextView textView, String text) {

        if(TextUtils.isEmpty(textView.getText().toString())){
            textView.setError(textView.getHint()+" is require!");
            return false;
        } else {
            textView.setError(null);
            return false;
        }
    }

    //get data fromedit text fields
    public void GetDataFromEditText() {
        first_name_holder = first_name.getText().toString().trim();
        validate((TextView)first_name,first_name_holder);
        last_name_holder = last_name.getText().toString().trim();
        prim_phone_holder = prim_phone.getText().toString().trim();
        other_phone_holder = other_phone.getText().toString().trim();
        address_holder = addresss.getText().toString().trim();
        relationship_holder = relationship.getText().toString().trim();

        first_name_holder2 = first_name2.getText().toString().trim();
        validate((TextView)first_name2,first_name_holder2);
        last_name_holder2 = last_name2.getText().toString().trim();
        prim_phone_holder2 = prim_phone2.getText().toString().trim();
        other_phone_holder2 = other_phone2.getText().toString().trim();
        address_holder2 = addresss2.getText().toString().trim();
        relationship_holder2 = relationship2.getText().toString().trim();

        first_name_holder3 = first_name3.getText().toString().trim();
        validate((TextView)first_name3,first_name_holder3);
        last_name_holder3 = last_name3.getText().toString().trim();
        prim_phone_holder3 = prim_phone3.getText().toString().trim();
        other_phone_holder3 = other_phone3.getText().toString().trim();
        address_holder3 = addresss3.getText().toString().trim();
        relationship_holder3 = relationship3.getText().toString().trim();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position == layouts.length - 3) {
                btnBack.setVisibility(View.GONE);
            }

            // changing the next button text 'NEXT' / 'GOT IT'
            else if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnBack.setVisibility(View.VISIBLE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(EmContactAdd.this, ParentActivity.class));
        finish();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

                first_name = (EditText) findViewById(R.id.emFName_one);
                last_name = (EditText) findViewById(R.id.emLName_one);
                prim_phone = (EditText) findViewById(R.id.em_phone_one);
                other_phone = (EditText) findViewById(R.id.em_other_one);
                addresss = (EditText) findViewById(R.id.em_address_one);
                relationship = (EditText) findViewById(R.id.em_rel_one);

                first_name2 = (EditText) findViewById(R.id.emFName_two);
                last_name2 = (EditText) findViewById(R.id.emLName_two);
                prim_phone2 = (EditText) findViewById(R.id.em_phone_two);
                other_phone2 = (EditText) findViewById(R.id.em_other_two);
                addresss2 = (EditText) findViewById(R.id.em_address_two);
                relationship2 = (EditText) findViewById(R.id.em_rel_two);

                first_name3 = (EditText) findViewById(R.id.emFName_three);
                last_name3 = (EditText) findViewById(R.id.emLName_three);
                prim_phone3 = (EditText) findViewById(R.id.em_phone_three);
                other_phone3 = (EditText) findViewById(R.id.em_other_three);
                addresss3 = (EditText) findViewById(R.id.em_address_three);
                relationship3 = (EditText) findViewById(R.id.em_rel_three);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
