package com.lbt.petmarket.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.MD5Util;
import com.android.volley.toolbox.StringRequest;
import com.lbt.petmarket.AppController;
import com.lbt.petmarket.R;
import com.lbt.petmarket.customView.DatePickerView;
import com.lbt.petmarket.customView.MyProgressDialog;
import com.lbt.petmarket.customView.MyToast;
import com.lbt.petmarket.customView.ScrollSwipeRefreshLayout;
import com.lbt.petmarket.customView.UpdateDialog;
import com.lbt.petmarket.model.AdList;
import com.lbt.petmarket.model.AdWelcome;
import com.lbt.petmarket.service.UpdateService;
import com.lbt.petmarket.util.CommonFunction;
import com.lbt.petmarket.util.Constant;
import com.lbt.petmarket.util.ConstantKey;
import com.lbt.petmarket.util.FileUtil;
import com.lbt.petmarket.util.ImageController;

import com.lbt.petmarket.util.NetWorkUtils;
import com.lbt.petmarket.util.PreferencesUtils;
import com.lbt.petmarket.util.SelectHeadUtil;
import com.lbt.petmarket.util.SelectTrue;
import com.lbt.petmarket.util.ShakeListenerUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;




public class WebActivity extends BaseActivity implements OnClickListener, AMapLocationListener {

    private static final String TAG = "WebActivity";
    public static final String PHOTO_URI = "photo_uri";
    private WebView wv;
    private String mUrl;
    private String mImageChooseString;
    private ImageView ivBack;
    private boolean  mIsError = true;
//    private ProgressBar progressBar;
    private ShakeListenerUtils shakeUtils;
//    private SensorManager mSensorManager; //定义sensor管理器, 注册监听器用

    public static final int FROM_WELCOME = 1;  //来自欢迎页

    private int from = 0;

    private static final int TYPE_REFRESH_TOKEN = 1;
    private static final int TYPE_CHECK_UPDATE = 2;

    private static final int TYPE_CHECK_AD = 3;

    private boolean mLocation;
    private int mLocaCnt = 0;



    private static final int TYPE_TEST = 9;
    private ArrayList<AdList> mAdList = new ArrayList<AdList>();

    private int mPostType = TYPE_REFRESH_TOKEN;

    private SwipeRefreshLayout swipeLayout;

    private String[] mMp3List;

    private String mCurMp3Url;
    SoundPool soundPool;
    int mCurSoundId;
    File file1;
    private String bitmaptoString;


    public static final int INPUT_FILE_REQUEST_CODE = 11;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 12;
    private ValueCallback<Uri[]> mFilePathCallback;

    private String mCameraPhotoPath;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private double mLat;
    private double mLon;




    //Date相关

    private int yearNow;
    private int monthNow;
    private int dayNow;
    private String mGetBrith;







    private Uri photoUri;

    private MyProgressDialog pd;

    Bitmap bit;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1==1){
                wv.loadUrl("javascript:chooseImage('" +"data:image/jpeg;base64,"+ mImageChooseString + "')");
                delete(ImageController.getImageStorePath(), ImageController.SAVE_PIC_NAME_PEOPLE);
            }else if(msg.arg1==2){
                wv.loadUrl("javascript:chooseTrueImage('" +"data:image/jpeg;base64,"+ mImageChooseString + "')");
                delete(ImageController.getImageStorePath(), ImageController.SAVE_PIC_NAME_PEOPLE);
            }

        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 0) {
            return;
        }
        System.out.println("requestCode is "+requestCode+"");
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != RESULT_OK ? null
                    : data.getData();
            if (result != null) {
                String imagePath = ImageController.getImageStorePath();   //.getPath(this, result);
                if (!TextUtils.isEmpty(imagePath)) {
                    result = Uri.parse("file:///" + imagePath);
                }
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == INPUT_FILE_REQUEST_CODE && mFilePathCallback != null) {
            // 5.0的回调
            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
//                        Logger.d("camera_photo_path", mCameraPhotoPath);
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
//                    Logger.d("camera_dataString", dataString);
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if(requestCode==ImageController.PHOTO_REQUEST_TAKEPHOTO){
            if(photoUri!=null){
                System.gc();
                SelectHeadUtil.startPhotoZoom(this, photoUri, 600);
            }
            photoUri = null;

        }else if(requestCode==ImageController.PHOTO_REQUEST_GALLERY){
            if(data!=null){
                SelectHeadUtil.startPhotoZoom(this, data.getData(), 600);
            }

        }else if(requestCode==ImageController.PHOTO_REQUEST_CUT){
            if (resultCode == 1) {
//                    Bitmap bit = ImageController.decodeSampledBitmapFromFile(ImageController.getAvatarImgPath());
                bit = ImageController.decodeSampledBitmapFromFile(data.getStringExtra(ConstantKey.AVATAR_PATH));
//                    LogUtil.d("person mCirHead is " + mCirHead);

//                    LogUtil.d("person bit is " + bit);
//                bitmaptoString = GetImageStr(bit);


//                file1 = new File(ImageController.getImageStorePath() + "head.png");

//                ImageController.compressBmpToFile(bit, file1);
//                encode(ImageController.getImageStorePath() + "head.png");

                pd = new MyProgressDialog(WebActivity.this);
                pd.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        mImageChooseString = bitmaptoString(bit);
                        bit  = null;
                        Message msg = new Message();
                        msg.arg1 = 1;
                        handler.sendMessage(msg);

                    }
                }).start();


//                MyToast.showMsg(WebActivity.this, "上传成功");


//                mImageChooseString = Bitmap2StrByBase64(bit);
//          String  bitmaptoString2 = GetImageStr(ImageController.getImageStorePath() + "head.png");

//                System.out.println("bitmaptoString1 is " + bitmaptoString);
//                System.out.println("bitmaptoString2 is " + bitmaptoString2);
//                System.out.println("bitmaptoString3 is " + bitmaptoString3);
//                System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
//                mPostType = TYPE_TEST;
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("userId","1");
//                params.put("token","48bbc7f59732ba362074522648e67d8d569a26d1");
//                params.put("base64","data:image/png;base64,"+bitmaptoString3);
//                sendSessionHttpRequest("http://webapp.sale.petshow.cc/v1/imageuploadbybase64",params,TAG);




            }
        }
        else if(requestCode==ImageController.PHOTO_REQUEST_TAKEPHOTO_TRUE){

            if(photoUri!=null){
                System.gc();
                Bitmap bitmap = ImageController.decodeSampledBitmapFromStream(this, photoUri);
                if(bitmap.getWidth()>640){
                    float scale =  640f/bitmap.getWidth();
                    bitmap =  ImageController.zoom(bitmap,scale);
//                    MyToast.showMsg(CropActivity.this,"宽度是"+croppedImage.getWidth()+"压缩中");
                }

                pd = new MyProgressDialog(WebActivity.this);
                pd.show();
                final Bitmap finalBitmap = bitmap;
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mImageChooseString = bitmaptoString(finalBitmap);
                        Message msg = new Message();
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        }else if(requestCode==ImageController.PHOTO_REQUEST_GALLERY_TRUE){
//            SelectTrue.startPhotoZoom(this, data.getData(), 600);

            if(data!=null){
                Bitmap bitmap = ImageController.decodeSampledBitmapFromStream(this, data.getData());
                if(bitmap.getWidth()>640){
                    float scale =  640f/bitmap.getWidth();
                    bitmap =  ImageController.zoom(bitmap,scale);
//                    MyToast.showMsg(CropActivity.this,"宽度是"+croppedImage.getWidth()+"压缩中");
                }
                pd = new MyProgressDialog(WebActivity.this);
                pd.show();
                final Bitmap finalBitmap = bitmap;
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mImageChooseString = bitmaptoString(finalBitmap);
                        Message msg = new Message();
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                    }
                }).start();
            }

        }else if(requestCode==ImageController.PHOTO_REQUEST_CUT_TRUE){
            if (resultCode == 11) {
            }
        }



        else {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }




    /**
     * 将图片转换成base64格式进行存储
     * @param
     * @return
     */
    // // 将图片转换成字符串
    public String bitmaptoString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.NO_WRAP);


        return string;
    }
    public String Bitmap2StrByBase64(Bitmap bit){
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG, 40, bos);//参数100表示不压缩
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

//        /9j/4AAQSkZJRgABAQAAAQABAAD/2wBDABQODxIPDRQSEBIXFRQYHjIhHhwcHj0sLiQySUBMS0dA
    }

    private void encode(String path) {
        //decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.d(TAG, "bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
        String encodeString = new String(encode);
        System.out.println("encodeString"+encodeString+"");
//        mTvShow.setText(encodeString);
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        initLocation();
        System.out.println("photossss112312312");
        System.out.println("asdas123123");
        initDefaultDate();
//        datePick();

//        MyToast.showMsg(WebActivity.this, PreferencesUtils.getPreferences(WebActivity.this, PreferencesUtils.FIRST_LAT) + "" +
//                PreferencesUtils.getPreferences(WebActivity.this, PreferencesUtils.FIRST_LON) + "");

//        chooseTrueImage();
        if(savedInstanceState==null){
            photoUri = null;
        }else{
            photoUri = savedInstanceState.getParcelable(PHOTO_URI);
        }
        CommonFunction.initApplicationConfig(this);


        Log.d("lyl", "network name is " + NetWorkUtils.getNetworkTypeName(this));
        if(NetWorkUtils.getNetworkTypeName(this).equals(NetWorkUtils.NETWORK_TYPE_WIFI)){
//            checkUpdate();
        }
        shakeUtils = new ShakeListenerUtils(this);


//        shakeUtils = new ShakeListenerUtils(this);
//        from = getIntent().getIntExtra(Constant.FROM,0);


//        chooseImage();
    }
    private void initLocation(){
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        //每两秒定位一次
        locationOption.setInterval(2000);
        locationOption.setOnceLocation(false);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    public void initView(){
        super.initView();
        hideLoading();
//        mTitleView.setVisibility(View.GONE);
//        hideLoading();
        mUrl = getIntent().getStringExtra(Constant.URL);
        if(TextUtils.isEmpty(mUrl)){
            mUrl = "http://webapp.sale.petshow.cc/viewv1/publish1";
            mIsError = false;
        }

//        mUrl = "http://webapp.sale.petshow.cc/viewv1/indexview";

//        mUrl = "http://192.168.199.228:8000/";
//
        if ("".equals(mUrl) || null == mUrl) {
            finish();
            return;
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //重新刷新页面
                wv.loadUrl(wv.getUrl());
            }
        });

        soundPool=new SoundPool(5, 0,5);
        mCurSoundId = 0;
        mCurMp3Url = "";
//        progressBar = (ProgressBar) findViewById(R.id.pb);

        wv = (WebView)findViewById(R.id.wv);
        wv.setBackgroundColor(0);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if(NetWorkUtils.getNetworkTypeName(this).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)){
            wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }else{
            wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

//        wv.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
//        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setMediaPlaybackRequiresUserGesture(false);

        String cacheDirPath = getFilesDir().getAbsolutePath()
                + getString(R.string.app_name);
//        Log.i("cachePath", cacheDirPath);
        // 设置数据库缓存路径
        wv.getSettings().setDatabasePath(cacheDirPath); // API 19 deprecated
        // 设置Application caches缓存目录
        wv.getSettings().setAppCachePath(cacheDirPath);
        // 开启Application Cache功能
        wv.getSettings().setAppCacheEnabled(true);

        //模拟手机访问
        wv.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19 petshow");
        wv.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d("lyl", "shouldOverrideUrlLoading url is " + url);
                if (url.endsWith("apk")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                }
                else if (url.startsWith("sms:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                else {        //正常页
                    view.loadUrl(url);
                }
                return true;
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                Log.d("lyl", "shouldInterceptRequest url is " + url);

                if (url.endsWith("jquery.min.js")) {
                    //Log.d("lyl", "shouldInterceptRequest url=" + url + ";threadInfo" + Thread.currentThread());
//                        InputStream is = WebActivity.this.getres.getInputStream(R.raw.jquery_min_js);
                    InputStream is = WebActivity.this.getResources().openRawResource(R.raw.jquery_min_js);
                    WebResourceResponse response = new WebResourceResponse("text/javascript",
                            "utf-8", is);
                    return response;
                } else if (url.lastIndexOf("mp3") > 0 || url.lastIndexOf("MP3") > 0) {
                    Log.d("lyl", "shouldInterceptRequest is mp3 ");
                    String path = Environment.getExternalStorageDirectory() + File.separator + "testmp3" + File.separator + MD5Util.MD5(url) + ".mp3";
                    File file = new File(path);
                    if (file.exists()) {
                        Log.d("lyl", "cache hit ");

                        try {
                            InputStream is = new FileInputStream(file);

                            WebResourceResponse response = new WebResourceResponse("audio/mpeg",
                                    "utf-8", is);
                            Log.d("lyl", "response is  hit " + response.toString());

                            //如果
//                            if(mUrl.equals(url)){
                            if (NetWorkUtils.getNetworkTypeName(WebActivity.this).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
                                if (mCurSoundId != 0) {
                                    soundPool.stop(mCurSoundId);
                                }

                                soundPool.load(path, 1);
                                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                                    @Override
                                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                        mCurSoundId = sampleId;
                                        soundPool.play(sampleId, 1, 1, 0, 0, 1);
                                    }
                                });

                            }

                            return super.shouldInterceptRequest(view, url);
                        } catch (FileNotFoundException e) {
                            Log.d("lyl", "FileNotFoundException e " + e.toString());
                            e.printStackTrace();
                        }

                    } else {
                        Log.d("lyl", "cache no exists");
                        return super.shouldInterceptRequest(view, url);
                    }

                }


                return super.shouldInterceptRequest(view, url);
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                progressBar.setVisibility(View.VISIBLE);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                wv.loadUrl("javascript:firstLocation('" + PreferencesUtils.getPreferences(WebActivity.this, PreferencesUtils.FIRST_LAT)
//                        + "," + PreferencesUtils.getPreferences(WebActivity.this, PreferencesUtils.FIRST_LON) + "')");
//                hideLoading();
//                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mIsError = true;
                FrameLayout errorview = (FrameLayout) findViewById(R.id.container);


                FrameLayout.LayoutParams frameerrorbg = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                final ImageView ivBg = new ImageView(WebActivity.this);
                ivBg.setBackgroundColor(Color.WHITE);
                ivBg.setLayoutParams(frameerrorbg);
                errorview.addView(ivBg);

                FrameLayout.LayoutParams frameerror = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                frameerror.gravity = Gravity.CENTER;
                errorview.setBackgroundColor(Color.WHITE);
                final ImageView imageView = new ImageView(WebActivity.this);
                imageView.setImageResource(R.drawable.error_iv);
                imageView.setLayoutParams(frameerror);
                errorview.addView(imageView);

                final TextView tv = new TextView(WebActivity.this);
                FrameLayout.LayoutParams frametv = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                frametv.topMargin = CommonFunction.getZoomX(130);
                frametv.gravity = Gravity.CENTER;
                tv.setText("主人你的网络不见了~");
                tv.setTextColor(getResources().getColor(R.color.main_font_grey));
                tv.setLayoutParams(frametv);
                errorview.addView(tv);
                hideLoading();
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.setVisibility(View.GONE);
                        ivBg.setVisibility(View.GONE);
                        tv.setVisibility(View.GONE);
                        //  wv.setVisibility(View.GONE);
//                        mUrl = "http://192.168.199.227:8000/";
                        wv.reload();
//                        wv.loadUrl(mUrl);

                    }
                });

            }
        });

//        if(isNetworkAvailable(WebActivity.this)){
//            wv.setVisibility(View.VISIBLE);
//        }else {
//            wv.setVisibility(View.GONE);
//        }

        wv.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//弹Log
                AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
                builder.setMessage(consoleMessage.message())
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                return super.onConsoleMessage(consoleMessage);
            }


            // android 5.0
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
//                    try {
                    photoFile = new File(ImageController.getImageStorePath());//createImageFile();

                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
//                    } catch (IOException ex) {
//                        // Error occurred while creating the File
//                        Log.e("WebViewSetting", "Unable to create Image File", ex);
//                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }

            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                WebActivity.this.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                WebActivity.this.startActivityForResult(
                        Intent.createChooser(i, "Image Chooser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                WebActivity.this.startActivityForResult(Intent.createChooser(i, "Image Chooser"), WebActivity.FILECHOOSER_RESULTCODE);

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //隐藏进度条
                    swipeLayout.setRefreshing(false);
                } else {
//                    if (!swipeLayout.isRefreshing())
//                        swipeLayout.setRefreshing(true);
                }
                //动态在标题栏显示进度条
                super.onProgressChanged(view, newProgress);
            }

        });

        wv.addJavascriptInterface(new LbtJavaScriptInterface(), "lbt");//js可以通过demo访问到DemoJavaScriptInterface 类中的方法。
        wv.loadUrl(mUrl);
    }

    private void initMp3Download(){


    }

    private long exitTime = 0;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        System.out.println("zzzz33333333333333asdasd");
        if (mLocation) {
            System.out.println("zzzz3333111111asdasd");
            if (aMapLocation != null) {
                System.out.println("zzzz22333443244444111111asdasd");
                mLocaCnt++;
                mLat = aMapLocation.getLatitude();
                mLon = aMapLocation.getLongitude();
                System.out.println("zzzzla_and_lo is"+mLat+","+mLon);
                if ((mLat != 0) && (mLon != 0)) {
//                    wv.loadUrl("javascript:firstLocationBack('" + mLat + "" + "," + mLon + "" + "')");
                    wv.post(new Runnable() {
                        @Override
                        public void run() {
                            wv.loadUrl("javascript:firstLocationBack('" + mLat + "','" + mLon + "'  )");
                            System.out.println("zzzz111111111asdasd");
                        }
                    });

                } else {
                    wv.post(new Runnable() {
                        @Override
                        public void run() {
                            wv.loadUrl("javascript:firstLocationBack('" + 0 + "','" + 0 + "'  )");
                        }
                    });

                }
            } else {
                wv.post(new Runnable() {
                    @Override
                    public void run() {
                        wv.loadUrl("javascript:firstLocationBack('" + 0 + "','" + 0 + "'  )");
                    }
                });
            }
            stopLocation();
            mLocation = false;
            System.out.println("zzzz2222222asdasd");

        }

    }

//    public void  chooseTrueImage(){
//        if (!FileUtil.hasSdcard()) {
//            Toast.makeText(WebActivity.this, "没有找到SD卡，请检查SD卡是否存在", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            photoUri = FileUtil.getUriByFileDirAndFileName(ImageController.getImageStorePath(), ImageController.SAVE_PIC_NAME_PEOPLE);
//        } catch (IOException e) {
//            Toast.makeText(WebActivity.this, "创建文件失败。", Toast.LENGTH_SHORT).show();
//            return;
//        }
////            LogUtil.d("photoUri is " + photoUri);
//        SelectTrue.openDialog(WebActivity.this, photoUri);
//    }




    final class LbtJavaScriptInterface {
        LbtJavaScriptInterface() {
        }
        @JavascriptInterface
        public void destroy() {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                MyToast.showMsg(WebActivity.this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                WebActivity.this.finish();
            }
//            WebActivity.this.finish();
        }
        @JavascriptInterface
        public int getVersionCode(){
            return CommonFunction.getCurrVersionCode(WebActivity.this);
        }

//        @JavascriptInterface
//        public void assetCache(ArrayList<String> arrayList,String str) {
//
//
//            Log.d("lyl","assetCache list is " + arrayList.size());
//            if(arrayList.size()>0){
//                Log.d("lyl", "item is " + arrayList.get(0).toString());
//
//            }
//
//        }


        @JavascriptInterface
        public void firstLocation(){

            mLocation = true;
            if(locationClient==null){

               initLocation();
            }
        }

//        @JavascriptInterface
//        public void forbidPullRefresh(){
//           swipeLayout.
//        }
//        @JavascriptInterface
//        public void allowPullRefresh(){
//            swipeLayout.setEnabled(true);
//        }

        @JavascriptInterface
        public void assetCache(String[] input,String str) {

            mMp3List = input;
            Log.d("lyl", "assetCache str is " + input + " time is " + str);

            String localUpdateTime =  PreferencesUtils.getPreferences(WebActivity.this,PreferencesUtils.UPDATE_TIME);

            if(TextUtils.isEmpty(localUpdateTime)||!localUpdateTime.equals(str)){
                initMp3Download();
                PreferencesUtils.setPreferences(WebActivity.this, PreferencesUtils.UPDATE_TIME, str);
            }

        }

        @JavascriptInterface
        public void  chooseImage(){
            if (!FileUtil.hasSdcard()) {
                Toast.makeText(WebActivity.this, "没有找到SD卡，请检查SD卡是否存在", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                photoUri = FileUtil.getUriByFileDirAndFileName(ImageController.getImageStorePath(), ImageController.SAVE_PIC_NAME_PEOPLE);


            } catch (IOException e) {
                Toast.makeText(WebActivity.this, "创建文件失败。", Toast.LENGTH_SHORT).show();
                return;
            }
//            LogUtil.d("photoUri is " + photoUri);
            SelectHeadUtil.openDialog(WebActivity.this, photoUri);
        }

        @JavascriptInterface
        public void  feedBack(){
            final FeedbackAgent agent = new FeedbackAgent(WebActivity.this);
            agent.setWelcomeInfo(getResources().getString(R.string.feedback_welcome));
            agent.startFeedbackActivity();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }



        @JavascriptInterface
        public void  chooseTrueImage(){
            if (!FileUtil.hasSdcard()) {
                Toast.makeText(WebActivity.this, "没有找到SD卡，请检查SD卡是否存在", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                photoUri = FileUtil.getUriByFileDirAndFileName(ImageController.getImageStorePath(), ImageController.SAVE_PIC_NAME_PEOPLE);
            } catch (IOException e) {
                Toast.makeText(WebActivity.this, "创建文件失败。", Toast.LENGTH_SHORT).show();
                return;
            }
//            LogUtil.d("photoUri is " + photoUri);
            SelectTrue.openDialog(WebActivity.this, photoUri);
        }

        @JavascriptInterface
        public void  datePick(){
            DatePickerView datePickerView = new DatePickerView(WebActivity.this, Datelistener, yearNow, monthNow, dayNow);
            datePickerView.myShow();
        }

        @JavascriptInterface
        public void  dismissLoading() {
            pd.dismiss();
        }

    }


    public void  chooseImage(){
        if (!FileUtil.hasSdcard()) {
            Toast.makeText(WebActivity.this, "没有找到SD卡，请检查SD卡是否存在", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            photoUri = FileUtil.getUriByFileDirAndFileName(ImageController.getImageStorePath(), ImageController.SAVE_PIC_NAME_PEOPLE);
        } catch (IOException e) {
            Toast.makeText(WebActivity.this, "创建文件失败。", Toast.LENGTH_SHORT).show();
            return;
        }
//            LogUtil.d("photoUri is " + photoUri);
        SelectHeadUtil.openDialog(WebActivity.this, photoUri);
    }


//    public void  datePick(){
//        DatePickerView datePickerView = new DatePickerView(WebActivity.this, Datelistener, yearNow, monthNow, dayNow);
//        datePickerView.myShow();
//    }
    private void initDefaultDate() {
        //初始化Calendar日历对象
        Calendar mycalendar = Calendar.getInstance(Locale.CHINA);


        Date mydate = new Date(); //获取当前日期Date对象
        mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期

        yearNow = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        monthNow = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        dayNow = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天

    }

    @Override
    public void onBackPressed() {

        if(mIsError){
            super.onBackPressed();
        }else{
            wv.loadUrl("javascript:android_back()");
        }
    }
    private DatePickerView.OnDateSetListener Datelistener = new DatePickerView.OnDateSetListener() {
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {


            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            yearNow = myyear;
            monthNow = monthOfYear;
            dayNow = dayOfMonth;
            //更新日期
            updateDate();

        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate() {
            // mPet.setBirth_date(yearNow+"-"+(monthNow+1)+"-"+dayNow);
            //在TextView上显示日期
            mGetBrith = yearNow + "-" + (monthNow + 1) + "-" + dayNow;
            final String bri = yearNow + "-" + (monthNow + 1) + "-" + dayNow;

            wv.post(new Runnable() {
                @Override
                public void run() {
                    wv.loadUrl("javascript:datePickBack('" + bri + "')");
                }
            });

//            MyToast.showMsg(WebActivity.this,bri);
        }
    };
    public void checkUpdate(){
        mPostType = TYPE_CHECK_UPDATE;
        HashMap<String, String> params = new HashMap<String, String>();

        //params.put(Constant.VERSION_CODE,CommonFunction.getCurrVersionCode(HomePageActivity.this)+"");
        params.put(Constant.VERSION_CODE, 1 + "");
        params.put(Constant.SYSTEM, Constant.ANDROID);
        sendSessionHttpRequest(Constant.API_VERSION, params, TAG);
    }
    private void checkAd(){
        mPostType = TYPE_CHECK_AD;
    }

    @Override
    protected void onSuccess(JSONObject response) {
        super.onSuccess(response);

        if(mPostType ==TYPE_CHECK_UPDATE){

            final int versionCode;
            try {
                JSONObject objVersion = response.getJSONObject(Constant.VERSION);
                versionCode = objVersion.getInt("version_code");

                int currVerCode = CommonFunction.getCurrVersionCode(this);

                int is_force = response.getInt("is_force");
                int is_maintenance = response.getInt(Constant.IS_MAINTENANCE);


//                JSONArray jaAl = response.getJSONArray("ad_list");
//
//                AdList adList;
//                for(int i=0;i<jaAl.length();i++){
//                    mAdList.add(mGson.fromJson(jaAl.getString(i),AdList.class));
//                    Log.d("wwwssss", mAdList + "");
//                    Log.d("wwwssss",mAdList+"");
//                }
//                if(mAdList.size()==0){
//                    mLLBtBtm.setVisibility(View.GONE);
//                }else {
//                    mLLBtBtm.setVisibility(View.VISIBLE);
//                    RelativeLayout.LayoutParams rlPara = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                            RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    rlPara.topMargin = CommonFunction.getZoomX(600);
//                    mLlMain.setLayoutParams(rlPara);
//
//                    Random random = new Random(System.currentTimeMillis());
//
//                    Log.d("random size",mAdList.size()+"");
//                    ImageLoader.getInstance().displayImage(mAdList.get(random.nextInt((mAdList.size()-1)+1)).getPicture(),
//                            mIvBtMaterial,
//                            App.getApp().getImageLoaderOption());
//
//
////                            ImageLoader.getInstance().loadImage(mAdList.get(i).getPicture(), new ImageLoadingListener() {
////                    @Override
////                    public void onLoadingStarted(String imageUri, View view) {
////
////                    }
////
////                    @Override
////                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
////
////                    }
////
////                    @Override
////                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
////                        mIvBtMaterial.setImageDrawable(new RoundImageDrawable(loadedImage));
//////                        LinearLayout.LayoutParams llm = new LinearLayout.LayoutParams(CommonFunction.getZoomX(230),
//////                                CommonFunction.getZoomX(230));
//////                        mIvBtMaterial.setLayoutParams(llm);
////                    }
////
////                    @Override
////                    public void onLoadingCancelled(String imageUri, View view) {
////
////                    }
////                });
//
//
//
//
//
//
//                }


                //加载广告
                saveLaunchList(response);
                if(is_maintenance==1){
                    FrameLayout topview = (FrameLayout) findViewById(R.id.container);
                    FrameLayout.LayoutParams frameLp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT);
                    ImageView maintenance = new ImageView(this);
                    maintenance.setLayoutParams(frameLp);
                    maintenance.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    maintenance.setImageResource(R.drawable.error_server);
                    maintenance.setBackgroundColor(Color.WHITE);
                    topview.addView(maintenance);
                    maintenance.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            WebActivity.this.finish();
                        }
                    });
                }else if (currVerCode < versionCode) {

                    final String downloadUrl = objVersion.getString("download");
                    String desc = "修复已知bug";
                    if(objVersion.has("description")){
                        desc = objVersion.getString("description");
                    }

                    if (is_force == 1) {
                        FrameLayout topview = (FrameLayout) findViewById(R.id.container);
                        FrameLayout.LayoutParams frameLp = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);
                        ImageView update = new ImageView(this);
                        update.setLayoutParams(frameLp);
                        update.setImageResource(R.drawable.updata);
                        update.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        update.setBackgroundColor(Color.BLACK);
//                        update.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        topview.addView(update);
                        ImageView buttonview = new ImageView(this);
                        FrameLayout.LayoutParams frameBt = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT);
                        frameBt.gravity = Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL;
                        buttonview.setImageResource(R.drawable.updata_bt);
                        frameBt.bottomMargin = CommonFunction.getZoomX(100);
                        buttonview.setLayoutParams(frameBt);
                        topview.addView(buttonview);
                        buttonview.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                // MainActivity.this.finish();
                                Intent updateIntent = new Intent(
                                        WebActivity.this, UpdateService.class);
//                                    updateIntent.putExtra("titleId",
//                                            R.string.app_name + versionCode);
                                updateIntent.putExtra("title",getResources().getString(R.string.app_name)
                                        +versionCode);
                                updateIntent.putExtra("download_url",
                                        downloadUrl);
                                startService(updateIntent);
                                WebActivity.this.finish();
                            }
                        });
                        // OkCancelDialog dialog = new OkCancelDialog(this, new
                        // OnClic() {
                        // @Override
                        // public void listenler() {
                        // Intent updateIntent =new Intent(MainActivity.this,
                        // UpdateService.class);
                        // updateIntent.putExtra("titleId",R.string.app_name);
                        // updateIntent.putExtra("download_url", downloadUrl);
                        // startService(updateIntent);
                        // }
                        //
                        // @Override
                        // public void dismiss() {
                        // MainActivity.this.finish();
                        //
                        // }
                        // }, null, desc);
                        //
                        // dialog.createForceUpdateDialog();
                    } else {
                        PreferencesUtils.setBooleanPreferences(WebActivity.this,
                                PreferencesUtils.IS_SHOW_UPDATE_DIAGLOG, true);
                        if(PreferencesUtils.getBooleanPreferences(WebActivity.this,
                                PreferencesUtils.IS_SHOW_UPDATE_DIAGLOG)){
                            UpdateDialog dialog = new UpdateDialog(this,
                                    new UpdateDialog.OnClic() {
                                        @Override
                                        public void listener() {
                                            Intent updateIntent = new Intent(
                                                    WebActivity.this,
                                                    UpdateService.class);
//                                                updateIntent.putExtra("titleId",
//                                                        R.string.app_name+versionCode);
                                            updateIntent.putExtra("title",getResources().getString(R.string.app_name)
                                                    +versionCode);
                                            updateIntent.putExtra("download_url",
                                                    downloadUrl);
                                            startService(updateIntent);
                                        }

                                        @Override
                                        public void dismiss() {
                                            PreferencesUtils.setBooleanPreferences(WebActivity.this,
                                                    PreferencesUtils.IS_NEED_UPDATE,true);
                                        }
                                    }, "版本更新", desc);
                            dialog.createDialog();
                        }else{
//                                PreferencesUtils.setBooleanPreferences(MainActivity.this,
//                                        PreferencesUtils.IS_NEED_UPDATE,true);
                        }
                    }

                }


//                    else {
                //                       检测手机更新后
//                        if (CommonFunction.isLogin(this)) {
//                            refreshSession();
//                        }
//                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    public void delete(String SDPATH,String fileName) {

        //SDPATH目录路径，fileName文件名

        File file = new File(SDPATH + "/" + fileName);
        if (file == null || !file.exists() || file.isDirectory()){

        }
        file.delete();


    }




    private void saveLaunchList(JSONObject response) throws JSONException {
        JSONArray ja = response.getJSONArray(Constant.LAUNCH_LIST);
       // LogUtil.d("urlStr is " + ja.length());
        Log.d("lyl", "urlStr is " + ja.length());
        StringBuilder sb = new StringBuilder();

        StringBuilder urlStr = new StringBuilder();
        String duration;
        String jumpUrl;
        AdWelcome adWelcome;
//                    url格式为  picture|duration|url
        for(int i=0;i<ja.length();i++){
            adWelcome = mGson.fromJson(ja.getString(i),AdWelcome.class);
            urlStr.delete(0,urlStr.length());
            urlStr.append(adWelcome.getPicture());
            urlStr.append(" "+adWelcome.getDuration());
            urlStr.append(" "+adWelcome.getUrl());


            if(sb.length()>0){
                sb.append(",");
                sb.append(urlStr);
            }else{
                sb.append(urlStr);
            }

            ImageLoader.getInstance().loadImage(adWelcome.getPicture(), AppController.getApp().getImageLoaderOption(),
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            //LogUtil.d("Ad onLoadingComplete");
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
//                        mAdWelcomList.add(mGson.fromJson(ja.getString(i),AdWelcome.class));
        }
        if(ja.length() == 0){
            PreferencesUtils.setPreferences(getApplicationContext(),
                    PreferencesUtils.AD_LIST, "");
        }else if(getApplicationContext()!=null){
            Log.d("lyl", "sb is " + sb);
            PreferencesUtils.setPreferences(getApplicationContext(),
                    PreferencesUtils.AD_LIST,sb.toString());
        }
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            if (wv.canGoBack()) {
//                wv.goBack(); // goBack()表示返回WebView的上一页面
//            } else {
//                this.finish();
//            }
//            return true;
//        }
//        return false;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_topback:
//                finish();
//                break;
//            case R.id.iv_web_back:
//                if (wv.canGoBack()) {
//                    wv.goBack(); // goBack()表示返回WebView的上一页面
//                } else {
//                    this.finish();
//                }
//                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        mSensorManager = (SensorManager) this
//                .getSystemService(Service.SENSOR_SERVICE);
//        //加速度传感器
//        mSensorManager.registerListener(shakeUtils,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
//                //根据不同应用，需要的反应速率不同，具体根据实际情况设定
//                SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void refresh(){
        wv.reload();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
//        mSensorManager.unregisterListener(shakeUtils);
    }

    @Override
    protected void onDestroy() {
        wv.destroy();
        super.onDestroy();

//        if(from == FROM_WELCOME){
//            Intent guideIntent = new Intent(WebActivity.this, LoginActivity.class);
//            startActivity(guideIntent);
//            WebActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }



    }

    private void stopLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    // 添加用户cookie信息发送请求
    public void sendSessionHttpRequest(final String url,  Map params, String tag) {
        tag = (tag == null ? TAG : tag);
        hideSorry();
//        LogUtil.d("url is " + url);

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
                return CommonFunction.getAuthMap(WebActivity.this, url);
            }

            protected Map<String, String> getParams() {
//                LogUtil.d("params is" + paramTrue.toString());
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


    /**
     * 检查当前网络是否可用
     *
     * @param
     * @return
     */

    public boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }


}
