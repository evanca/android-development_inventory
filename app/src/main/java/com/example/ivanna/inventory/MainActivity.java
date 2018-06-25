package com.example.ivanna.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.ivanna.inventory.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_MODEL,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SHELF,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_PHONE,
                ProductEntry.COLUMN_PRODUCT_DATESTAMP};

        // Perform a query on the provider using the ContentResolver.
        Cursor cursor = getContentResolver().query(
                ProductEntry.CONTENT_URI, // The content URI of the table
                projection, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null); // The sort order

        // Find ListView to populate
        ListView warehouseItems = findViewById(R.id.warehouse_listview);

        // Setup cursor adapter using cursor from last step
        ProductCursorAdapter adapter = new ProductCursorAdapter(this, cursor);

        // Attach cursor adapter to the ListView
        warehouseItems.setAdapter(adapter);

    }
}
