package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by ghada on October/4/2017 AD.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME= "inventory.db";
    private static final int DATABASE_VERSION = 1;
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE "+ InventoryEntry.TABLE_NAME +" ("
                + InventoryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME+" TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_PRICE+" INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_QUANTITY+" INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_PRODUCT_SUPPLIER+" TEXT, "
                + InventoryEntry.COLUMN_PRODUCT_IMAGE+" TEXT);";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
