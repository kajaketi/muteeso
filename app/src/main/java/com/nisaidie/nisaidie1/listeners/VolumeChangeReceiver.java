package com.nisaidie.nisaidie1.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import com.nisaidie.nisaidie1.activity.ParentActivity;

/**
 * Created by faren on 04/02/2018.
 */

public class VolumeChangeReceiver extends BroadcastReceiver {
    private static final int PRESS_INTERVAL = 700;
    private long mUpKeyEventTime = 0;

/*    Context context;
    KeyEvent event;
    int keyCode;

    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode()){
            if((event.getEventTime() - mUpKeyEventTime) < PRESS_INTERVAL) {
                //START INTENT
                Intent i = new Intent();
                i.setClass(context, ParentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true;
        }
        return onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event){
        if(KeyEvent.KEYCODE_VOLUME_DOWN == keyCode){
            mUpKeyEventTime = event.getEventTime();
        }
        return onKeyUp(keyCode, event);
    }*/
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
            int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);

            if (newVolume != oldVolume) {
                Intent i = new Intent();
                i.setClass(context, ParentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
     /*
     ///error java.lang.NullPointerException: Attempt to invoke virtual method 'int android.view.KeyEvent.getKeyCode()

     if(KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode()){
            if((event.getEventTime() - mUpKeyEventTime) < PRESS_INTERVAL) {
                //START INTENT
                Intent i = new Intent();
                i.setClass(context, ParentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mUpKeyEventTime = event.getEventTime();
        }*/


    }
}
