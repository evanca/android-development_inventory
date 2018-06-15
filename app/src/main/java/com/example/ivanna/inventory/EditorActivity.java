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
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);

    }

}
