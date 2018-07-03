package com.example.ivanna.inventory;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ivanna.inventory.ProductContract.ProductEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<android.database.Cursor> {

    /**
     * Identifier for the data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentUri;

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

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add a "Trash" button to menu
        getMenuInflater().inflate(R.menu.menu_trash, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Trash" menu item.
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.trash);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get the Intent that started this activity from warehouse products list item and extract the URI
        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        // If the intent does NOT contain a product content URI, then we know that we are
        // creating a new product
        if (mCurrentUri == null) {
            // This is a new product
            setTitle(getString(R.string.add_a_new_item));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an item that hasn't been created yet.)
            invalidateOptionsMenu();

        } else {
            // Otherwise this is an existing product
            setTitle(getString(R.string.edit_an_item));

            // Prepare the loader
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
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

                if (mNameEditText.getText().toString().isEmpty()) {
                    mNameEditText.setError(getString(R.string.field_required));
                } else if (mModelEditText.getText().toString().isEmpty()) {
                    mModelEditText.setError(getString(R.string.field_required));
                } else {
                    saveProduct();
                    finish();
                }
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        mNameEditText.setOnTouchListener(mTouchListener);
        mModelEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mShelfEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);

    }

    /**
     * Get user input from editor and save item into database
     */
    private void saveProduct() {
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

        // Determine if this is a new or existing item / product by checking if current URI is null or not
        if (mCurrentUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because current URI will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {

        // If the item hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Respond to a click on the "Up" arrow button in the app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trash:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_MODEL,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SHELF,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_PHONE};

        return new CursorLoader(
                this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Figure out the index of each column
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int modelColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_MODEL);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int shelfColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SHELF);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PHONE);

            // Extract properties from cursor
            String currentName = cursor.getString(nameColumnIndex);
            String currentModel = cursor.getString(modelColumnIndex);
            double currentPrice = cursor.getDouble(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentShelf = cursor.getString(shelfColumnIndex);
            int currentSupplier = cursor.getInt(supplierColumnIndex);
            String currentPhone = cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(currentName);
            mModelEditText.setText(currentModel);
            mPriceEditText.setText(String.valueOf(currentPrice));
            mQuantityEditText.setText(String.valueOf(currentQuantity));
            mShelfEditText.setText(currentShelf);
            mSupplierEditText.setText(String.valueOf(currentSupplier));
            mPhoneEditText.setText(currentPhone);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mModelEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mShelfEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing item / product.
        if (mCurrentUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.

            // Pass in null for the selection and selection args because the mCurrentUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
