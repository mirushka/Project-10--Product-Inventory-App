package com.example.android.iteminventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mirka on 23/07/2017.
 */

public class DataDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;
    private static final String LOG_TAG = DataDbHelper.class.getSimpleName();
    public String SQL_CREATE_ENTRIES = "CREATE TABLE " + DataContract.ProductEntry.TABLE_NAME +
            "(" +
            DataContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
            DataContract.ProductEntry.COLUMN_PRODUCT_PRICE + " STRING NOT NULL," +
            DataContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0," +
            DataContract.ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT" + ");";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(LOG_TAG, SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.ProductEntry.TABLE_NAME);
        onCreate(db);
    }
}
