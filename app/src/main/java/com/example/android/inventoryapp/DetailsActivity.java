package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.android.inventoryapp.data.InventoryProvider.LOG_TAG;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private int currentQuantity;
    private int newQuantity;
    private String currentSupplier;
    private Uri currentProductUri;
    private TextView nameTextView, priceTextView, quantityTextView, supplierTextView;
    private FloatingActionButton addFab, deleteFab, minusFab;
    private ImageView itemImage;
    private Button orderBtn;
    private ContentValues values;

    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        currentProductUri = intent.getData();
        getLoaderManager().initLoader(LOADER_ID, null, this);

        nameTextView = (TextView) findViewById(R.id.item_name);
        priceTextView = (TextView) findViewById(R.id.item_price);
        quantityTextView = (TextView) findViewById(R.id.item_quantity);
        supplierTextView = (TextView) findViewById(R.id.item_supplier);

        itemImage = (ImageView) findViewById(R.id.item_image);

        orderBtn = (Button) findViewById(R.id.order_button);

        addFab = (FloatingActionButton) findViewById(R.id.fab_add);
        minusFab = (FloatingActionButton) findViewById(R.id.fab_minus);
        deleteFab = (FloatingActionButton) findViewById(R.id.fab_delete);

        values = new ContentValues();

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("tel:" + currentSupplier);
                Intent intent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(intent);
            }
        });
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values.clear();
                newQuantity = currentQuantity + 1;
                values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                getContentResolver().update(currentProductUri, values, null, null);
                quantityTextView.setText(String.valueOf(newQuantity));
                getContentResolver().notifyChange(InventoryEntry.CONTENT_URI, null);

            }
        });

        minusFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values.clear();
                if (currentQuantity > 0) {
                    newQuantity = currentQuantity - 1;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    getContentResolver().update(currentProductUri, values, null, null);
                    quantityTextView.setText(String.valueOf(newQuantity));
                }
                getContentResolver().notifyChange(InventoryEntry.CONTENT_URI, null);
            }
        });
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteProduct();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_PRODUCT_IMAGE
        };
        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int imageCI = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);

            String currentName = cursor.getString(nameCI);
            int currentPrice = cursor.getInt(priceCI);
            currentQuantity = cursor.getInt(quantityCI);
            currentSupplier = cursor.getString(supplierCI);
            final String image = cursor.getString(imageCI);

            nameTextView.setText(currentName);
            priceTextView.setText(currentPrice + "");
            quantityTextView.setText(currentQuantity + "");
            supplierTextView.setText(currentSupplier);

            ViewTreeObserver viewTreeObserver = itemImage.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    itemImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    itemImage.setImageBitmap(getBitmapFromUri(Uri.parse(image), DetailsActivity.this, itemImage));
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void deleteProduct() {
        if (currentProductUri != null) {
            getContentResolver().delete(currentProductUri, null, null);
            Toast.makeText(this, nameTextView.getText().toString() + getString(R.string.deleted), Toast.LENGTH_SHORT);
            finish();
        }
    }

    public static Bitmap getBitmapFromUri(Uri uri, Context context, ImageView imageView) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "exception image load failed", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception image load failed", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void confirmDeleteProduct(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_product_message));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}