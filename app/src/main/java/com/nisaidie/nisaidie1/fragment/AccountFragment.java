package com.nisaidie.nisaidie1.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.database.DBContract;
import com.nisaidie.nisaidie1.database.DBSQLiteHelper;
import com.nisaidie.nisaidie1.database.RecyclerViewCursorAdapter;
import com.nisaidie.nisaidie1.databinding.AccountFragmentBinding;
import com.nisaidie.nisaidie1.util.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class AccountFragment extends Fragment{
    //firebase db
    public Firebase ref;
    public EditText username, phone, email;

    //data binding
    private static final String TAG = "NisaidieUserActivity";
    private AccountFragmentBinding binding;

    //add profile pic
	private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
	private Button btnSelect;
	private ImageView profileImage;
	private String userChoosenTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(getActivity(), R.layout.account_fragment);
       //View view = inflater.inflate(R.layout.account_fragment, container, false);

       //firebase
        Firebase.setAndroidContext(getActivity());
        ref=new Firebase("https://nisaidieapp-73ade.firebaseio.com/");
        username=(EditText) getActivity().findViewById(R.id.et_username);
        phone = (EditText) getActivity().findViewById(R.id.et_phone);
        email = (EditText) getActivity().findViewById(R.id.et_mail);
        profileImage = (ImageView) getActivity().findViewById(R.id.profilePic);

        btnSelect = (Button) getActivity().findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //binding.recycleView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Firebase nameUser=ref.child("Username");
                nameUser.setValue(username.getText()+"");
                Firebase userPhone = ref.child("User_Phone");
                userPhone.setValue(phone.getText()+"");
                Firebase userEmail = ref.child("User_Mail");
                userEmail.setValue(email.getText()+"");
                Firebase userImage=ref.child("Image");
                userImage.setValue(profileImage.toString()+"");
                Toast.makeText(getActivity(),"Details Added",Toast.LENGTH_SHORT).show();

                saveToDB();
            }
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromDB();
            }
        });



		return getView();
	}

    private void saveToDB() {
        SQLiteDatabase database = new DBSQLiteHelper(getActivity()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.Nisaidie_user.COLUMN_NAME, binding.etUsername.getText().toString());
        values.put(DBContract.Nisaidie_user.COLUMN_EMAIL, binding.etMail.getText().toString());
        values.put(DBContract.Nisaidie_user.COLUMN_PHONE, binding.etPhone.getText().toString());
        values.put(DBContract.Nisaidie_user.COLUMN_DESCRIPTION, binding.descEditText.getText().toString());

        try {
            Calendar calendar = Calendar.getInstance();

            long date = calendar.getTimeInMillis();
            values.put(DBContract.Nisaidie_user.COLUMN_JOINED_DATE, date);
        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
           // Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
            return;
        }
        long newRowId = database.insert(DBContract.Nisaidie_user.TABLE_NAME, null, values);

        Toast.makeText(getActivity(), "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        String name = binding.etUsername.getText().toString();
        String desc = binding.descEditText.getText().toString();
        long date = 0;

        /*try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    binding.foundedEditText.getText().toString()));
            date = calendar.getTimeInMillis();
        }
        catch (Exception e) {}*/

        SQLiteDatabase database = new DBSQLiteHelper(getActivity()).getReadableDatabase();

        String[] projection = {
                DBContract.Nisaidie_user._ID,
                DBContract.Nisaidie_user.COLUMN_NAME,
                DBContract.Nisaidie_user.COLUMN_DESCRIPTION,
                DBContract.Nisaidie_user.COLUMN_EMAIL,
                DBContract.Nisaidie_user.COLUMN_PHONE,
                DBContract.Nisaidie_user.COLUMN_JOINED_DATE
        };

        String selection =
                DBContract.Nisaidie_user.COLUMN_NAME + " like ? and " +
                        DBContract.Nisaidie_user.COLUMN_JOINED_DATE + " > ? and " +
                        DBContract.Nisaidie_user.COLUMN_EMAIL + " > ? and " +
                        DBContract.Nisaidie_user.COLUMN_PHONE + " > ? and " +
                        DBContract.Nisaidie_user.COLUMN_DESCRIPTION + " like ?";

        String[] selectionArgs = {"%" + name + "%", date + "", "%" + desc + "%"};

        Cursor cursor = database.query(
                DBContract.Nisaidie_user.TABLE_NAME,     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

        binding.recycleView.setAdapter(new RecyclerViewCursorAdapter(getActivity(), cursor));
    }

//code for adding profile image below
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profileImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profileImage.setImageBitmap(bm);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    HomeFragment home = new HomeFragment();
                    fragmentTransaction.add(R.id.output,home, "HELLO");
                    fragmentTransaction.commit();


                    return true;
                }
                return false;
            }
        });
    }


}