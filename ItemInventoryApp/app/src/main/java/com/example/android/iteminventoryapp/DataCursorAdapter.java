package com.example.android.iteminventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.iteminventoryapp.data.DataContract;

import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_IMAGE;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;

/**
 * Created by Mirka on 23/07/2017.
 */

public class DataCursorAdapter extends CursorAdapter {
    private Context mContext;

    public DataCursorAdapter(CatalogActivity context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        String productName = cursor.getString(nameColumnIndex);
        nameTextView.setText(productName);

        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_PRICE);
        int productPrice = cursor.getInt(priceColumnIndex);
        priceTextView.setText(String.valueOf(productPrice));

        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
        int productQuantity = cursor.getInt(quantityColumnIndex);
        quantityTextView.setText(String.valueOf(productQuantity));

        ImageView productImageView = (ImageView) view.findViewById(R.id.product_image);
        String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
        productImageView.setImageURI(Uri.parse(image));


        final int idColumnIndex = cursor.getColumnIndex(DataContract.ProductEntry._ID);
        final int productId = cursor.getInt(idColumnIndex);

        view.setOnClickListener(openProductDetailsAction(productId));

        Button saleProductButton = (Button) view.findViewById(R.id.sale_button);
        saleProductButton.setOnClickListener(sellOneItemAction(quantityTextView, productId));
    }

    private View.OnClickListener openProductDetailsAction(final int productId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditActivity.class);
                Uri contentProductUri = ContentUris.withAppendedId(DataContract.ProductEntry.CONTENT_URI, productId);
                intent.setData(contentProductUri);
                mContext.startActivity(intent);
            }
        };
    }

    private View.OnClickListener sellOneItemAction(final TextView quantityTextView, final int productId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityTextView.getText().toString());

                if (0 == quantity) {
                    Toast.makeText(mContext, R.string.out_of_stock,
                            Toast.LENGTH_SHORT).show();
                } else if (quantity > 0) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_PRODUCT_QUANTITY, quantity - 1);

                    Uri currentProductUri =
                            ContentUris.withAppendedId(DataContract.ProductEntry.CONTENT_URI, productId);

                    int rowsAffected =
                            mContext.getContentResolver().update(currentProductUri, values, null, null);

                    if (0 != rowsAffected) {
                        quantityTextView.setText(Integer.toString(quantity - 1));
                    } else {
                        Toast.makeText(mContext, R.string.error_update_item, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }
}