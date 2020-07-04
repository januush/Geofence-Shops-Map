package com.example.shopslistwithgeo;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
	private static final String TAG = "GeofenceTransition";
	private GoogleMap mMap;
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	private List<Shop> shopList;
	private DatabaseHandler db;
	int minCzas = 0;
	int minDystans = 0;
	private LocationManager lm;
	private Circle geoFenceLimits;
	private int i = 0;
	private PendingIntent geofencePendintIntent;
	private GeofencingClient gc;
	private ArrayList<Geofence> mGeofenceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		mGeofenceList = new ArrayList<>();
		gc = LocationServices.getGeofencingClient(this);
		db = new DatabaseHandler(this);
		shopList = new ArrayList<>();

		i = 1;
		// Get items from database
		shopList = db.getAllShops();
		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (checkLocationPermission()) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minCzas, minDystans, this);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minCzas, minDystans, this);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode==MY_PERMISSIONS_REQUEST_LOCATION) {
			if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
				Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
				startActivity(intent);
				Toast.makeText(this,"Location permission allowed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this,"Location permission not allowed", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(MapsActivity.this, MainActivity.class);
				startActivity(intent);
			}
		}
	}

	public boolean checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
			return false;
		} else {
			return true;
		}
	}

	private GeofencingRequest getGeofencingRequest() {
		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
		builder.addGeofences(mGeofenceList);
		return builder.build();
	}

	private PendingIntent getGeofencePendingIntent() {
		// Reuse the PendingIntent if we already have it.
		if (geofencePendintIntent != null) {
			return geofencePendintIntent;
		}
		Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
		// calling addGeofences() and removeGeofences().
		geofencePendintIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
				FLAG_UPDATE_CURRENT);
		return geofencePendintIntent;
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		getAllShops();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("OnLocationChanged", "onLocationChanged [" + location + "]");
		writeActualLocation(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	private void getAllShops() {
		if (shopList.isEmpty()) {
			Toast.makeText(this, "No shops on the map", Toast.LENGTH_LONG).show();
		} else {
			for (int i = 0; i < shopList.size(); i++) {
				double longtitude = Double.parseDouble(shopList.get(i).getLongitude());
				double latitude = Double.parseDouble(shopList.get(i).getLatitude());
				Float radius = Float.valueOf(shopList.get(i).getRange());

				Log.d(TAG, "long: " + longtitude + " lat: " + latitude);

				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(new LatLng(latitude, longtitude));
				markerOptions.title(shopList.get(i).getName());
				markerOptions.snippet(shopList.get(i).getDescription());

				Marker shopMarker = mMap.addMarker(markerOptions);
				shopMarker.setTag(shopList.get(i).getName());
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtitude), 1));
			}
			drawGeofences();
			addGeofencesNewOne();
		}
	}

	private void writeActualLocation(Location location) {
		markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
	}

	private Marker locationMarker;

	private void markerLocation(LatLng latLng) {
		Log.i("onMarkerChanged", "markerLocation(" + latLng + ")");
		String title = latLng.latitude + ", " + latLng.longitude;
		MarkerOptions markerOptions = new MarkerOptions()
				.position(latLng)
				.title(title);

		if (locationMarker != null)
			locationMarker.remove();
		locationMarker = mMap.addMarker(markerOptions);
		float zoom = 7f;
		if (i < 3) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
			mMap.animateCamera(cameraUpdate);
		}
		i++;
	}

	private void drawGeofences() {
		Log.d(TAG, "drawGeofence()");

		if (geoFenceLimits != null)
			geoFenceLimits.remove();

		for (int i = 0; i < shopList.size(); i++) {
			double longtitude = Double.parseDouble(shopList.get(i).getLongitude());
			double latitude = Double.parseDouble(shopList.get(i).getLatitude());
			Float radius = Float.valueOf(shopList.get(i).getRange());

			CircleOptions circleOptions = new CircleOptions()
					.center(new LatLng(latitude, longtitude))
					.strokeColor(Color.argb(50, 70, 70, 70))
					.fillColor(Color.argb(100, 150, 150, 150))
					.radius(radius);
			geoFenceLimits = mMap.addCircle(circleOptions);
		}
	}

	private void addGeofencesNewOne() {
		double longtitude;
		double latitude;
		int radius;

		for (int i = 0; i < shopList.size(); i++) {
			longtitude = Double.parseDouble(shopList.get(i).getLongitude());
			latitude = Double.parseDouble(shopList.get(i).getLatitude());
			radius = Integer.valueOf(shopList.get(i).getRange());
			String id = shopList.get(i).getName();

			LatLng currentLatLng = new LatLng(latitude, longtitude);

			mGeofenceList.add(new Geofence.Builder()
					.setRequestId(id)
					.setCircularRegion(currentLatLng.latitude, currentLatLng.longitude, radius)
					.setExpirationDuration(2000)
					.setLoiteringDelay(1)
					.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
							Geofence.GEOFENCE_TRANSITION_EXIT)
					.build());
		}

		if(checkLocationPermission()) {
			gc.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
					.addOnSuccessListener(new OnSuccessListener<Void>() {
						@Override
						public void onSuccess(Void aVoid) {
							for (int i = 0; i < getGeofencingRequest().getGeofences().size(); i++) {
								Log.d(TAG, "Added Geofence! " + getGeofencingRequest().getGeofences().get(i).getRequestId());
							}
						}
					})
					.addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							Log.d(TAG, "Failed!" + e.getMessage().toString());
						}
					});
		}
	}
}







