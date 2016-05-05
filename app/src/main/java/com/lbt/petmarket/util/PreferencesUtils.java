package com.lbt.petmarket.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * preference读写工具
 * 
 */
public class PreferencesUtils {
	 
	public static final String PREFERENCES_NAME = "info";
	public static final String IS_LOADED_LOCAL_CONFIG = "is_loaded_local_config";
	public static final String PUBLISH_PET_ID = "publish_pet_id";
	public static final String AUTH_KEY = "auth_key";
	public static final String AUTH_NUM = "auth_num";
    public static final String IS_LOGIN = "is_login";
    public static final String HISTORY = "history_record";
    public static final String USER_NAME_CACHE = "user_name_cache";
    public static final String IS_REVEIVE_PUSH = "IS_REVEIVE_PUSH";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String OPEN_ID = "open_id";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ADDRESS_EMPTY = "address_empty";//用户是否拥有地址信息
	public static final String UPDATE_TIME = "update_time";
    
    public static final String HAS_ENTER_MAIN = "has_enter_main";  //是否第一次进入首页
    public static final String HAS_ENTER_ADD = "has_enter_add";  //是否第一次进入添加宠物页
	public static final String MESSAGE_COUNT = "message_count";  //是否第一次进入添加宠物页

    public static final String IS_SHOW_UPDATE_DIAGLOG = "is_show_update_dialog"; //是否显示非强制更新
    public static final String IS_OPEN_PUSH_MSG = "is_open_push_msg"; //是否开启推送
    public static final String IS_NEED_UPDATE = "is_need_update"; //是否需要升级
    
    public static final String USER_AGENT ="user_agent";
    public static final String USER_NAME ="user_name";
    public static final String USER_PWD = "user_pwd";
	public static final String USER_GENDER = "user_gender";
	public static final String USER_AVATAR = "user_avatar";
	public static final String USER_PROVINCE = "user_province";
	public static final String USER_CITY = "user_city";
    public static final String LOCATION = "location";

    
    public static final String IS_HAS_OPEN = "is_has_open";                //是否已经打开过
    public static final String IS_HAS_SUB = "is_has_sub";          //是否第一次进入订阅

	public static final String IS_HAS_TIP_FOLLOW = "is_has_tip_follow";
	public static final String IS_HAS_TIP_PRAISE = "is_has_tip_praise";
	public static final String IS_HAS_TIP_LIKE = "is_has_tip_like";


	public static final String FIRST_LAT = "first_lat";
	public static final String FIRST_LON = "first_lon";

	public static final String USER_ID  = "user_id";        //用户id
    public static final String LATITUDE  = "latitude";
    public static final String LONGITUDE  = "longitude";
	public static final String AD_LIST  = "ad_list";        //用户id
    
    public static final String J_USER_ID = "j_user_id";  // 极光推送id
    public static final String B_USER_ID = "b_user_id";  // 百度推送id
    public static final String SESSION_ID = "session_id";   //用户id
	public static final String FONT_ID = "font_id";   //用户id
	public static final String EDIT_HISTORY_MENU_NAME = "edit_history_menu_name";   //用户id
    
    public static final String QUESTION_CONTENT = "qustion_content";   //用户id
    public static final String QUESTION_SUPPLEMENT = "qustion_supplement";   //用户id
    public static final String QUESTION_PIC_URL = "qustion_pic_url";   //用户id
    
    public static final String MESSAGE_RECORD = "message_record";

	public static final String IS_CROP_OPENED = "is_crop_opened";//是否进入过剪切图片页面
	public static final String IS_RECORD_OPENED = "is_record_opened"; //是否进入过录音
	public static final String IS_PUBLISH_OPENED = "is_crop_opened";//是否发布过，默认第三方为true

	public static final String TOKEN = "token";   //token

	public static final String PUBLISH_SHARE_TO_QQ_ZONE = "publish_share_to_qq_zone";   //qq
	public static final String PUBLISH_SHARE_TO_WX = "publish_share_to_wx";   //wx
	public static final String PUBLISH_SHARE_TO_SINA = "publish_share_to_sina";   //sina

	public static final String GRAFFITO_UPDATE_TIME = "graffito_update_time";   //sina
	public static final String GRAFFITO_LAST_UPDATE_TIME = "graffito_last_update_time";   //sina
	public static final String CHARTLETS_CLICK_HISTORY = "chartlets_click_history";
	public static final String FINISH_DOWNLOAD_TEXT = "finish_download_text";

	public static final String IS_HAS_EDIT = "is_has_edit";
	
	public static String getPreferences(Context context,String name){

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		return preferences.getString(name, "");
	}
	public static void setPreferences(Context context,String name,String value){
		if(context!=null&&value!=null){
			Editor sharedata = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
			sharedata.putString(name, value);
			sharedata.commit();
		}
	}
	public static int getIntPreferences(Context context,String name){
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		return preferences.getInt(name, 0);
	}
	public static void setIntPreferences(Context context,String name,int value){
		Editor sharedata = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		sharedata.putInt(name, value);
		sharedata.commit();
	}

	public static boolean getBooleanPreferences(Context context,String name){
		 if(context==null){
             return false;
         }
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(name, false);
	}
	public static void setBooleanPreferences(Context context,String name,boolean value){
	 if(context!=null){
			Editor sharedata = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
			sharedata.putBoolean(name, value);
			sharedata.commit();
	 }
	
	}
	
	public static long getLongPreferences(Context context,String name){
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		return preferences.getLong(name, 0L);
	}
	public static void setLongPreferences(Context context,String name,long value){
		Editor sharedata = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		sharedata.putLong(name, value);
		sharedata.commit();
	}
	//清空数据
	public static void reset(Context context){
		Editor sharedata = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		sharedata.clear().commit();
	}
}
