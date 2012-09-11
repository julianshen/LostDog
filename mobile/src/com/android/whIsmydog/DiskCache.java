package com.android.whIsmydog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class DiskCache {
	File mDirectory;
	private static final String TAG = "DiskCache";

	public static DiskCache openCache(File cacheDir) {
		DiskCache cache = new DiskCache(cacheDir);
		return cache;
	}

	public DiskCache(File dir) {
		mDirectory = dir;
	}

	private static final String CACHE_FILENAME_PREFIX = "image";

	public static String createFilePath(File cacheDir, String key) {
		try {
			// Use URLEncoder to ensure we have a valid filename, a tad hacky
			// but it will do for
			// this example
			return cacheDir.getAbsolutePath() + File.separator
					+ CACHE_FILENAME_PREFIX
					+ URLEncoder.encode(key.replace("*", ""), "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			Log.e(TAG, "createFilePath - " + e);
		}

		return null;
	}

	public Bitmap get(String key, int targetW, int targetH) {
		BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		String file = createFilePath(mDirectory, key);
		if (new File(file).exists()) {
			BitmapFactory.decodeFile(file, options);
			if (options.outWidth > targetW && options.outHeight > targetH) {
				int sampleSize = Math.min(options.outWidth / targetW,
						options.outHeight / targetH);
				options.inJustDecodeBounds = false;
				options.inSampleSize = sampleSize;
				Bitmap src = BitmapFactory.decodeFile(file, options);
				Bitmap target = resizeAndCrop(src, targetW, targetH);
				return target;
			} else {
				return BitmapFactory.decodeFile(file);
			}
		}
		return null;

	}

	public static Bitmap resizeAndCrop(Bitmap source, int newWidth,
			int newHeight) {
		Matrix matrix = new Matrix();
		int originalWidth = source.getWidth();
		int originalHeight = source.getHeight();
		float scale = Math.max((float) newWidth / originalWidth,
				(float) newHeight / originalHeight);
		matrix.setScale(scale, scale);
		// RectF rect = new RectF(0, 0, originalWidth, originalHeight);
		// matrix.mapRect(rect);
		Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight,
				source.getConfig());
		Canvas canvas = new Canvas();
		canvas.concat(matrix);
		canvas.setBitmap(bitmap);
		bitmap.setDensity(source.getDensity());
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		if (!matrix.rectStaysRect()) {
			paint.setAntiAlias(true);
		}

		int tmpWidth = Math.round(newWidth / scale);
		int tmpHeight = Math.round(newHeight / scale);
		int marginLeft = (originalWidth - tmpWidth) / 2;
		int marginTop = (originalHeight - tmpHeight) / 2;
		Rect srcBound = new Rect(marginLeft, marginTop, originalWidth
				- marginLeft, originalHeight - marginTop);
		Log.d(TAG, "marginLeft " + marginLeft + " marginTop " + marginTop);
		RectF targetBound = new RectF(0, 0, tmpWidth, tmpHeight);
		canvas.drawBitmap(source, srcBound, targetBound, paint);
		source.recycle();
		return bitmap;
	}

	public void put(String key, Bitmap data) {

		try {
			final String file = createFilePath(mDirectory, key);
			Log.d(TAG, "write bitmap ");
			if (writeBitmapToFile(data, file)) {
			}
		} catch (final FileNotFoundException e) {
			Log.e(TAG, "Error in put: " + e.getMessage());
		} catch (final IOException e) {
			Log.e(TAG, "Error in put: " + e.getMessage());
		}

	}

	private CompressFormat mCompressFormat = CompressFormat.JPEG;
	private int mCompressQuality = 70;
	public static final int IO_BUFFER_SIZE = 8 * 1024;

	private boolean writeBitmapToFile(Bitmap bitmap, String file)
			throws IOException, FileNotFoundException {

		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file),
					IO_BUFFER_SIZE);
			return bitmap.compress(mCompressFormat, mCompressQuality, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}
