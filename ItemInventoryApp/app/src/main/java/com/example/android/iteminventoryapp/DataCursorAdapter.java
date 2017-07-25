package com.example.android.iteminventoryapp;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.iteminventoryapp.data.DataContract;

import static com.example.android.iteminventoryapp.R.id.quantity;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_IMAGE;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.iteminventoryapp.data.DataContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;

/**
 * Created by Mirka on 23/07/2017.
 */

public class DataCursorAdapter extends CursorAdapter {

    private CatalogActivity catalogActivity = new CatalogActivity();

    public DataCursorAdapter(CatalogActivity context, Cursor c) {
        super(context, c, 0);
        this.catalogActivity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        int nameColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME);
        String productName = cursor.getString(nameColumnIndex);
        nameTextView.setText(productName);

        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE);
        int productPrice = cursor.getInt(priceColumnIndex);
        priceTextView.setText(String.valueOf(productPrice));

        TextView quantityTextView = (TextView) view.findViewById(quantity);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        quantityTextView.setText(String.valueOf(productQuantity));

        ImageView productImageView = (ImageView) view.findViewById(R.id.product_image);
        String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
        productImageView.setImageURI(Uri.parse(image));


        final Long id = cursor.getLong(cursor.getColumnIndex(DataContract.ProductEntry._ID));


        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catalogActivity.onListItemClick(id);
            }
        });
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catalogActivity.onSaleButtonClick(id, productQuantity);
                notifyDataSetChanged();
            }
        });

    }
}
