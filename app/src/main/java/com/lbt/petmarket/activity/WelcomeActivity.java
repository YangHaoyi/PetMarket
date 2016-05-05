package com.lbt.petmarket.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbt.petmarket.AppController;
import com.lbt.petmarket.R;
import com.lbt.petmarket.util.CommonFunction;
import com.lbt.petmarket.util.Constant;
import com.lbt.petmarket.util.PreferencesUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Random;


public class WelcomeActivity extends FragmentActivity {

    private static final int SPLASH_DISPLAY_LENGHT = 2600;  //两秒进入主界面1000
    private static final int BOTTOM_LOGO_HEIGHT = 226;
    Bitmap bg;

    private int duration = SPLASH_DISPLAY_LENGHT;



    private ImageView mIvLogo;
    private ImageView ivBottomLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //user_id由int变成string 兼容问题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        CommonFunction.initApplicationConfig(this);

        setContentView(R.layout.activity_welcome);
        initView();
        showDefaultBg();

        String strAdList = PreferencesUtils.getPreferences(getApplicationContext(),
                PreferencesUtils.AD_LIST);
//        Log.d("lyl", "strAdList is " + strAdList);
//        if (!TextUtils.isEmpty(strAdList)) {
//            String[] adList = strAdList.split(",");
//            Random random = new Random();
//            int randomIndex = random.nextInt(adList.length);
//            String showAdUrl = adList[randomIndex];
//
//
//            String[] adStuct = showAdUrl.split(" ");
//            if(adStuct!=null&&adStuct.length == 3){
//                String adUrl = adStuct[0];
//                String adDuration = adStuct[1];
//                final String adJumpUrl = adStuct[2];
//
////                LogUtil.d("strAdList is " + strAdList);
////                LogUtil.d("randomIndex is " + randomIndex + " url is " + adUrl + "," + adDuration + "," + adJumpUrl);
//
//                File f = DiskCacheUtils.findInCache(adUrl, ImageLoader.getInstance().getDiskCache());
//
//                    if (f != null) {
//
//
//
//                        mIvLogo.setVisibility(View.GONE);
//                    duration = Integer.parseInt(adDuration)*1000;
////                    LogUtil.d("strList size is " + f.getAbsolutePath() + "," + f.getName());
//                    ImageView ad = (ImageView) findViewById(R.id.ad);
//
//                    FrameLayout.LayoutParams frameLp = new FrameLayout.LayoutParams(
//                            FrameLayout.LayoutParams.MATCH_PARENT,
//                            (int) AppController.getApp().getAdaptation().getCurHeight() - CommonFunction.getZoomX(BOTTOM_LOGO_HEIGHT)
//                    );
//                    ad.setLayoutParams(frameLp);
//                    ad.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//                        Log.d("lyl", "jump url is " + adJumpUrl);
//                    if(!TextUtils.isEmpty(adJumpUrl)){
//                        ad.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent select = new Intent();
//                                select.setClass(WelcomeActivity.this, WebActivity.class);
//                                //埋点    启动页_logo点击
//                               // MobclickAgent.onEvent(WelcomeActivity.this, "logo_dianji");
//                              //  Toast.makeText(WelcomeActivity.this,"启动页_logo点击",Toast.LENGTH_SHORT).show();
//                                select.putExtra(Constant.FROM, WebActivity.FROM_WELCOME);
//                                select.putExtra(Constant.URL, adJumpUrl);
//                                select.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                WelcomeActivity.this.startActivity(select);
//                                mJumpMainHandler.removeCallbacks(mJumpMainRunnable);
//                                WelcomeActivity.this.finish();
//                            }
//                        });
//                    }
//
//
//                    ImageLoader.getInstance().displayImage(adUrl, ad, AppController.getApp().getImageLoaderOption());
//
//                     ivBottomLogo = (ImageView) findViewById(R.id.iv_bottom_logo);
//                    frameLp = new FrameLayout.LayoutParams(
//                            FrameLayout.LayoutParams.MATCH_PARENT,
//                            CommonFunction.getZoomX(BOTTOM_LOGO_HEIGHT)
//                    );
//                    frameLp.gravity = Gravity.BOTTOM;
//
//                    ivBottomLogo.setVisibility(View.VISIBLE);
//                    ivBottomLogo.setLayoutParams(frameLp);
//                    ivBottomLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                    ivBottomLogo.setBackgroundColor(Color.WHITE);
////                    ivBottomLogo.setImageResource(R.drawable.welcome_logo);
//                    TextView tvJump = (TextView)findViewById(R.id.tv_jump);
//                    tvJump.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //埋点    启动页_logo跳过
//                           // MobclickAgent.onEvent(WelcomeActivity.this, "logo_tiaoguo");
//                          //  Toast.makeText(WelcomeActivity.this,"启动页_logo跳过",Toast.LENGTH_SHORT).show();
//
//                            mJumpMainHandler.removeCallbacks(mJumpMainRunnable);
//                            Intent guideIntent = new Intent(WelcomeActivity.this, WebActivity.class);
//                            startActivity(guideIntent);
//                            WelcomeActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                            WelcomeActivity.this.finish();
//                        }
//                    });
//
//                } else {
//                    showDefaultBg();
//                }
//            }else{
//                showDefaultBg();
//            }
//        } else {
//            showDefaultBg();
//        }

        mJumpMainHandler.postDelayed(mJumpMainRunnable, SPLASH_DISPLAY_LENGHT);

        //启动下载广告服务
//        Intent downloadIntent = new Intent(
//                WelcomeActivity.this, DownloadAdService.class);
//        startService(downloadIntent);
//		}else{
//
//			//进入引导页
//			 new Handler().postDelayed(new Runnable() {
//				 @Override
//				 public void run() {
//					 Intent guideIntent = new Intent(WelcomeActivity.this, GuideActivity.class);
//					 WelcomeActivity.this.startActivity(guideIntent);
//					 WelcomeActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//					 WelcomeActivity.this.finish();
//				 }
//			 }, SPLASH_DISPLAY_LENGHT);
//
//		}

    }


    Handler mJumpMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    Runnable mJumpMainRunnable = new Runnable() {
        @Override
        public void run() {

            Intent guideIntent = new Intent(WelcomeActivity.this, WebActivity.class);
            startActivity(guideIntent);
            WelcomeActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            WelcomeActivity.this.finish();
        }
    };

    //广告没有加载出来，展示默认背景图
    private void showDefaultBg() {

        ivBottomLogo = (ImageView) findViewById(R.id.iv_bottom_logo);
        ivBottomLogo.setVisibility(View.GONE);
//        initConfig();
//        Glide.with(this).load(R.drawable.welcome).into(mIvLogo);
        mIvLogo.setBackgroundResource(R.drawable.welcome);
//        AnimationDrawable animation = (AnimationDrawable) mIvLogo.getBackground();
//        animation.start();
//        FrameLayout container = (FrameLayout) findViewById(R.id.welcome);
//        bg = ImageController.getBitmapFromStream(this, R.drawable.welcome,
//                (int) AppController.getApp().getAdaptation().getCurWidth(),
//                (int) AppController.getApp().getAdaptation().getCurHeight());

//        BitmapDrawable b = new BitmapDrawable(getResources(), bg);
//        container.setBackgroundDrawable(b);
    }

    private void initView(){

        mIvLogo = (ImageView) findViewById(R.id.iv_logo);
//        FrameLayout.LayoutParams frameLp = new FrameLayout.LayoutParams(
//                (int) AppController.getApp().getAdaptation().getCurWidth(),
//                (int) AppController.getApp().getAdaptation().getCurHeight()
//        );
//        mIvLogo.setLayoutParams(frameLp);
    }
//    private void initConfig(){
//
//        //适配logo
//        FrameLayout.LayoutParams logoPara = new FrameLayout.LayoutParams(CommonFunction.getZoomX(750),
//                CommonFunction.getZoomX(750));
//        logoPara.topMargin = CommonFunction.getZoomX(110);
//        mIvLogo.setLayoutParams(logoPara);
//
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bg != null && !bg.isRecycled()) {
            bg.recycle();
            bg = null;
        }
        if (mJumpMainHandler != null) {
            mJumpMainHandler.removeCallbacks(mJumpMainRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

    }
}
