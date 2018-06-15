package com.example.ivanna.inventory;

import android.provider.BaseColumns;

// The contract class allows you to use the same constants across all the other classes in the same package.
// This lets you change a column name in one place and have it propagate throughout your code.

public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ProductContract() {
    }

    /* Inner class that defines the table contents */
    // Note that both double and single quotes are used here to prevent syntax errors caused by spaces
    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "'Product Name'";
        public static final String COLUMN_PRODUCT_MODEL = "'Product Model'";
        public static final String COLUMN_PRODUCT_PRICE = "'Price'";
        public static final String COLUMN_PRODUCT_QUANTITY = "'Quantity'";
        public static final String COLUMN_PRODUCT_SHELF = "'Shelf Number'";
        public static final String COLUMN_PRODUCT_SUPPLIER = "'Supplier Code'";
        public static final String COLUMN_PRODUCT_PHONE = "'Supplier Phone Number'";
    }
}


