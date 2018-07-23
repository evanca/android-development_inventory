package com.example.ivanna.inventory;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivanna.inventory.ProductContract.ProductEntry;

public class StatsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    String maxQuantity;
    String currency;
    private int countNumberOfGoods = 0;
    private double sumStockValue = 0.00;
    private Cursor stockValueCursor;
    private Cursor supplierCountCursor;
    private Cursor noStockCursor;
    private ProgressDialog dialog;
    private ProductDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setTitle(R.string.title_stats);

        // Retrieve values from a shared preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        maxQuantity = sharedPrefs.getString(
                getString(R.string.settings_max_quantity_key),
                getString(R.string.settings_max_quantity_default));
        currency = sharedPrefs.getString(
                getString(R.string.settings_currency_key),
                getString(R.string.settings_currency_EUR));

        // If custom currency is set, use it instead of a pre-defined currency:
        if (currency.equals(getString(R.string.settings_currency_custom_title))) {
            String checkCustomCurrency = sharedPrefs.getString(
                    getString(R.string.settings_currency_key_custom),
                    getString(R.string.settings_currency_EUR));
            currency = checkCustomCurrency;
            // If user forgot to input a custom value, just use the default:
            if (currency.isEmpty()) {
                currency = getString(R.string.settings_currency_EUR);
            }
        }

        // Here we need to use an SQLiteOpenHelper because Content Providers don't support rawQuery()
        mDbHelper = new ProductDbHelper(this);
        new rawQueryAsyncTask().execute();

        // Prepare the loader to count the number of items on stock
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    // This is called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create and return a CursorLoader that will take care of
        // creating a Cursor for counting ID's
        String[] projection = {
                ProductEntry._ID,};

        return new CursorLoader(
                this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        countNumberOfGoods = data.getCount();
        TextView numberOfGoods = findViewById(R.id.number_of_goods);
        numberOfGoods.setText(String.valueOf(countNumberOfGoods) + " / " + maxQuantity);

        // Calculate the fullness of the warehouse to show in our pie chart:
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        double d = (double) countNumberOfGoods / (double) Integer.valueOf(maxQuantity);
        int progress = (int) (d * 100);
        pieChart.setProgress(progress);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        countNumberOfGoods = 0;
    }

    private void updateTextViews() {
        if (stockValueCursor == null) {
            Toast.makeText(this, getString(R.string.loading_stats_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            if (stockValueCursor.moveToFirst()) {
                sumStockValue = stockValueCursor.getDouble(0);
            }
            TextView stockValue = findViewById(R.id.stock_value_text);
            stockValue.setText(String.valueOf(sumStockValue) + " " + currency);
        }

        if (supplierCountCursor == null) {
            Toast.makeText(this, getString(R.string.loading_stats_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            int countSuppliers = supplierCountCursor.getCount();
            TextView supplierCount = findViewById(R.id.supplier_count_text);
            supplierCount.setText(String.valueOf(countSuppliers));
        }

        if (noStockCursor == null) {
            Toast.makeText(this, getString(R.string.loading_stats_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            int countNoStock = noStockCursor.getCount();
            TextView noStock = findViewById(R.id.no_stock_text);
            // When using the getQuantityString() method, you need to pass the count twice if your
            // string includes string formatting with a number.
            noStock.setText(getResources().getQuantityString(R.plurals.models, countNoStock, countNoStock));
        }
    }

    // Because it can be long-running, be sure that you call getReadableDatabase() in a background
    // thread, such as with AsyncTask
    private class rawQueryAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            // Use a rawQuery to calculate stats
            stockValueCursor = mDbHelper.getReadableDatabase().rawQuery("SELECT SUM(price*quantity) FROM products", null);
            supplierCountCursor = mDbHelper.getReadableDatabase().rawQuery("SELECT DISTINCT supplier_code FROM products", null);
            noStockCursor = mDbHelper.getReadableDatabase().rawQuery("SELECT _id FROM products WHERE quantity = 0", null);
            return true;
        }

        @Override
        protected void onPreExecute() {
            // Show a loading dialog
            dialog = new ProgressDialog(StatsActivity.this);
            dialog.setMessage(getString(R.string.getting_data_stats));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            // Close loading dialog and update the views
            dialog.cancel();
            updateTextViews();
        }
    }
}

