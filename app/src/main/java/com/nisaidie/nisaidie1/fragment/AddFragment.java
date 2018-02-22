package com.nisaidie.nisaidie1.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.databinding.EmergencyMainBinding;


public class AddFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "EmergencyActivity";
    private EmergencyMainBinding binding;

    @Nullable
    @Override
   // protected void onCreate(Bundle savedInstanceState){
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(getActivity(), R.layout.emergency_main);
        // /View view = inflater.inflate(R.layout.emergency_main, container, false);

        binding.emView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Fragment fragment = null;
                fragment = new AccountFragment();
               // FragmentManager manager = getSupportFragmentManager();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                //transaction.show(fragment);
                transaction.replace(R.id.emmain, fragment);
                transaction.commit();
                //startActivity(new Intent(getActivity(), com.nisaidie.nisaidie1.fragment.AccountFragment.class));
            }
        });


        binding.emAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), com.nisaidie.nisaidie1.activity.EmergencyContacts.class));
            }
        });
        return getView();
    }


    @Override
    public void onClick(View v) {

    }
}

