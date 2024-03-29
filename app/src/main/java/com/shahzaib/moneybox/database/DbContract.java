package com.shahzaib.moneybox.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DbContract {

    public static final String AUTHORITY = "com.shahzaib.glla.Database";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);




    private DbContract(){}


    public static class GOALS implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(GOALS.TABLE_NAME).build();

        public static final String TABLE_NAME = "GOALS"; // path to this table
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_PICTURE_NAME = "Picture";
        public static final String COLUMN_TARGET_AMOUNT = "TargetAmount";
        public static final String COLUMN_DEPOSITED_AMOUNT = "DepositedAmount";
        public static final String COLUMN_TARGET_DATE = "TargetDate";
        public static final String COLUMN_ALARM_ID = "AlarmID";
        public static final String COLUMN_SAVING_FREQUENCY = "SavingFrequency";
        public static final String COLUMN_REMINDER = "Reminder";
        public static final String COLUMN_IS_COMPLETED = "isCompleted";
    }

    public static class NOTIFICATION_IDs
    {// saving request code will help to cancel notifications on button click
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(NOTIFICATION_IDs.TABLE_NAME).build();

        public static final String TABLE_NAME = "NotificationTable"; // path to this table
        public static final String _ID = "_id";
        public static final String COLUMN_NotificationID = "NotificationID";
    }


    public static class CONTRIBUTION_HISTORY
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTRIBUTION_HISTORY.TABLE_NAME).build();

        public static final String TABLE_NAME = "ContributionsHistoryTable"; // path to this table
        public static final String _ID = "_id"; // goal id, in which user contribute money
        public static final String COLUMN_DATE = "Date";
        public static final String COLUMN_AMOUNT = "Amount";
    }



}
