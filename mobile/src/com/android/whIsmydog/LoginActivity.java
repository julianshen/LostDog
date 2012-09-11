package com.android.whIsmydog;

import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;
import com.facebook.android.Utility;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LoginActivity extends Activity implements Constants {
	TextView mText;
	LoginButton mLoginButton;
	private static final int AUTHORIZE_ACTIVITY_RESULT_CODE = 101;
	private static final String permissions[] = new String[] {
			"publish_actions", "publish_stream" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mText = (TextView) findViewById(R.id.txt);
		mLoginButton = (LoginButton) findViewById(R.id.login);
		Utility.mFacebook = new Facebook(APP_ID);
		// Instantiate the asynrunner object for asynchronous api calls.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		// restore session if one exists
		SessionStore.restore(Utility.mFacebook, this);
		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());
		mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE,
				Utility.mFacebook, permissions);

		if (Utility.mFacebook.isSessionValid()) {
			requestUserData();
		}
	}

	private static final String TAG = "LoginActivity";

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode + " resultCode "
				+ resultCode);
		switch (requestCode) {
		/*
		 * if this is the activity result from authorization flow, do a call
		 * back to authorizeCallback Source Tag: login_tag
		 */
		case AUTHORIZE_ACTIVITY_RESULT_CODE: {
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
			Log.d(TAG, "success");
			break;
		}
		}
	}

	private void requestUserData() {
		boolean redirect = getIntent().getBooleanExtra("redirect", true);
		if (redirect) {
			Intent mainActivity = new Intent(this,  PetListActivity.class);
			startActivity(mainActivity);
		}
	}

	public class FbAPIsAuthListener implements AuthListener {

		@Override
		public void onAuthSucceed() {
			mText.setText("You have logged In!");
			requestUserData();
		}

		@Override
		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
		}
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */
	public class FbAPIsLogoutListener implements LogoutListener {
		@Override
		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		@Override
		public void onLogoutFinish() {
			mText.setText("You have logged out! ");
		}
	}

}
