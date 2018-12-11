package com.ktc.tvremote.client.utils;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	private HttpClient client;
	private HttpPost post;
	private HttpResponse response;

	public HttpClientUtil() {
		client = new DefaultHttpClient();
	}

	public String sendGet(String url) {
		HttpGet get = new HttpGet(url);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 8000);
		HttpConnectionParams.setSoTimeout(httpParams, 8000);
		get.setParams(httpParams);
		try {
			response = client.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public InputStream loadImg(String url) {
		System.out.println(url);
		HttpGet get = new HttpGet(url);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 8000);
		HttpConnectionParams.setSoTimeout(httpParams, 8000);
		get.setParams(httpParams);

		try {
			response = client.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				return response.getEntity().getContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
