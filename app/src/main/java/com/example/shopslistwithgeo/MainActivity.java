package com.example.shopslistwithgeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "GeofenceTransition";
	private Button mapb;
	private Button Shops;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Shops = (Button) findViewById(R.id.shops_btn);
		mapb = (Button) findViewById(R.id.maps_btn);
		Shops.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, ShopListActivity.class));
			}
		});
		mapb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,MapsActivity.class));
			}
		});
	}
}