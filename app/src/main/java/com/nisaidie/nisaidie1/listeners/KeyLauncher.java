package com.nisaidie.nisaidie1.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.nisaidie.nisaidie1.activity.ParentActivity;

/**
 * Created by faren on 06/02/2018.
 */

public class KeyLauncher extends BroadcastReceiver {


    private Context cntxt;
    private Vibrator vibe;
    private long a, seconds_screenoff, seconds_screenon, OLD_TIME, diff, actual_diff;
    public static boolean OFF_SCREEN = false;
    public static boolean ON_SCREEN = false;
    private boolean Emergency = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        cntxt = context;
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        Log.v("onReceive", "Power button is pressed");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            a = System.currentTimeMillis();
            seconds_screenoff = a;
            OLD_TIME = seconds_screenoff;
            OFF_SCREEN = true;

            new CountDownTimer(5000,200) {

                public void onTick(long millisUntillFinished) {

                    if(ON_SCREEN) {
                        if (seconds_screenon !=0 && seconds_screenoff != 0) {

                            actual_diff = cal_diff(seconds_screenon, seconds_screenoff);
                            if(actual_diff <= 4000) {
                                //now lets put the logic here to launch
                                //for now lets just play with a boolean variable


                                //START INTENT
                                Intent i = new Intent();
                                i.setClass(context, ParentActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                                Emergency = true;
                                if(Emergency) {

                                    Toast.makeText(cntxt, "PWR BT PRSD 2X",Toast.LENGTH_SHORT).show();
                                    vibe.vibrate(100);
                                    seconds_screenon = 0;
                                    seconds_screenoff = 0;

                                }
                            } else {
                                seconds_screenon = 0;
                                seconds_screenoff = 0;
                            }

                        }
                    }
                }

                @Override
                public void onFinish() {

                    seconds_screenoff = 0;
                }
            }.start();

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            a = System.currentTimeMillis();
            seconds_screenon = a;
            OLD_TIME =seconds_screenon;

            new CountDownTimer(5000, 200) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if(OFF_SCREEN) {
                        if(seconds_screenon != 0 && seconds_screenoff != 0){
                            actual_diff = cal_diff(seconds_screenon, seconds_screenoff);
                            if (actual_diff <= 4000) {

                                //START INTENT
                                Intent i = new Intent();
                                i.setClass(context, ParentActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                                Emergency = true;
                                if (Emergency) {

                                    Toast.makeText(cntxt,"pwr btnklik 2x", Toast.LENGTH_SHORT).show();
                                    vibe.vibrate(100);
                                    seconds_screenon = 0;
                                    seconds_screenoff = 0;
                                }
                            } else {
                                seconds_screenon = 0;
                                seconds_screenoff = 0;
                            }
                        }
                    }
                }

                @Override
                public void onFinish() {

                    seconds_screenon = 0;
                }
            }.start();
        }

    }

    //diff btn time on and time off
    private long cal_diff(long seconds_screenon2, long seconds_screenoff2) {

        if (seconds_screenon2 >= seconds_screenoff2) {
            diff = (seconds_screenon2) - (seconds_screenoff2);
            seconds_screenon2 = 0;
            seconds_screenoff2 = 0;
        } else {
            diff = (seconds_screenoff2) - (seconds_screenon2);
            seconds_screenon2 = 0;
            seconds_screenoff2 = 0;
        }

        return diff;
    }
}
