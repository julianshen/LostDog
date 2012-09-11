package com.android.whIsmydog;

import android.app.Application;
import android.os.Handler;

public class LostDogApp extends Application {
	CacheManager cacheManager;
	ThreadPool mThreadPool;
	Handler uiHandler;

	public CacheManager getCacheManager() {
		if (cacheManager == null) {
			cacheManager = new CacheManager(this.getApplicationContext());

		}
		return cacheManager;
	}

	public ThreadPool getThreadPool() {
		if (mThreadPool == null) {
			mThreadPool = new ThreadPool();
		}
		return mThreadPool;
	}

	public Handler getUIHandler() {
		if (uiHandler == null) {
			uiHandler = new Handler(getMainLooper());
		}
		return uiHandler;
	}
}
