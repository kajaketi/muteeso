package com.nisaidie.nisaidie1.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.adapters.EmContactViewAdapter;
import com.nisaidie.nisaidie1.helper.EmContactsDetails;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ViewEmergencyContacts extends AppCompatActivity {

    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    List<EmContactsDetails> list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_emergency_contacts);
        ButterKnife.bind(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        recyclerView = (RecyclerView) findViewById(R.id.emcontactslistrecyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(ViewEmergencyContacts.this));

        progressDialog = new ProgressDialog(ViewEmergencyContacts.this);

        progressDialog.setMessage("Loading Data from Database");

        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference(EmContactAdd.Database_Path).child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    EmContactsDetails emContactsDetails = dataSnapshot.getValue(EmContactsDetails.class);

                    list.add(emContactsDetails);
                }

                adapter = new EmContactViewAdapter(ViewEmergencyContacts.this, list);

                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();

            }
        });
    }
}
