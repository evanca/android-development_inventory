package com.example.ivanna.inventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ivanna.inventory.ProductContract.ProductEntry;

// When you use SQLiteOpenHelper class to obtain references to your database, the system performs
// the potentially long-running operations of creating and updating the database only when needed
// and not during app startup.

public class ProductDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "warehouse.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the table
        // Note that phone no. is a text, to be able to quickly copy & paste from a supplier website
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_MODEL + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0.00, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_SHELF + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " INTEGER NOT NULL DEFAULT 000, "
                + ProductEntry.COLUMN_PRODUCT_PHONE + " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_DATESTAMP + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
