package com.example.android.iteminventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.android.iteminventoryapp.data.DataContract.CONTENT_AUTHORITY;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_IMAGE;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.CONTENT_URI;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry._ID;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private DataCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView productListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_catalog);
        productListView.setEmptyView(emptyView);

        // Create empty adapter
        adapter = new DataCursorAdapter(this, null);
        productListView.setAdapter(adapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> pAdapterView, View pView, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_dummy_data) {
            insertProduct();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_all_items) {
            showDeleteConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_items);
        builder.setPositiveButton(R.string.action_continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteAllProducts();
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

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from products database");
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, "Mia ");
        values.put(COLUMN_PRODUCT_PRICE, "20.00");
        values.put(COLUMN_PRODUCT_QUANTITY, 5);

        Uri imgUri = Uri.parse("android.resource://" + CONTENT_AUTHORITY + "/" + R.drawable.doll);
        values.put(COLUMN_PRODUCT_IMAGE, imgUri.toString());
        Uri newUri = getContentResolver().insert(CONTENT_URI, values);
    }

    public void onSaleBtnClick(long id, int quantity) {
        Uri currentItemUri = ContentUris.withAppendedId(CONTENT_URI, id);

        int newQuantity = quantity > 0 ? quantity - 1 : 0;

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_QUANTITY, newQuantity);

        int rowsAffected = getContentResolver().update(currentItemUri, values, null, null);
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
        } else if (quantity != newQuantity) {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.updated_item) + id,
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                COLUMN_PRODUCT_NAME,
                COLUMN_PRODUCT_PRICE,
                COLUMN_PRODUCT_QUANTITY,
                COLUMN_PRODUCT_IMAGE};

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter.swapCursor(null);
    }
}