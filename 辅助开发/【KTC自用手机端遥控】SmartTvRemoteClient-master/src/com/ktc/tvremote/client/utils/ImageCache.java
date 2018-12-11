package com.ktc.tvremote.client.utils;


import com.ktc.tvremote.client.bean.GlobalParams;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageCache {
	private static final int MAXSIZE = 1024 * 1024 * 5;
	protected static final String TAG = "ImageCache";
	private static ImageCache cache = new ImageCache();

	public static ImageCache getInstance() {
		return cache;
	}

	private LruCache<Object, Bitmap> lrucache;

	private ImageCache() {

		lrucache = new LruCache<Object, Bitmap>(MAXSIZE) {

			@Override
			protected int sizeOf(Object key, Bitmap value) {
				return getSize(value);
			}

			@Override
			protected void entryRemoved(boolean evicted, Object key,
					Bitmap oldValue, Bitmap newValue) {
				if (evicted) {
					 GlobalParams.IMGCACHE.put(key.toString(), oldValue);
			//		WorldObject.SOFTCACHE.put(key.toString(), oldValue);
				}
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

		};
	}

	private int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	public void put(Object key, Bitmap value) {
		lrucache.put(key, value);
	}

	public Bitmap get(Object key) {
		Bitmap bitmap = lrucache.get(key);
		if (bitmap == null) {
			if (GlobalParams.IMGCACHE != null) {
				bitmap = GlobalParams.IMGCACHE.get(key.toString());
			}
		}

		return bitmap;
	}

	public void clear() {
		lrucache.evictAll();
		if (GlobalParams.IMGCACHE != null) {
			GlobalParams.IMGCACHE.clear();
		}
	}
}
