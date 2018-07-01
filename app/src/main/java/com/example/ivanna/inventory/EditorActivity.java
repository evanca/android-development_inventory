package com.example.ivanna.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ivanna.inventory.ProductContract.ProductEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        // Get the Intent that started this activity from warehouse products list item and extract the URI
        Intent intent = getIntent();
        Uri currentUri = intent.getData();

        // If the intent does NOT contain a product content URI, then we know that we are
        // creating a new product
        if (currentUri == null) {
            // This is a new product
            setTitle(getString(R.string.add_a_new_item));
        } else {
            // Otherwise this is an existing product
            setTitle(getString(R.string.edit_an_item));
        }

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

        // Create a datestamp to put in a relevant table column
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String datestampString = sdf.format(new Date());

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
        values.put(ProductEntry.COLUMN_PRODUCT_DATESTAMP, datestampString);

        // Insert a new product into the provider, returning the content URI for the new product
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion
            Toast.makeText(this, getString(R.string.editor_insert_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast
            Toast.makeText(this, getString(R.string.editor_insert_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
