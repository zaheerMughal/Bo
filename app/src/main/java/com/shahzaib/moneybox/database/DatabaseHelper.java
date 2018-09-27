package com.shahzaib.moneybox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "Glla.db";
    public static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_GOALS_TABLE = "CREATE TABLE "+DbContract.GOALS.TABLE_NAME+
                "(" +
                DbContract.GOALS._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.GOALS.COLUMN_TITLE+" TEXT NOT NULL," +
                DbContract.GOALS.COLUMN_PICTURE_NAME +" String, " +
                DbContract.GOALS.COLUMN_TARGET_AMOUNT +" INTEGER NOT NULL, " +
                DbContract.GOALS.COLUMN_DEPOSITED_AMOUNT +" INTEGER, " +
                DbContract.GOALS.COLUMN_TARGET_DATE+" INT8 NOT NULL," +
                DbContract.GOALS.COLUMN_IS_COMPLETED+" TEXT," +
                DbContract.GOALS.COLUMN_ALARM_ID+" INTEGER," +
                DbContract.GOALS.COLUMN_SAVING_FREQUENCY+" INTEGER," +
                DbContract.GOALS.COLUMN_REMINDER +" INT8" +
                ");";
        final String CREATE_REQUEST_CODES_TABLE = "CREATE TABLE "+ DbContract.NOTIFICATION_IDs.TABLE_NAME+
                "(" +
                DbContract.NOTIFICATION_IDs._ID+" INTEGER,"+
                DbContract.NOTIFICATION_IDs.COLUMN_NotificationID +" INTEGER"+
                ")";

        final String CREATE_CONTRIBUTIONS_HISTORY_TABLE = "CREATE TABLE "+ DbContract.CONTRIBUTION_HISTORY.TABLE_NAME+
                "(" +
                DbContract.CONTRIBUTION_HISTORY._ID+" INTEGER,"+
                DbContract.CONTRIBUTION_HISTORY.COLUMN_DATE +" INTEGER,"+
                DbContract.CONTRIBUTION_HISTORY.COLUMN_AMOUNT +" INTEGER"+
                ")";


        db.execSQL(CREATE_GOALS_TABLE);
        db.execSQL(CREATE_REQUEST_CODES_TABLE);
        db.execSQL(CREATE_CONTRIBUTIONS_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_GOALS_TABLE = "DROP TABLE IF EXISTS "+ DbContract.GOALS.TABLE_NAME;
        final String DROP_REQUEST_CODES_TABLE = "DROP TABLE IF EXISTS "+ DbContract.NOTIFICATION_IDs.TABLE_NAME;
        final String DROP_CONTRIBUTIONS_HISTORY_TABLE = "DROP TABLE IF EXISTS "+ DbContract.CONTRIBUTION_HISTORY.TABLE_NAME;

        db.execSQL(DROP_GOALS_TABLE);
        db.execSQL(DROP_REQUEST_CODES_TABLE);
        db.execSQL(DROP_CONTRIBUTIONS_HISTORY_TABLE);
        onCreate(db);
    }


}
