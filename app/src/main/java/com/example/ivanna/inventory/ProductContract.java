package com.example.ivanna.inventory;

import android.net.Uri;
import android.provider.BaseColumns;

// The contract class allows you to use the same constants across all the other classes in the same package.
// This lets you change a column name in one place and have it propagate throughout your code.

public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ProductContract() {
    }

    /**
     * A convenient string to use for the content authority is the package name for the app, which
     * is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.ivanna.inventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_PRODUCTS = "products";

    /* Inner class that defines the table contents */
    public static class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String TABLE_NAME = "products_table";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_MODEL = "product_model";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SHELF = "shelf_number";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier_code";
        public static final String COLUMN_PRODUCT_PHONE = "supplier_phone_number";
    }
}


