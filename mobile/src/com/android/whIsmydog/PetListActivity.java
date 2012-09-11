package com.android.whIsmydog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.facebook.android.Utility;
import com.google.gson.Gson;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PetListActivity extends ListActivity {
	PetServer mServer;
	PetAdapter mAdapter;
	private static final String TAG = "PetListActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new PetAdapter((LostDogApp) getApplication());
		setListAdapter(mAdapter);
		mServer = new PetServer();
		getActionBar().setTitle("Find Pet");
		new AsyncTask<Void, Void, Pet[]>() {

			@Override
			protected Pet[] doInBackground(Void... arg0) {
				try {
					String results = mServer.request("/post/list", null, "GET");
					Log.d(TAG, "result " + results);
					Gson gson = new Gson();
					PetPosts posts = gson.fromJson(results, PetPosts.class);
					return posts.posts;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			protected void onPostExecute(Pet[] result) {
				mAdapter.updateData(result);
			}

		}.execute();
	}

	public static class PetAdapter extends BaseAdapter {
		Pet[] mPets;
		LayoutInflater mInflater;
		ImageRequester mRequester;
		int picSize;

		public PetAdapter(LostDogApp app) {
			mInflater = LayoutInflater.from(app.getApplicationContext());
			mRequester = new ImageRequester(app);
			picSize = app.getApplicationContext().getResources()
					.getDimensionPixelSize(R.dimen.pic_size);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPets == null ? 0 : mPets.length;
		}

		@Override
		public Pet getItem(int position) {
			return mPets[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.pet_item, parent,
						false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			Pet pet = getItem(position);
			holder.name.setText(pet.name);
			holder.breed.setText(pet.breed);
			// pet.photos = new String[] { "10150979413107796" };
			if (pet.photos != null && pet.photos.length > 0) {
				String url = " https://graph.facebook.com/" + pet.photos[0]
						+ "/picture&access_token="
						+ Utility.mFacebook.getAccessToken();

				mRequester.submit(holder.img, url, picSize, picSize);
			}
			return convertView;
		}

		public void updateData(Pet pets[]) {
			mPets = pets;
			notifyDataSetChanged();
		}

		public static class ViewHolder {
			ImageView img;
			TextView name;
			TextView breed;

			public ViewHolder(View convertView) {
				img = (ImageView) convertView.findViewById(R.id.pic);
				name = (TextView) convertView.findViewById(R.id.name);
				breed = (TextView) convertView.findViewById(R.id.breed);
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Pet pet = (Pet) l.getItemAtPosition(position);
		Intent main = new Intent(this, MainActivity.class);
		main.putExtra("pet", pet);
		startActivity(main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			return true;
		}
		return false;
	}
}
