package com.android.whIsmydog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity implements Constants {
	MapView mMapView;
	MapController mController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setSatellite(false); // Set satellite view
		mController = mMapView.getController();
		mController.setZoom(18);
		LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		MyLocationListener listener = new MyLocationListener();

		myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, listener);

		// Get the current location in start-up
		GeoPoint initGeoPoint = new GeoPoint((int) (myLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				.getLatitude() * 1000000), (int) (myLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				.getLongitude() * 1000000));
		mController.animateTo(initGeoPoint);
		Drawable drawable = getResources()
				.getDrawable(R.drawable.androidmarker);
		HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(
				drawable, this);
		OverlayItem overlayitem = new OverlayItem(initGeoPoint, "Hola, Mundo!",
				"I'm in Mexico City!");
		itemizedoverlay.addOverlay(overlayitem);
		List<Overlay> mOverlays = mMapView.getOverlays();
		mOverlays.add(itemizedoverlay);
	}

	public static class HelloItemizedOverlay extends ItemizedOverlay {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		Context mContext;

		public HelloItemizedOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
		}

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}
	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location argLocation) {
			// TODO Auto-generated method stub
			GeoPoint myGeoPoint = new GeoPoint(
					(int) (argLocation.getLatitude() * 1000000),
					(int) (argLocation.getLongitude() * 1000000));

			mController.animateTo(myGeoPoint);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("redirect", false);
			startActivity(intent);
			return true;
		}
		return false;
	}

}
