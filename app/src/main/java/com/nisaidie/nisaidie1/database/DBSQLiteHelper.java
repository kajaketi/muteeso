package com.nisaidie.nisaidie1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "emergency_contact";

    public DBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBContract.Emergency_contacts.CREATE_TABLE);
        sqLiteDatabase.execSQL(DBContract.Nisaidie_user.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.Emergency_contacts.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.Nisaidie_user.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
