package com.lbt.petmarket.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
//import com.bugtags.library.Bugtags;
import com.google.gson.Gson;
import com.lbt.petmarket.AppController;
import com.lbt.petmarket.R;
import com.lbt.petmarket.customView.CustomProgressDialog;
import com.lbt.petmarket.customView.MyToast;
import com.lbt.petmarket.util.CommonFunction;
import com.lbt.petmarket.util.Constant;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/28 0028.
 */


/**
 * Created by Administrator on 2015/5/5 0005.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<String>{
    private static final String TAG = "BaseActivity";

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    protected boolean mIsLoading; //是否加载中
    protected FrameLayout mFrameLoading;
    CustomProgressDialog dialog;  //加载对话框
    public JSONObject mResponse;
    public FrameLayout mFrameSorry;
    protected ImageView mIvSorry;
    public Gson mGson = new Gson();
    FrameLayout mContentContainer;


    protected TextView mTitle;
    ImageView mIvTopBack;
    protected RelativeLayout mTitleView;
    ImageView mIvEmptyTip;
    FrameLayout mFlEmptyContainer;
    TextView mTvConfirm;

//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        //用于适配
//        CommonFunction.initApplicationConfig(this);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(LayoutInflater.from(this).inflate(R.layout.activity_base_container, null, false));
        mContentContainer = (FrameLayout)findViewById(R.id.ll_content_container);
        //用于适配
        CommonFunction.initApplicationConfig(this);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, view.getLayoutParams());
    }

    @Override
    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
        View v =  LayoutInflater.from(this).inflate(layoutResID, mContentContainer, false);
        setContentView(v);
        //初始化view
        initView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
//        super.setContentView(view, params);
        mContentContainer.addView(view, 0, params);
    }

    @Override
    public void onClick(View v) {

    }

    protected void initView(){
        mTitleView = (RelativeLayout)findViewById(R.id.title_view);
        LinearLayout.LayoutParams llPara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                CommonFunction.getZoomX(92));
        mTitleView.setLayoutParams(llPara);
        mTitleView.setVisibility(View.GONE);
        mTitle = (TextView)findViewById(R.id.title);
        mIvTopBack = (ImageView)findViewById(R.id.iv_topback);
        mIvTopBack.setOnClickListener(this);
        //获得句柄，默认不可见,数据空提示
        mFlEmptyContainer = (FrameLayout)findViewById(R.id.frame_empty_tip);
        mIvEmptyTip = (ImageView)findViewById(R.id.iv_empty_tip);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTitleView.setVisibility(View.GONE);
        initLoading();
        initSorry();
    }

    // 嵌入页面中的加载图标，而不是加载对话框，展示
    public void initLoading() {
        mFrameLoading = (FrameLayout) findViewById(R.id.frame_loading);

        if (mFrameLoading != null) {
            mFrameLoading.setVisibility(View.VISIBLE);
        }
    }

    protected int getZoomX(int length) {
        return AppController.getApp().getAdaptation().getZoomX(length);
    }

    public void initSorry() {
        mFrameSorry = (FrameLayout)findViewById(R.id.frame_sorry);
        mIvSorry = (ImageView)findViewById(R.id.iv_sorry);
        if(mFrameSorry != null){
            mFrameSorry.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    onNetErrorClick();
                }
            });
        }
    }

    protected void showProgressDialog(){
        if (dialog == null) {
            dialog = new CustomProgressDialog(this);
        }

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void showProgressDialog(String content) {

//        if (dialog == null) {
        dialog = new CustomProgressDialog(this, content, R.anim.loading_rotate);
//        }

//        if (!dialog.isShowing()) {
        dialog.show();
//        }

    }

    public void dismissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.d("lyl", "onErrorResponse is " + volleyError.getMessage());
        Log.d("lyl", "networkResponse is " + volleyError.networkResponse);
        Log.d("lyl", "toString is " + volleyError.toString());

        dismissProgressDialog();
        hideLoading();
        showSorry();
    }

    @Override
    public void onResponse(String response) {

         Log.d("lyl", "response is " + response);
        hideLoading();
        try {
            // json解析统一处理
            mResponse = new JSONObject(response);

            if(mResponse.has(Constant.STATUS)){
                // 错误信息统一调用
                if (mResponse.getInt(Constant.STATUS) != Constant.API_SUCCESS) {
//                    if(mResponse.getInt(Constant.STATUS) == Constant.API_DATA_EMPTY){
//                        onDataEmpty();
//                    }else
                        if(mResponse.getInt(Constant.STATUS) == Constant.API_LOGIN_TIMEOUT){
//                        LoginDialog loginDialog = new LoginDialog(this);
//                        loginDialog.createDialog();
                    }else{
//                        MyToast.showMsg(this, mResponse.getString("msg"));
                        onFailed(mResponse.getInt(Constant.STATUS),mResponse.getString("msg"));
                    }
                } else {
                    if (mResponse.has(Constant.DATA)) {
                        onSuccess(mResponse.getJSONObject(Constant.DATA));
                    } else {
                        onSuccess(mResponse);
                    }

                }
            }
//            else{//旧版接口的status换成了code，用于兼容老版接口
//                //错误信息统一调用
//                String status = "status";
//                if(mResponse.getInt(status)!=100){
//                    if(mResponse.getInt(status) == Constant.API_DATA_EMPTY){
//                        onDataEmpty();
//                    }else if(mResponse.getInt(status) == Constant.API_LOGIN_TIMEOUT){
//                        //清除登陆状态
//                        PreferencesUtils.setBooleanPreferences(this, PreferencesUtils.IS_LOGIN, false);
//                        onFailed(mResponse.getInt(Constant.STATUS));
//                    }else{
//                        MyToast.showMsg(this, mResponse.getString("msg"));
//                        onFailed(mResponse.getInt(Constant.STATUS));
//                    }
//                }else{
//                    if(mResponse.has(Constant.DATA)){
//                        onSuccess(mResponse.getJSONObject(Constant.DATA));
//                    }else{
//                        onSuccess(mResponse);
//                    }
//                }
//            }

        } catch (JSONException e) {
            CommonFunction.sendSimpleRequest(BaseActivity.this, Constant.API_ERROR_ADD, Constant.CONTENT, response, null);
            e.printStackTrace();
        }
    }

    // 成功返回jsonobject data
    protected void onSuccess(JSONObject response) {
        hideLoading();
    }

    // 返回错误状态码
    protected void onFailed(int status,String msg) {
        MyToast.showMsg(this, msg);
        dismissProgressDialog();
        hideLoading();

    }

    protected void onDataEmpty(){
        hideLoading();
        showSorry();
    }

    public void showSorry(){
        if(mFrameSorry!=null){
            mFrameSorry.setVisibility(View.VISIBLE);
        }
    }

    public void hideSorry(){
        if(mFrameSorry!=null){
            mFrameSorry.setVisibility(View.GONE);
        }
    }


    // 嵌入页面中的加载图标，而不是加载对话框，隐藏
    public void hideLoading() {
        if (mFrameLoading != null) {
            mFrameLoading.setVisibility(View.GONE);
        }
    }

    public void showLoading(){
        if(mFrameLoading!=null){
            mFrameLoading.setVisibility(View.VISIBLE);
        }
    }

    // 添加用户cookie信息发送请求
    public void sendSessionHttpRequest(final String url,  Map params, String tag) {
        tag = (tag == null ? TAG : tag);
        hideSorry();
        //LogUtil.d("url is " + url);

        if (params == null) {
            params =new HashMap<String, String>();
        }

//        if(CommonFunction.isLogin(BaseActivity.this)){
//            params.put(Constant.USER_ID,
//                    PreferencesUtils.getPreferences(BaseActivity.this, PreferencesUtils.USER_ID));
//            params.put(Constant.TOKEN,
//                    PreferencesUtils.getPreferences(BaseActivity.this,PreferencesUtils.TOKEN));
//        }

        final Map paramTrue = params;

        StringRequest strRequest = new StringRequest(Request.Method.POST, url, this,
                this) {
            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap hashmap = new HashMap();
//                if (!TextUtils.isEmpty(PreferencesUtils.getPreferences(
//                        BaseActivity.this, PreferencesUtils.SESSION_ID))) {
//                    hashmap.put("Cookie", PreferencesUtils.getPreferences(
//                            BaseActivity.this, PreferencesUtils.SESSION_ID));
//                }
//                return hashmap;
                return CommonFunction.getAuthMap(BaseActivity.this, url);
            }

            protected Map<String, String> getParams() {
                //LogUtil.d("params is"+paramTrue.toString() );
                return paramTrue;
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
//        strRequest.setRetryPolicy(new DefaultRetryPolicy(5* 1000, 3, 1.0f));
        AppController.getApp().addToRequestQueue(strRequest, tag);
    }

    // 添加用户cookie信息发送请求
    public void sendSessionHttpRequestNotRetry(final String url,  Map params, String tag) {
        tag = (tag == null ? TAG : tag);
        hideSorry();
        if (params == null) {
            params =new HashMap<String, String>();
        }

//        if(CommonFunction.isLogin(BaseActivity.this)){
//            params.put(Constant.USER_ID,
//                    PreferencesUtils.getPreferences(BaseActivity.this,PreferencesUtils.USER_ID));
//            params.put(Constant.TOKEN,
//                    PreferencesUtils.getPreferences(BaseActivity.this,PreferencesUtils.TOKEN));
//        }

        final Map paramTrue = params;
        StringRequest strRequest = new StringRequest(Request.Method.POST, url, this,
                this) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CommonFunction.getAuthMap(BaseActivity.this, url);
            }
            protected Map<String, String> getParams() {
                return paramTrue;
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
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0,1, 1.0f));
        AppController.getApp().addToRequestQueue(strRequest, tag);
    }




    //    public void sendSessionMultiHttpRequest(final String url,
//                                            final HashMap<String, String> fileParams,
//                                             HashMap<String, String> params, String tag) {
//        // showProgressDialog();
//        // tag = (tag==null?TAG:tag);
//        int index = url.indexOf("/api");
//        final String subUrl = url.substring(index);
//        if (params == null) {
//            params =new HashMap<String, String>();
//        }
//
////        LogUtil.d("params is " +params.toString());
////
////        if(CommonFunction.isLogin(BaseActivity.this)){
////            params.put(Constant.USER_ID,
////                    PreferencesUtils.getPreferences(BaseActivity.this,PreferencesUtils.USER_ID));
////            params.put(Constant.TOKEN,
////                    PreferencesUtils.getPreferences(BaseActivity.this,PreferencesUtils.TOKEN));
////        }
//
//        final Map paramTrue = params;
//        MultipartRequest local12 = new MultipartRequest(url, fileParams,
//                params, this, this) {
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CommonFunction.getAuthMap(EditPhotoBaseActivity.this, url);
//            }
//
//            protected Map<String, String> getParams() {
//                return paramTrue;
//            }
//
//            @Override
//            protected Response<String> parseNetworkResponse(
//                    NetworkResponse response) {
//                try {
//                    String dataString = new String(response.data, "UTF-8");
//                    return Response.success(dataString,
//                            HttpHeaderParser.parseCacheHeaders(response));
//                } catch (UnsupportedEncodingException e) {
//                    return Response.error(new ParseError(e));
//                }
//            }
//
//        };
//        // local12.setRetryPolicy(new DefaultRetryPolicy(6* 1000, 1, 1.0f));
//        local12.setRetryPolicy(new DefaultRetryPolicy(0,1, 1.0f));
//        App.getApp().addToRequestQueue(local12, TAG);
//    }
    //单击联网失败图标
    protected void onNetErrorClick(){
        hideSorry();
        showLoading();
    }

    @Override
    protected void onDestroy() {
        //防止内存泄露
        MyToast.clearResult();
        AppController.getApp().cancelPendingRequests(TAG);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // MobclickAgent.onResume(this);
        //  StatService.onResume(this);

//        if(ConstantValue.DEBUG){
//            Bugtags.onResume(this);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(ConstantValue.DEBUG){
//            Bugtags.onPause(this);
//        }
        //MobclickAgent.onPause(this);
        //  StatService.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if(ConstantValue.DEBUG){
//            Bugtags.onDispatchTouchEvent(this, ev);
//
        return super.dispatchTouchEvent(ev);
    }
}
