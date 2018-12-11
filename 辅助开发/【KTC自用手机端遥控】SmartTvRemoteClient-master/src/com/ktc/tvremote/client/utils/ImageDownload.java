package com.ktc.tvremote.client.utils;
import java.io.InputStream;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageDownload extends AsyncTask<Object, Integer, Object> {
	private ImageCallBack imageCallBack;
	private String tag;

	public ImageDownload(ImageCallBack imageCallBack) {
		this.imageCallBack = imageCallBack;
	}

	@Override
	protected Object doInBackground(Object... params) {
		if (params[0] != null) {
			Bitmap bitmap = loadImageFromUrl(params[0].toString());
			if (bitmap != null) {
				tag = (String) params[1];
				ImageCache.getInstance().put(params[1], bitmap);
				return bitmap;
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		if (imageCallBack != null && result instanceof Bitmap)
			imageCallBack.imageLoaded((Bitmap) result, tag);
	}

	public static Bitmap loadImageFromUrl(String url) {
		InputStream i = null;
		try {
			HttpClientUtil util = new HttpClientUtil();
			i = util.loadImg(url);
			BitmapFactory.Options opt = new BitmapFactory.Options();
		//	opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			return BitmapFactory.decodeStream(i, null, opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
