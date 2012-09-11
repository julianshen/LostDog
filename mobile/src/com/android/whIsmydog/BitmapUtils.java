package com.android.whIsmydog;

import android.graphics.Bitmap;

public class BitmapUtils {
	public static final int getBitmapByteCount(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}
