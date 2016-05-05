# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontnote
-dontwarn
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-dontwarn com.sina.**
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-dontwarn android.support.v4.view.**
-dontwarn android.support.v7.view.**
-dontwarn org.apache.http.**
-dontwarn org.apache.commons.httpclient.**
-dontwarn com.baidu.location.**
-dontwarn com.squareup.okhttp.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn okio.**

-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.facebook.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**

-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep public class com.lbt.petmarket.R$*{
    public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.baidu.mobads.** {
  public protected *;
}

-keepclassmembers class ** {
    public void onEvent*(***);
}

-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

# for microMsg
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.mm.** {*;}

-keep class com.pocketdigi.utils.FlameUtils {*;}

-keep class  cn.jpush.android.**{*;}

-keep class org.apache.**{*;}

-keep class com.jakewharton.disklrucache.**{*;}

-keep class com.google.gson.**{*;}

-keep class com.sina.weibo.sdk.**{*;}

-keep class com.tencent.weibo.sdk.android.**{*;}

-keep class com.tencent.**{*;}

-keep class com.umeng.**{*;}

-keep class com.baidu.**{*;}

-keep class com.lidroid.**{*;}

-keep class com.lbt.petmarket.model.**{*;}

-keep class android.support.v4.**{*;}
-keep class android.support.v7.**{*;}

-keepattributes Signature



-keepattributes *JavascriptInterface*

-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}


-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

-keepclassmembers class * implements com.lbt.petmarket.activity.WebActivity$.LbtJavaScriptInterface {
    *;
}
-keep class android.webkit.JavascriptInterface {*;}

-keepclassmembers   class com.lbt.petmarket.activity.WebAcivity$*{
*;
}
-keep    class com.lbt.petmarket.activity.WebActivity$*{
<methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Location
-keep   class com.amap.api.location.**{*;}
-keep   class com.aps.**{*;}

#友盟意见反馈
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}