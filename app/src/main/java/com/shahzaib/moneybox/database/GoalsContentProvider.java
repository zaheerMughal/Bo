package com.shahzaib.moneybox.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class GoalsContentProvider extends ContentProvider {

    public final int GOALS = 100;
    public final int GOAL_WITH_ID = 101;
    public final int NOTIFICATION_ID_TABLE = 200;
    public final int NOTIFICATION_ID_TABLE_WITH_ID = 201;
    public final int CONTRIBUTIONS_HISTORY_TABLE = 300;


    DatabaseHelper databaseHelper;
    UriMatcher uriMatcher;


    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        uriMatcher = buildUriMatcher();
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        Cursor cursor;

        String goalID;
        String goalSelection;
        String[] goalSelectionArgs;
        switch (match) {
            case GOALS:
                cursor = db.query(DbContract.GOALS.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri); // to notify CursorLoader for any data changes
                return cursor;

            case GOAL_WITH_ID:
                goalID = uri.getPathSegments().get(1); // this will extract the last path from uri (the goal id)
                goalSelection = "_id=?";
                goalSelectionArgs = new String[]{goalID};

                cursor = db.query(DbContract.GOALS.TABLE_NAME,
                        null,
                        goalSelection,
                        goalSelectionArgs,
                        null,
                        null,
                        null,
                        null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;

            case NOTIFICATION_ID_TABLE:
                cursor = db.query(DbContract.NOTIFICATION_IDs.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri); // to notify CursorLoader for any data changes
                return cursor;

            case NOTIFICATION_ID_TABLE_WITH_ID:
                goalID = uri.getPathSegments().get(1); // this will extract the last path from uri (the goal id)
                goalSelection = "_id=?";
                goalSelectionArgs = new String[]{goalID};

                cursor = db.query(DbContract.NOTIFICATION_IDs.TABLE_NAME,
                        null,
                        goalSelection,
                        goalSelectionArgs,
                        null,
                        null,
                        null,
                        null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;

            case CONTRIBUTIONS_HISTORY_TABLE:
                cursor = db.query(DbContract.CONTRIBUTION_HISTORY.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri); // to notify CursorLoader for any data changes
                return cursor;
        }

        db.close();
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        long insertedItemID;
        Uri insertedItemUri;

        switch (match) {
            case GOALS:
                insertedItemID = db.insert(DbContract.GOALS.TABLE_NAME, null, values);
                if (insertedItemID != -1) {
                    SHOW_LOG("Row inserted successfully....\n Row id: " + insertedItemID);
                    insertedItemUri = uri.buildUpon().appendPath(String.valueOf(insertedItemID)).build();
                    getContext().getContentResolver().notifyChange(uri, null);// to notify CursorLoader for any data changes
                    return insertedItemUri;
                } else {
                    insertedItemUri = null;
                    return insertedItemUri;
                }

            case NOTIFICATION_ID_TABLE:
                insertedItemID = db.insert(DbContract.NOTIFICATION_IDs.TABLE_NAME, null, values);
                if (insertedItemID != -1) {
                    SHOW_LOG("Row inserted successfully....\n Row id: " + insertedItemID);
                    insertedItemUri = uri.buildUpon().appendPath(String.valueOf(insertedItemID)).build();
                    getContext().getContentResolver().notifyChange(uri, null);// to notify CursorLoader for any data changes
                    return insertedItemUri;
                } else {
                    insertedItemUri = null;
                    return insertedItemUri;
                }

            case CONTRIBUTIONS_HISTORY_TABLE:
                insertedItemID = db.insert(DbContract.CONTRIBUTION_HISTORY.TABLE_NAME, null, values);
                if (insertedItemID != -1) {
                    SHOW_LOG("Row inserted successfully....\n Row id: " + insertedItemID);
                    insertedItemUri = uri.buildUpon().appendPath(String.valueOf(insertedItemID)).build();
                    getContext().getContentResolver().notifyChange(uri, null);// to notify CursorLoader for any data changes
                    return insertedItemUri;
                } else {
                    insertedItemUri = null;
                    return insertedItemUri;
                }
        }

        db.close();
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        String goalID = uri.getPathSegments().get(1); // this will extract the last path from uri (the goal id)
        String goalSelection = "_id=?";
        String[] goalSelectionArgs = new String[]{goalID};
        int noOfDeletedItem;
        switch (match) {
            case GOAL_WITH_ID:
                noOfDeletedItem = db.delete(DbContract.GOALS.TABLE_NAME,goalSelection,goalSelectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);// to notify CursorLoader for any data changes
                return noOfDeletedItem;

            case NOTIFICATION_ID_TABLE_WITH_ID:
                noOfDeletedItem = db.delete(DbContract.NOTIFICATION_IDs.TABLE_NAME,goalSelection,goalSelectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);// to notify CursorLoader for any data changes
                return noOfDeletedItem;

            case CONTRIBUTIONS_HISTORY_TABLE:
                noOfDeletedItem = db.delete(DbContract.CONTRIBUTION_HISTORY.TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);// to notify CursorLoader for any data changes
                return noOfDeletedItem;

            default:
                db.close();
                throw new UnsupportedOperationException("Error while Deleting item, Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        String goalID = uri.getPathSegments().get(1); // this will extract the last path from uri (the goal id)
        String goalSelection = "_id=?";
        String[] goalSelectionArgs = new String[]{goalID};

        switch (match) {
            case GOAL_WITH_ID:
                int itemUpdated = db.update(DbContract.GOALS.TABLE_NAME, values, goalSelection, goalSelectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);// to notify CursorLoader for any data changes
                return itemUpdated;

            default:
                db.close();
                throw new UnsupportedOperationException("Error while updating item, Unknown Uri: " + uri);
        }
    }


    //******************************* Helper methods
    //********* Helper methods
    private UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.GOALS.TABLE_NAME, GOALS);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.GOALS.TABLE_NAME + "/#", GOAL_WITH_ID);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.NOTIFICATION_IDs.TABLE_NAME, NOTIFICATION_ID_TABLE);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.NOTIFICATION_IDs.TABLE_NAME + "/#", NOTIFICATION_ID_TABLE_WITH_ID);
        uriMatcher.addURI(DbContract.AUTHORITY, DbContract.CONTRIBUTION_HISTORY.TABLE_NAME, CONTRIBUTIONS_HISTORY_TABLE);

        return uriMatcher;
    }

    private void SHOW_LOG(String message) {
        Log.i("123456", message);
    }


}


