package com.nisaidie.nisaidie1.database;

import android.provider.BaseColumns;


/**
 * Created by obaro on 26/09/2016.
 */

public final class DBContract {

    public static final String SELECT_EM_CONTACT= "SELECT * " +
            "FROM " + Emergency_contacts.TABLE_NAME + " INNER JOIN " + Nisaidie_user.TABLE_NAME + "  "
            + "ON " + Emergency_contacts.COLUMN_NISAIDIEUSER_ID + " WHERE " +
            "" + Emergency_contacts.COLUMN_FIRSTNAME + " like ? AND " + Emergency_contacts.COLUMN_LASTNAME + " like ?";


    private DBContract() {
    }

    public static class Nisaidie_user implements BaseColumns {
        public static final String TABLE_NAME = "Nisaidie_user";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_JOINED_DATE = "date";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_JOINED_DATE + " INTEGER" + ")";
    }

    public static class Emergency_contacts implements BaseColumns {
        public static final String TABLE_NAME = "Emergency_contacts";
        public static final String COLUMN_FIRSTNAME = "firstname";
        public static final String COLUMN_LASTNAME = "lastname";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_OTHER = "other";
        public static final String COLUMN_NISAIDIEUSER_ID = "nisaidie_user_id";
        public static final String COLUMN_EM_ADDRESS = "em_address";
        public static final String COLUMN_EM_REL = "relationship";
        public static final String COLUMN_ADDED_DATE = "em_added_date";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIRSTNAME + " TEXT, " +
                COLUMN_LASTNAME + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_OTHER + " TEXT, " +
                COLUMN_EM_REL + " TEXT, " +
                COLUMN_NISAIDIEUSER_ID + " INTEGER, " +
                COLUMN_EM_ADDRESS + " TEXT, " +
                COLUMN_ADDED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_NISAIDIEUSER_ID + ") REFERENCES " +
                Nisaidie_user.TABLE_NAME + "(" + Nisaidie_user._ID + ") " + ")";

    }
}
