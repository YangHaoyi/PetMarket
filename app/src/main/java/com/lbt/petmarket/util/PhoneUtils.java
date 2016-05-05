package com.lbt.petmarket.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.lbt.petmarket.customView.MyToast;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtils {

	// static Logger logger=Logger.qLog();

	public static String getLocalIpAddress() {
		String ipaddress = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ipaddress = ipaddress + ";"
								+ inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			//Log.e("WifiPreference IpAddress", ex.toString());
		}
		return ipaddress;
	}

	public static void open(Context mContext, String packageName) {
		try {
			Intent intent = mContext.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断指定包名的进程是否运行
	 * 
	 * @param context
	 * @param packageName
	 *            指定包名
	 * @return 是否运行
	 */
	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo rapi : infos) {
			if (rapi.processName.equals(packageName))
				return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static <IClipboard> void copy(String copy, Context context) {
		if (Integer.parseInt(Build.VERSION.SDK) >= 11) {
			android.content.ClipboardManager clip = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			// clip.getText(); //
			clip.setText(copy); //
		} else {
			android.text.ClipboardManager clip = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			// clip.getText(); //
			clip.setText(copy); //
		}

	}

//	public static boolean isMobile(String mobiles) {
//		Pattern p = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//		Matcher m = p.matcher(mobiles);
//		System.out.println(m.matches() + "---");
//		// return m.matches();
//		if (mobiles.length() != 11) {
//			return false;
//		} else {
//			return true;
//		}
//	}

	public static String nowDataString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date(System.currentTimeMillis()));
		return date;
	}

	//判断当前设备是否是模拟器。如果返回TRUE，则当前是模拟器，不是返回FALSE
    public static boolean isEmulator(Context context){
        try{
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
           String deviceId =  tm.getDeviceId();
            // 如果 运行的 是一个 模拟器
            if (deviceId != null&&deviceId.matches("0+"))  {
            	return true;
            }
            //lyl modify at 20141015
//            String imei = tm.getDeviceId();
//            if (imei != null && imei.equals("000000000000000")){
//                return true;
//            }
            return  (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
        }catch (Exception ioe) {

        }
        return false;
    }
	
//	public static boolean checkQQ(String value) {
//		if (value.matches("[1-9][0-9]{4,13}")) {
//			return true;
//		} else if (checkEmail(value)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public static boolean checkEmail(String email) {
//		boolean flag = false;
//		try {
//			String check = "^锛圼a-z0-9A-Z]+[-|\\.]?锛?[a-z0-9A-Z]@锛圼a-z0-9A-Z]+锛?[a-z0-9A-Z]+锛夛紵\\.锛?[a-zA-Z]{2,}$";
//			Pattern regex = Pattern.compile(check);
//			Matcher matcher = regex.matcher(email);
//			flag = matcher.matches();
//		} catch (Exception e) {
//			flag = false;
//		}
//		return flag;
//	}

	public static boolean checkNick(String value) {
		boolean flag = false;
		try {
			if (value.length() < 6) {
				return false;
			}
			String check = "[a-zA-Z0-9\\u4E00-\\u9FA5_]{1,10}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(value);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static void vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void vibrate(final Activity activity, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

	public static String getMac(Context mContext) {
		boolean flag = false;
		flag = getLocalIpAddress2(mContext);
		Log.i("getMac", "wifi:" + flag);
		WifiManager wifi = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info.getMacAddress() == null) {
			return "";
		}
		String mac = info.getMacAddress().replace(":", "");
		if (flag) {
			WifiManager wifiManager = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);
		}
		return mac;
	}

	public static String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI = telephonyManager.getDeviceId();
		if(TextUtils.isEmpty(IMEI)||IMEI.equals("unknown")||IMEI.contains("00000")){
			return "";
		}
		return IMEI;
	}

	public static boolean getLocalIpAddress2(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
			return true;
		}
		// WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// int ipAddress = wifiInfo.getIpAddress();

		// String ip = intToIp(ipAddress);
		return false;
	}

	public static void deleteApp(Context mContext, String appPackage) {
		Uri packageURI = Uri.parse("package:" + appPackage);
		// Log.i(TAG, "appInfos.get(arg2).get(arg3).getAppPackage():"
		// + appInfo.get(group).get(child).getAppPackage());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		mContext.startActivity(uninstallIntent);

	}

	public static void setupApk(Context mContext, String absolutePath) {
		boolean b = absolutePath.endsWith(".apk");
		if (b) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(absolutePath)),
					"application/vnd.android.package-archive");
			mContext.startActivity(intent);
		} else {
//			Toast.makeText(mContext, "未找到安装文件，请重新下载", Toast.LENGTH_SHORT)
//					.show();
			MyToast.showMsg(mContext, "未找到安装文件，请重新下载");
		}
	}

	public static void setupApk(Context mContext, File file) {
		if (file.exists() && file.isFile()) {
			boolean b = file.getName().endsWith(".apk");
			// logger.i(file.getAbsolutePath());
			if (b) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
				mContext.startActivity(intent);
			}
		} else {
//			Toast.makeText(mContext, "未找到安装文件，请重新下载", Toast.LENGTH_SHORT)
//					.show();
			MyToast.showMsg(mContext,  "未找到安装文件，请重新下载");
		}
	}

	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
			return packageInfo;
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
		}
		return null;
		// ApplicationInfo.FLAG_SYSTEM
	}

	// 将字符串转为时间戳
	public static String getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		try {
			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return re_time;
	}

	// yyyy年MM月dd日HH时mm分ss秒
	// 将时间戳转为字符串
	public static String getStrTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	public static String getAllTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd天HH小时mm分ss秒");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	public static String getAllOtherTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	public static String formatDuring(long mss) {
		mss = mss * 1000;
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		return days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
	}

	public static String formatDuringDay(long mss) {
		mss = mss * 1000;
		
		long days = mss / (1000 * 60 * 60 * 24);
		//lyl modify at 20141016 begin
		days++;
		//lyl modify at 20141016 end
		return days + "天";
	}

	public static String getAllOtherTime2(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	public static long getMonth() {
		Scanner scanner = new Scanner(System.in);
		// System.out.print("Please input the date(yyyy-MM): ");

		String input = scanner.nextLine();

		if (!input.matches("\\d{4}-\\d{2}")) {
			// System.out.println("Error input in format, exit!");
			System.exit(0);
		}

		int count = 0;

		int month = Integer.parseInt(input.substring(5, 7));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(input.substring(0, 4)));
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, 1);

		while (cal.get(Calendar.MONTH) < month) {
			int day = cal.get(Calendar.DAY_OF_WEEK);

			if (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
				count++;
			}

			cal.add(Calendar.DATE, 1);
		}
		return count;
	}
	public static boolean isMobileNO(String mobiles){
		  
		//Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
		Pattern p = Pattern.compile("^1\\d{10}");
		Matcher m = p.matcher(mobiles);

		///	System.out.println(m.matches()+"---"+mobiles);

		return m.matches();

	}  
}
