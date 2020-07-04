package com.example.shopslistwithgeo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends AppCompatActivity {
	private RecyclerView recyclerView;
	private RecyclerViewAdapter recyclerViewAdapter;
	private List<Shop> groceryList;
	private List<Shop> listItems;
	private DatabaseHandler db;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog dialog;
	private EditText shopItem;
	private EditText description;
	private EditText range;
	private Button saveButton;
	private SharedPreferences mPrefs;
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

	LocationManager lm;
	Criteria criteria;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_list);

		Button fab = (Button) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createPopDialog();
			}
		});

		db = new DatabaseHandler(this);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		groceryList = new ArrayList<>();
		listItems = new ArrayList<>();

		// Get items from database
		groceryList = db.getAllShops();

		for (Shop c : groceryList) {
			Shop grocery = new Shop();
			grocery.setName(c.getName());
			grocery.setLongitude(c.getLongitude());
			grocery.setId(c.getId());

			grocery.setLatitude(c.getLatitude());
			grocery.setDescription(c.getDescription());
			grocery.setRange(c.getRange());
			listItems.add(grocery);
		}
		recyclerViewAdapter = new RecyclerViewAdapter(this, listItems);
		recyclerView.setAdapter(recyclerViewAdapter);
		recyclerViewAdapter.notifyDataSetChanged();
	}

	private void createPopDialog() {
		dialogBuilder = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.popup, null);
		shopItem = (EditText) view.findViewById(R.id.shopItem);
		description = (EditText) view.findViewById(R.id.shopDescription);
		range = (EditText) view.findViewById(R.id.shopRange);
		saveButton = (Button) view.findViewById(R.id.saveButton);

		dialogBuilder.setView(view);
		dialog = dialogBuilder.create();
		dialog.show();

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveGroceryToDB(v);
			}
		});
	}

	private void saveGroceryToDB(View v) {
		Shop grocery = new Shop();

		String newShopRange = range.getText().toString();
		Location current_loc = createLocationManager(newShopRange);
		String newGrocery = shopItem.getText().toString();
		String newShopLongitude = String.valueOf(current_loc.getLongitude());
		String newShopLatitude = String.valueOf(current_loc.getLatitude());
		String newShopDescription = description.getText().toString();

		grocery.setName(newGrocery);
		grocery.setLongitude(newShopLongitude);
		grocery.setLatitude(newShopLatitude);
		grocery.setDescription(newShopDescription);
		grocery.setRange(newShopRange);

		//Save to DB
		db.addShop(grocery);
				new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
				//start a new activity
				startActivity(new Intent(ShopListActivity.this, ShopListActivity.class));
				finish();
			}
		}, 500); //  1 second.
	}


	private Location createLocationManager(String radius){
	   if (checkLocationPermission()) {
		   LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		   Criteria criteria = new Criteria();
		   criteria.setAltitudeRequired(true);
		   criteria.setAccuracy(Criteria.ACCURACY_FINE);
		   criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		   String provider = lm.getBestProvider(criteria, false);
		   Location location = lm.getLastKnownLocation(provider);

		   if (location != null) {
			   Log.i("map-app-tag", "Latitude: " + location.getLatitude());
			   Log.i("map-app-tag", "Longtitude: " + location.getLongitude());
			   Log.i("map-app-tag", "Altitude: " + location.getAltitude());
			   return location;
		   } else {
			   Log.e("map-app-tag", "Unable to receive location.");
		   }
	   }
		return null;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode==MY_PERMISSIONS_REQUEST_LOCATION) {
			if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
				Intent intent = new Intent(ShopListActivity.this, ShopListActivity.class);
				startActivity(intent);
				Toast.makeText(this,"Location permission allowed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this,"Location permission not allowed", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(ShopListActivity.this, MainActivity.class);
				startActivity(intent);
			}
		}
	}

	public boolean checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					MY_PERMISSIONS_REQUEST_LOCATION);
			return false;
		} else {
			return true;
		}
	}
}