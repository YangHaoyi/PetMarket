package com.lbt.petmarket.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.lbt.petmarket.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CommonFunction {

	public static void initApplicationConfig(Activity activity) {

		if (AppController.getApp().getAdaptation() == null) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			AdaptationClass cpc = new AdaptationClass(dm.widthPixels,
					dm.heightPixels, dm.density);
			AppController.getApp().setAdaptation(cpc);

		}
		
		// if(ParentsApplication.getInstance().getPreference() == null){
		// SharedPreferences userData =
		// activity.getSharedPreferences("userdata",0);
		// ParentsApplication.getInstance().setPreference(userData);
		// }
	}
	//适配类
	public static int getZoomX(int length) {


		return AppController.getApp().getAdaptation().getZoomX(length);

	}

	//赞，取消赞等简单的网络请求的回调
	public interface SimpleRequestListener{
		void onSuccess();          //请求成功
		void onFail(int code, String msg);            //请求数据失败时调用
		void onNetWorkError();    //联网失败调用
		void onCommonBehavior();  //无论发生了什么，都会调用该接口
	}


	public static HashMap getParamsWithUserInfo(Context context){
		HashMap<String,String> params = new HashMap<String,String>();
//		params.put(Constant.USER_ID, PreferencesUtils.getString(context, Constant.USER_ID));
//		params.put(Constant.TOKEN, PreferencesUtils.getString(context, Constant.TOKEN));

		return params;
	}
	public static int getCurrVersionCode(Context context) {
		String packageName = context.getPackageName();
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(packageName,
					PackageManager.PERMISSION_GRANTED);
			return pi.versionCode;

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static HashMap<String,String> getAuthMap(Context context,String url){

		int index = Constant.NEW_SERVER_ROOT.length()-1;


		if(index<0){
			index = 0;
		}

		final String subUrl = url.substring(index);

		HashMap hashmap = new HashMap<String,String>();

		String macAndImei = PhoneUtils.getImei(context)+ PhoneUtils.getMac(context)+ PreferencesUtils.getPreferences(context, PreferencesUtils.J_USER_ID);
		if(macAndImei==null){
			macAndImei ="";
		}


		String M = "A"+ String.valueOf(System.currentTimeMillis())+
				com.android.volley.toolbox.MD5Util.MD5(macAndImei + String.valueOf((int) ((Math.random() * 9 + 1) * 100000)));


		hashmap.put("m",M);


		long t = System.currentTimeMillis() ;
		hashmap.put("t", t + "");

		hashmap.put("puk", Constant.AUTH_PUB_KEY);
		hashmap.put("s", hmacSha256(M +
				subUrl + Constant.AUTH_PRIVATE_KEY, t + ""));


		//  LogUtil.d("auth " + hashmap.toString());

		return hashmap;
	}

	public static String hmacSha256(String value, String key) {
		try {
			// Get an hmac_sha256 key from the raw key bytes
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

			// Get an hmac_sha256 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);

			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(value.getBytes());

			// Convert raw bytes to Hex
			String hexBytes = bytesToHexString(rawHmac);

			return hexBytes;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
		if(src==null||src.length<=0){
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static String getShareAlbumDetailTitle(Context context,String username){
//		if(!TextUtils.isEmpty(username)){
//			return "『"+username+"』"+context.getResources().getString(R.string.title_tail);
//		}else{
//			return "『"+ PreferencesUtils.getString(context, PreferencesUtils.USER_NAME)+"』"
//					+context.getResources().getString(R.string.title_tail);
//		}
//        else{
//			return mWord;
		return "";
//		}
	}

	public static void sendSimpleRequest(final Context context,final String url,String key,String value, final SimpleRequestListener listener){
		//LogUtil.d("sendSimpleRequest url is " + url);
		final HashMap<String,String> params = getParamsWithUserInfo(context);
		if(!TextUtils.isEmpty(key)){
			params.put(key,value);
		}
		StringRequest strRequest = new StringRequest(Request.Method.POST,
				url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				if(listener!=null){
					listener.onCommonBehavior();
				}
				//   LogUtil.d("SimpleRequest is " + s);
				try {
					JSONObject jo = new JSONObject(s);
					int code = jo.getInt(Constant.STATUS);

					if(code== Constant.API_SUCCESS){
						if(listener!=null){
							listener.onSuccess();
						}

					}else{
						String msg=null;
						if(jo.has(Constant.MSG)){
							msg = jo.getString(Constant.MSG);
						}
						if(listener!=null){
							listener.onFail(code,msg);
						}

					}
				} catch (JSONException e) {
					CommonFunction.sendSimpleRequest(context, Constant.API_ERROR_ADD, Constant.CONTENT, s, null);
					e.printStackTrace();
				}
			}
		},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						if(listener!=null){
							listener.onCommonBehavior();
							listener.onNetWorkError();
						}

					}
				}) {
			public Map<String, String> getHeaders() throws AuthFailureError {
				return CommonFunction.getAuthMap(context, url);
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// LogUtil.d("Params is " + params.toString());
				return params;
			}

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				try {
					String dataString = new String(response.data, "UTF-8");
					return Response.success(dataString,
							HttpHeaderParser.parseCacheHeaders(response));
				} catch (UnsupportedEncodingException e) {
					return Response.error(new ParseError(e));
				}
			}

		};
		AppController.getApp().addToRequestQueue(strRequest, null);
	}
}
