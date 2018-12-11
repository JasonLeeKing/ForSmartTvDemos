package com.ktc.tvremote.client.utils;

import android.content.Context;
import java.util.Locale;


/**
 * @TODO 工具类
 * @author Arvin
 * @since 2018.6.5
 */
public class Tools {
	
	private static final boolean LOG = true;
	
	public static boolean isInChina (Context context) {
		return getCurrentCountry(context).equals(Locale.CHINA);
	}
	
	public static Locale getCurrentCountry (Context context) {
		return context.getResources().getConfiguration().locale;
	}
}
