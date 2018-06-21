package com.example.ivanna.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ivanna.inventory.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mModelEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mShelfEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;
    private double price;
    private int quantity;
    private int supplierCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mModelEditText = findViewById(R.id.edit_product_model);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mShelfEditText = findViewById(R.id.edit_product_shelf);
        mSupplierEditText = findViewById(R.id.edit_product_supplier);
        mPhoneEditText = findViewById(R.id.edit_product_phone);

        // Visibility state for softInputMode: please hide any soft input area when normally
        // appropriate (when the user is navigating forward to your window).
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent i = new Intent(EditorActivity.this, MainActivity.class);
                finish();  // Kill the editor activity
                startActivity(i);
            }
        });

        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                insertProduct();
                finish();
            }
        });
    }

    /**
     * Get user input from editor and save new item into database
     */
    private void insertProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String modelString = mModelEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        if (!priceString.isEmpty()) {
            price = Double.parseDouble(priceString);
        }
        String quantityString = mQuantityEditText.getText().toString().trim();
        if (!quantityString.isEmpty()) {
            quantity = Integer.parseInt(quantityString);
        }
        String shelfString = mShelfEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        if (!supplierString.isEmpty()) {
            supplierCode = Integer.parseInt(supplierString);
        }
        String phoneString = mPhoneEditText.getText().toString().trim();

        // Create database helper
        ProductDbHelper mDbHelper = new ProductDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_MODEL, modelString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_SHELF, shelfString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierCode);
        values.put(ProductEntry.COLUMN_PRODUCT_PHONE, phoneString);

        // Insert a new row for item in the database, returning the ID of that new row
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion
            Toast.makeText(this, "Error with saving a product. Please fill out all required fields.", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID
            Toast.makeText(this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

}
