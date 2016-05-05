package com.lbt.petmarket;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
//import com.bugtags.library.Bugtags;
//import com.bugtags.library.BugtagsOptions;
import com.lbt.petmarket.util.AdaptationClass;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Administrator on 2015/12/28 0028.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;
    //适配类
    private AdaptationClass mAdaptationClass;
    private RequestQueue mRequestQueue;
    DisplayImageOptions options;
    public AppController(){
        mInstance = this;
    }

    public static AppController getApp() {
        if (mInstance != null && mInstance instanceof AppController) {
            return (AppController) mInstance;
        } else {
            mInstance = new AppController();
            mInstance.onCreate();
            return (AppController) mInstance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        mInstance = this;
        //初始化文件下载
//        FileDownloader.init(this);
//        initBugTag();
    }
    public AdaptationClass getAdaptation() {
        if (mAdaptationClass == null) {
//             mAdaptationClass = new AdaptationClass(720, 1280, 160);
        }
        return mAdaptationClass;
    }

    public void setAdaptation(AdaptationClass adaptationClass) {
        mAdaptationClass = adaptationClass;
    }


    public static void initImageLoader(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .threadPoolSize(3)
//                .memoryCacheSize(2 * 1024 * 1024)
//                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

    }



    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }


    public DisplayImageOptions getImageLoaderOption(){
        if(options!=null){
            return options;
        }else{

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(android.R.color.white) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(android.R.color.white)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(android.R.color.white)  //设置图片加载/解码过程中错误时候显示的图片
//                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中


//				.cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .cacheOnDisk(true)
//				.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
//				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//

//				.decodingOptions(REsi )//设置图片的解码配置
                            //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
                            //设置图片加入缓存前，对bitmap进行设置
                            //.preProcessor(BitmapProcessor preProcessor)
                    .resetViewBeforeLoading(false)//设置图片在下载前是否重置，复位
//				.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                    .displayer(new FadeInBitmapDisplayer(300))//是否图片加载好后渐入的动画时间
                    .build();//构建完成
        }
        return options;
    }

//    private void initBugTag(){
////        Bugtags.start("ee5e6cd52c47c5d78611df629a8abece", this, Bugtags.BTGInvocationEventBubble);
//        BugtagsOptions options = new BugtagsOptions.Builder().
//                trackingLocation(true).//是否获取位置
//                trackingCrashLog(true).//是否收集crash
//                trackingConsoleLog(true).//是否收集console log
//                trackingUserSteps(true).//是否收集用户操作步骤
//                crashWithScreenshot(true).
//                versionName("1.0.1").//自定义版本名称
//                versionCode(1).//自定义版本号
//                build();
//
//        Bugtags.start("797bffb94985a22882641781171d0990", this, Bugtags.BTGInvocationEventBubble, options);
////        Bugtags.start("ee5e6cd52c47c5d78611df629a8abece", this, Bugtags.BTGInvocationEventBubble);
//
//    }


}
