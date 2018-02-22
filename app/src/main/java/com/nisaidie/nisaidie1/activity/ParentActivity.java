package com.nisaidie.nisaidie1.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.chat.Chat;
import com.nisaidie.nisaidie1.fragment.AddFragment;
import com.nisaidie.nisaidie1.fragment.HomeFragment;
import com.nisaidie.nisaidie1.listeners.KeyLauncher;

import static com.nisaidie.nisaidie1.R.id.action_home;

public class ParentActivity extends AppCompatActivity{

    private static final int PRESS_INTERVAL = 700;
    private long mUpKeyEventTime = 0;
    private Window window;

    @Override
    protected void onResume() {
        super.onResume();

        window = this.getWindow();

        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(!KeyLauncher.ON_SCREEN){

        } else{

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        setupNavigationView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        window = this.getWindow();

        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        //register the receiver here
        IntentFilter myFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        myFilter.addAction(Intent.ACTION_SCREEN_ON);
        BroadcastReceiver mReceiver = new KeyLauncher();
        registerReceiver(mReceiver, myFilter);

    }



    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    /**
     * Perform action when any item is selected.
     *
     * @param item Item that is selected.
     */
    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case action_home:
                // Action to perform when Home Menu item is selected.
                pushFragment(new HomeFragment());

                break;
            case R.id.action_chat:
                // Action to perform when chat Menu item is selected.
                //pushFragment(new Chat());

                Intent intent = new Intent(this, Chat.class);
                startActivity(intent);

                break;

            case R.id.action_bag:
                // Action to perform when Bag Menu item is selected.
                pushFragment(new AddFragment());
                break;
           /* case R.id.action_account:
                // Action to perform when Account Menu item is selected.
                pushFragment(new AccountFragment());
                break;*/
        }
    }

    /**
     * Method to push any fragment into given id.
     *
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }
    }

    //declare on pause and on resume methods


    @Override
    protected void onPause() {
        //when screen is about to turn off
        if (KeyLauncher.ON_SCREEN) {

        } else {

        }
        super.onPause();
    }


}
