package com.nisaidie.nisaidie1.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.database.DBContract;
import com.nisaidie.nisaidie1.database.DBSQLiteHelper;
import com.nisaidie.nisaidie1.database.JoinRecyclerViewCursorAdapter;
import com.nisaidie.nisaidie1.databinding.AddFragmentBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EmergencyContacts extends AppCompatActivity {

    private AddFragmentBinding binding;
    private static final String TAG = "EmergencyActivity";

    DateFormat dateFormat = new SimpleDateFormat();
    Calendar cal = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.add_fragment);


        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

        String[] queryCols = new String[]{"_id", DBContract.Nisaidie_user.COLUMN_NAME};
        String[] adapterCols = new String[]{DBContract.Nisaidie_user.COLUMN_NAME};
        int[] adapterRowViews = new int[]{android.R.id.text1};

        SQLiteDatabase database = new DBSQLiteHelper(this).getReadableDatabase();
        Cursor cursor = database.query(
                DBContract.Nisaidie_user.TABLE_NAME,     // The table to query
                queryCols,                                // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

       /*

        //this is for a spinner if we want to return views based on the app user's accounts if we have more than one account
        //attached to each app user.
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this, android.R.layout.simple_spinner_item, cursor, adapterCols, adapterRowViews, 0);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.employerSpinner.setAdapter(cursorAdapter);


        */

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveToDB();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        binding.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    readFromDB();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

               return;
            }
        });



       /*

        //if we had a search button we would implement it here. to search for contacts in db
       binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromDB();
            }
        });

        */

    }

    private void saveToDB() throws ParseException {
        SQLiteDatabase database = new DBSQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.Emergency_contacts.COLUMN_FIRSTNAME, binding.emFName.getText().toString());
        values.put(DBContract.Emergency_contacts.COLUMN_LASTNAME, binding.emLName.getText().toString());
        values.put(DBContract.Emergency_contacts.COLUMN_PHONE, binding.emPhone.getText().toString());
        values.put(DBContract.Emergency_contacts.COLUMN_OTHER, binding.emOther.getText().toString());
        values.put(DBContract.Emergency_contacts.COLUMN_EM_ADDRESS, binding.emAddress.getText().toString());
        values.put(DBContract.Emergency_contacts.COLUMN_EM_REL, binding.emRel.getText().toString());

        /*
            //THIS WOULD BE USED IF WE WANT TO BIND EM RESULTS BASED ON THE ASSOCIATED USER
        values.put(DBContract.Emergency_contacts.COLUMN_NISAIDIEUSER_ID,
                ((Cursor)binding.employerSpinner.getSelectedItem()).getInt(0));

        Log.d("getINT", ((Cursor)binding.employerSpinner.getSelectedItem()).getInt(0) + "");
        Log.d("getColumnName", ((Cursor)binding.employerSpinner.getSelectedItem()).getColumnName(0));
*/


       //insterting date and time to the added field
        /*
        * Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    binding.foundedEditText.getText().toString()));
            long date = calendar.getTimeInMillis();
            values.put(SampleDBContract.Employer.COLUMN_FOUNDED_DATE, date);
        *String da = "1957-01-01";
DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
Date date=df.parse(da); // parse your string to date
Calendar calendar = Calendar.getInstance();
calendar.setTime(date);
System.out.println(df.format(calendar.getTime())); // format date
        *
        * */

        String da = "31/12/2017";
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
        Date date = df.parse(da);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        values.put(DBContract.Emergency_contacts.COLUMN_ADDED_DATE, df.format(cal.getTime()));

        long newRowId = database.insert(DBContract.Emergency_contacts.TABLE_NAME, null, values);

        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "you have added succesfully " + binding.emFName.getText().toString(), Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        String firstname = binding.emFName.getText().toString();
        String lastname = binding.emLName.getText().toString();

        SQLiteDatabase database = new DBSQLiteHelper(this).getReadableDatabase();

        String[] selectionArgs = {"%" + firstname + "%", "%" + lastname + "%"};

        Cursor cursor = database.rawQuery(DBContract.SELECT_EM_CONTACT, selectionArgs);
        binding.recycleView.setAdapter(new JoinRecyclerViewCursorAdapter(this, cursor));
    }
}
