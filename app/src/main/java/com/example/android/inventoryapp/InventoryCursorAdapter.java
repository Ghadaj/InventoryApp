package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by ghada on October/4/2017 AD.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTV = view.findViewById(R.id.item_name);
        TextView priceTV = view.findViewById(R.id.item_price);
        final TextView quantityTV = view.findViewById(R.id.item_quantity);

        int nameCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String currentName = cursor.getString(nameCI);
        int currentPrice = cursor.getInt(priceCI);
        final int currentQuantity = cursor.getInt(quantityCI);

        final int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry._ID));
        final int previousQuantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY));

        Button saleBtn = view.findViewById(R.id.sale_button);

        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();

                if (previousQuantity > 0) {
                    int newQuantity = previousQuantity - 1;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    Uri updateUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, rowId);
                    context.getContentResolver().update(updateUri, values, null, null);
                    quantityTV.setText(String.valueOf(newQuantity));
                }
                context.getContentResolver().notifyChange(InventoryEntry.CONTENT_URI, null);
            }
        });

        nameTV.setText(currentName);
        priceTV.setText(currentPrice + "");
        quantityTV.setText(currentQuantity + "");
    }
}