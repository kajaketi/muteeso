package com.nisaidie.nisaidie1.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.session.EmPrefManager;

public class EmContactAdd extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    private Button btnNext, btnBack, btnView;
    private EmPrefManager prefManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check is it is the first time to launch this activity
        prefManager = new EmPrefManager(this);
        if(!prefManager.isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

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

            }
        });

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
        startActivity(new Intent(EmContactAdd.this, GoogleAuth.class));
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
