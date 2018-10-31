package com.example.anamaria.licentafirsttry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;

public class PoemsProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.anamaria.licentafirsttry";
    private static final String BASE_PATH = "poems";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constant to identify the requested operation
    private static final int GET_POEMS = 1;
    private static final int GET_POEM_BY_ID = 2;
    private static final int GET_POEMS_BY_TEXT = 3;

    private SQLiteDatabase database;
    public static final String CONTENT_ITEM_TYPE = "poem";
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, GET_POEMS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", GET_POEM_BY_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/*", GET_POEMS_BY_TEXT);
    }

    @Override
    public boolean onCreate() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getContext());
        database = dbOpenHelper.getReadableDatabase();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isFirst = sharedPreferences.getBoolean("isFirst", true);
        if (isFirst) {
            this.database.execSQL(DBOpenHelper.CREATE_TABLE_POEMS);
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case GET_POEM_BY_ID:
                selection = DBOpenHelper.POEM_ID + "=" + uri.getLastPathSegment();
                break;
        }
        return database.query(DBOpenHelper.TABLE_POEMS, DBOpenHelper.POEMS_ALL_COLUMNS,
                selection, null, null, null, DBOpenHelper.POEM_CREATED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_POEMS, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_POEMS, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_POEMS, values, selection, selectionArgs);
    }
}