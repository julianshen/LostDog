package com.android.whIsmydog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.android.Utility;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;

public class MainActivity extends MapActivity implements Constants {
	MapView mMapView;
	MapController mController;
	Pet mPet;
	private static final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setSatellite(false); // Set satellite view
		mController = mMapView.getController();
		mController.setZoom(18);
		mPet = getIntent().getParcelableExtra("pet");
		getActionBar().setTitle("Looking for a " + mPet.species);
		if (mPet == null) {
			LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			MyLocationListener listener = new MyLocationListener();

			myLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, listener);

			// Get the current location in start-up
			GeoPoint initGeoPoint = new GeoPoint((int) (myLocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER)
					.getLatitude() * 1000000), (int) (myLocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER)
					.getLongitude() * 1000000));
			Log.d(TAG, "initGeoPoint" + initGeoPoint.getLatitudeE6() + " "
					+ initGeoPoint.getLongitudeE6());

			moveTo(initGeoPoint);
		} else {

			TextView name = (TextView) findViewById(R.id.name);
			TextView gender = (TextView) findViewById(R.id.gender);
			name.setText(mPet.name + "(" + mPet.breed + ")");
			gender.setText("Gender:" + mPet.gender);
			TextView found = (TextView) findViewById(R.id.found);
			found.setText(mPet.founded ? "Found" : "Not found");
			found.setTextColor(mPet.founded ? Color.BLUE : Color.RED);
			// 25035552 121569432
			// mPet.where.lat = 25.035552;
			// mPet.where.lng = 121.569432;
			Log.d(TAG, "where " + mPet.where.lat + " " + mPet.where.lon);
			GeoPoint point = new GeoPoint((int) (mPet.where.lat * 1000000),
					(int) (mPet.where.lon * 1000000));
			moveTo(point);
			final int picSize = getResources().getDimensionPixelSize(
					R.dimen.pic_size);
			final ImageRequester requester = new ImageRequester(
					(LostDogApp) getApplication());
			ImageView img = (ImageView) findViewById(R.id.pet_pic);
			if (mPet.photos != null && mPet.photos.length > 0) {
				String url = " https://graph.facebook.com/" + mPet.photos[0]
						+ "/picture&access_token="
						+ Utility.mFacebook.getAccessToken();
				requester.submit(img, url, picSize, picSize);
			}

			// mPet.owner = "827463733";
			new AsyncTask<String, Void, User>() {

				@Override
				protected User doInBackground(String... params) {
					try {
						Bundle extras = new Bundle();
						extras.putString("fields", "id,name");
						String user = Utility.mFacebook.request(params[0],
								extras);
						Gson gson = new Gson();
						User fbUser = gson.fromJson(user, User.class);
						return fbUser;
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(User result) {
					TextView owner = (TextView) findViewById(R.id.owner);
					ImageView owner_pic = (ImageView) findViewById(R.id.owner_pic);
					owner.setText("Posted by: " + result.name);
					String url = " https://graph.facebook.com/" + result.id
							+ "/picture&access_token="
							+ Utility.mFacebook.getAccessToken();
					requester.submit(owner_pic, url, picSize, picSize);
				}

			}.execute(mPet.owner);
		}
	}

	public void moveTo(GeoPoint point) {
		mController.animateTo(point);
		Drawable drawable = getResources()
				.getDrawable(R.drawable.androidmarker);
		HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(
				drawable, this);
		OverlayItem overlayitem = new OverlayItem(point, "Hello there",
				"Please find my dong!");
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
