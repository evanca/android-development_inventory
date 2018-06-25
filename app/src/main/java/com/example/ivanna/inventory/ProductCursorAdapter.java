package com.example.ivanna.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

// Adapter that exposes data from a Cursor to a ListView widget.
// The Cursor must include a column named "_id" or this class will not work.
// Additionally, using MergeCursor with this class will not work
// if the merged Cursors have overlapping values in their "_id" columns.

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView name = view.findViewById(R.id.name_textview);
        TextView info = view.findViewById(R.id.info_textview);
        TextView date = view.findViewById(R.id.date_textview);

        TextView quantity = view.findViewById(R.id.quantity_textview);
        TextView price = view.findViewById(R.id.price_textview);

        // Figure out the index of each column
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int modelColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_MODEL);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int shelfColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SHELF);
        int datestampColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_DATESTAMP);

        // Extract properties from cursor
        String currentName = cursor.getString(nameColumnIndex);
        String currentModel = cursor.getString(modelColumnIndex);
        double currentPrice = cursor.getDouble(priceColumnIndex);
        int currentQuantity = cursor.getInt(quantityColumnIndex);
        String currentShelf = cursor.getString(shelfColumnIndex);
        String currentDatestamp = cursor.getString(datestampColumnIndex);

        // Populate fields with extracted properties
        name.setText(currentName);
        info.setText(currentModel + " | " + currentShelf);
        date.setText(currentDatestamp);

        quantity.setText(String.valueOf(currentQuantity));

        // Format the price to show two zeros after period
        DecimalFormat df = new DecimalFormat("0.00");
        price.setText("x " + String.valueOf(df.format(currentPrice)));
    }
}