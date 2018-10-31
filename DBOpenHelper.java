package com.example.anamaria.licentafirsttry;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBOpenHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "rimeaza.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_POEMS = "poem";
    public static final String POEM_ID = "_id";
    public static final String POEM_TEXT = "poemText";
    public static final String POEM_CREATED = "poemCreated";
    public static final String POEM_TITLE = "poemTitle";
    public static final String POEM_TYPE = "poemType";


    public static final String TABLE_WORDS = "word";
    public static final String WORD_ID = "_id";
    public static final String WORD_TEXT = "wordText";
    public static final String WORD_LETTERS = "lastLetters";
    public static final String WORD_FREQUENCY = "wordFrequency";

    public static final String[] WORDS_ALL_COLUMNS =
            {WORD_ID, WORD_TEXT, WORD_LETTERS, WORD_FREQUENCY};

    public static final String[] POEMS_ALL_COLUMNS =
            {POEM_ID, POEM_TEXT, POEM_CREATED, POEM_TITLE, POEM_TYPE};


    //SQL to create table
    public static final String CREATE_TABLE_POEMS =
            "CREATE TABLE " + TABLE_POEMS + " (" +
                    POEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    POEM_TEXT + " TEXT, " +
                    POEM_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    POEM_TITLE + " TEXT, " +
                    POEM_TYPE + " INTEGER " +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_TABLE_POEMS);
//        db.execSQL(CREATE_TABLE_WORDS);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POEMS);
//        db.execSQL("DROP TABLE IF EXISTS" + TABLE_WORDS);
//
}
