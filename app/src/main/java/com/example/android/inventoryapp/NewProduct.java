package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;


public class NewProduct extends AppCompatActivity {

    private String name, supplier;
    private String imagePath;
    private int quantity;
    private double price;

    private Button addImage, addBtn;

    private Uri mImageUri;
    private ContentValues values;

    private final int IMAGE_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        addBtn = (Button) findViewById(R.id.add_product_button);
        addImage = (Button) findViewById(R.id.add_image);

        values = new ContentValues();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonImageClick();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertProduct();
            }
        });
    }

    private void insertProduct() {
        EditText nameET = (EditText) findViewById(R.id.new_item_name_editText);
        EditText priceET = (EditText) findViewById(R.id.new_item_price_editText);
        EditText quantityET = (EditText) findViewById(R.id.new_item_quantity_editText);
        EditText supplierET = (EditText) findViewById(R.id.new_item_supplier_editText);

        if (nameET == null || TextUtils.isEmpty(nameET.getText().toString())) {
            Toast.makeText(this, getString(R.string.add_product_name), Toast.LENGTH_SHORT).show();
            return;
        }

        name = nameET.getText().toString().trim();

        if (priceET == null || TextUtils.isEmpty(priceET.getText().toString())) {
            Toast.makeText(this, getString(R.string.add_product_price), Toast.LENGTH_SHORT).show();
            return;
        }

        price = Double.parseDouble(priceET.getText().toString().trim());

        if (quantityET == null || TextUtils.isEmpty(quantityET.getText().toString())) {
            Toast.makeText(this, getString(R.string.add_product_quantity), Toast.LENGTH_SHORT).show();
            return;
        }

        quantity = Integer.parseInt(quantityET.getText().toString().trim());

        if (supplierET == null || TextUtils.isEmpty(supplierET.getText().toString())) {
            Toast.makeText(this, getString(R.string.add_product_supplier), Toast.LENGTH_SHORT).show();
            return;
        }

        supplier = supplierET.getText().toString().trim();

        if (mImageUri == null) {
            Toast.makeText(this, getString(R.string.add_product_image), Toast.LENGTH_SHORT).show();
            return;
        }

        imagePath = mImageUri.toString();

        values.clear();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, name);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, imagePath);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        if (newUri != null) {
            Toast.makeText(this, "Product added",
                    Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(NewProduct.this, MainActivity.class));
    }

    private void buttonImageClick() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && (resultCode == RESULT_OK)) {
            try {
                mImageUri = data.getData();
                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
