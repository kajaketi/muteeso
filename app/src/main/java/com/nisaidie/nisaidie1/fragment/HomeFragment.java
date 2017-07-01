package com.nisaidie.nisaidie1.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.animation.RippleAnimation;
import com.nisaidie.nisaidie1.activity.LoginActivity;


public class HomeFragment extends Fragment {

	RippleAnimation rippleBackground1, rippleBackground2;
	ImageView btn;

	//these variables are for the sms functionality
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    EditText txtphoneNo;
	EditText txtMessage;
    String phonenoo;
	int phoneNo = 0775075211; //testing phone number
	String message = "testing the security app sms feature"; //testing message

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_fragment, container, false);
		init(view);// to initialize widgets
		doRippleBackground(); //start ripple background work..
		btn = (ImageView) view.findViewById(R.id.profileImage);

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickme();
			}
		});

        return view;
	}


	//below is the send message method
	protected void sendSMSMessage() {

        phonenoo = String.valueOf(phoneNo);
		message = txtMessage.getText().toString();

		if (ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.SEND_SMS)
				!= PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
					Manifest.permission.SEND_SMS)) {
			} else {
				ActivityCompat.requestPermissions(getActivity(),
						new String[]{Manifest.permission.SEND_SMS},
						MY_PERMISSIONS_REQUEST_SEND_SMS);
			}
		}
	}

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phonenoo, null, message, null, null);
                    Toast.makeText(getActivity(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

	//trying to activate wake lock feature for the app

	private void init(View view) {
		//root ripple background initialization
		rippleBackground1 = (RippleAnimation) view.findViewById(R.id.content);
		rippleBackground1.setVisibility(View.VISIBLE);
	}

	public void doRippleBackground() {

		startAnimation();

		//handler created to handle cardStack as well as timer...
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				callCardStack();
			}
		}, 8000);

	}

	public void clickme() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.app_name)
				.setMessage("Proceed to send out alert to Next of Kin")
				.setCancelable(false)
				.setIcon(R.mipmap.ic_launcher)
				.setPositiveButton("Share", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT,
								". Get more Mechanics Around You via Makanika Dot Com https://play.google.com/store/apps/details?id=com.makanika" + "\n#Makanika Dot Com");
						sendIntent.setType("text/plain");
						startActivity(Intent.createChooser(sendIntent, "Share Makanika Dot Com"));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_SHORT).show();
					}
				});
		builder.create().show();
	}
	//start the background ripple animation...
	private void startAnimation() {
		//if it's not running
		if (!rippleBackground1.isRippleAnimationRunning()) {
			rippleBackground1.startRippleAnimation();//start root ripple animation
		}
	}

	//this method will stop background ripple animation. if it's running.
	private void stopAnimation() {
		if (rippleBackground1.isRippleAnimationRunning()) {
			rippleBackground1.stopRippleAnimation();
			// rippleBackground2.stopRippleAnimation();
		}
	}

	public void callCardStack() {
		rippleBackground1.setVisibility(View.GONE);

		stopAnimation();//start the ripple background animation.

	}

}