package com.example.shopslistwithgeo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctx;
    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GROCERY_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_SHOP_ITEM + " TEXT,"
                + Constants.KEY_SHOP_LONGITUDE + " TEXT,"
                + Constants.KEY_SHOP_LATITUDE + " TEXT,"
                + Constants.KEY_SHOP_DESCRIPTION + " TEXT,"
                + Constants.KEY_SHOP_RANGE + " TEXT);";

        db.execSQL(CREATE_GROCERY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(db);

    }

    /**
     *  Dodaj, usun, update itp.
     */

    //Add Grocery
    public void addShop(Shop shop) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constants.KEY_SHOP_ITEM, shop.getName());
        values.put(Constants.KEY_SHOP_LONGITUDE, shop.getLongitude());
        values.put(Constants.KEY_SHOP_LATITUDE, shop.getLatitude());
        values.put(Constants.KEY_SHOP_DESCRIPTION,shop.getDescription());
        values.put(Constants.KEY_SHOP_RANGE,shop.getRange());

        //Insert the row
        db.insert(Constants.TABLE_NAME, null, values);


    }


    //Get a Grocery
    public Shop getGrocery(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {Constants.KEY_ID, Constants.KEY_SHOP_ITEM,
                        Constants.KEY_SHOP_LONGITUDE, Constants.KEY_SHOP_LATITUDE, Constants.KEY_SHOP_DESCRIPTION, Constants.KEY_SHOP_RANGE},
                Constants.KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


        Shop shop = new Shop();
        // shop.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
        shop.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_ITEM)));
        shop.setLongitude(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_LONGITUDE)));
        shop.setLatitude(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_LATITUDE)));
        shop.setLatitude(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_DESCRIPTION)));
        shop.setLatitude(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_RANGE)));

        return shop;
    }


    //Get all shops
    public List<Shop> getAllShops() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Shop> shopList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {
                Constants.KEY_ID, Constants.KEY_SHOP_ITEM, Constants.KEY_SHOP_LONGITUDE,
                Constants.KEY_SHOP_LATITUDE, Constants.KEY_SHOP_DESCRIPTION, Constants.KEY_SHOP_RANGE}, null, null, null, null, Constants.KEY_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Shop shop = new Shop();
                shop.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                shop.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_ITEM)));
                shop.setLongitude(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_LONGITUDE)));
                shop.setLatitude(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_LATITUDE)));
                shop.setDescription(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_DESCRIPTION)));
                shop.setRange(cursor.getString(cursor.getColumnIndex(Constants.KEY_SHOP_RANGE)));



                // Add to the shopList
                shopList.add(shop);

            }while (cursor.moveToNext());
        }

        return shopList;
    }


    //Updated Shop
    public int updateShop(Shop shop) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_SHOP_ITEM, shop.getName());
        values.put(Constants.KEY_SHOP_LONGITUDE, shop.getLongitude());
        values.put(Constants.KEY_SHOP_LATITUDE,shop.getLatitude());
        values.put(Constants.KEY_SHOP_DESCRIPTION,shop.getDescription());
        values.put(Constants.KEY_SHOP_RANGE,shop.getRange());

        return db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=?", new String[] { String.valueOf(shop.getId())} );
    }



    public void deleteShop(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + " = ?",
                new String[] {String.valueOf(id)});

        db.close();

    }

}