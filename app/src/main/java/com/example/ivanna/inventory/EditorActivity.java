package com.example.ivanna.inventory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mModelEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mShelfEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;

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
    }

}
