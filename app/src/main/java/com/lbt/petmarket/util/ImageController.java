package com.lbt.petmarket.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.lbt.petmarket.BuildConfig;

import com.lbt.petmarket.R;
import com.lbt.petmarket.customView.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


//图片控制类
public class ImageController {
    
	//public static final String IMAGE_STORE_PATH = "KindergartenMgr_img";
	
	public static final int MAX_WIDTH = 1080;//1400
	public static final int MAX_HEIGHT = 1080;//800
	
	public static final String ASK_IMG_NAME = "ask.jpg";
	public static final String PIC_NAME = "pic.jpg";
	public static final String AVATAR_IMG_NAME = "avatar.jpg";
	public static final String PET_AVATAR_IMG_NAME = "pet_avatar.jpg";
	public static final String GRAFFITO_NAME = "graffito.jpg";

	public static final String SAVE_PIC_NAME_PEOPLE="people_head.jpg";
	/***
	*标记用户点击了从照相机获取图片  即拍照
	*/
	public static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	public static final int PHOTO_REQUEST_TAKEPHOTO_TRUE = 21;// 拍照
	/***
	 *标记用户点击了从图库中获取图片  即从相册中取
	 */
	public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	public static final int PHOTO_REQUEST_GALLERY_TRUE = 22;// 从相册中选择

	/***
	 * 返回处理后的图片
	 */
	public static final int PHOTO_REQUEST_CUT = 3;// 结果
	public static final int PHOTO_REQUEST_CUT_TRUE = 23;// 结果



	public static void restoreImg(){
		
	}
	
	public static String getImageStorePath(){
		 String path = Environment
			.getExternalStorageDirectory()+ File.separator+"宠物市场/";
    	 File dir = new File(path);
    	 if (!dir.exists()) {  
              try {  
                  //在指定的文件夹中创建文件  
                  dir.mkdirs(); 
            } catch (Exception e) {
            }  
          }  
		return path;
	}
	
	public static String getShareImgPath(){
		return getImageStorePath()+"share.jpg";
	}
	
	public static String getAskImgPath(){
		return getImageStorePath()+ASK_IMG_NAME;
	}
	
	public static String getPicPath(){
		return getImageStorePath()+PIC_NAME;
	}
	
	public static String getAvatarImgPath(){
		return getImageStorePath()+AVATAR_IMG_NAME;
	}
	
	public static String getPetAvatarImgPath(){
		return getImageStorePath()+PET_AVATAR_IMG_NAME;
	}
	
	public static String getGraffitoImgPath(){
		return getImageStorePath()+GRAFFITO_NAME;
	}
	
	
	public static Bitmap getImg(){
	    Bitmap bmp = null;
		return bmp;
	}
	
	     //缩放图片比  
         //显示尺寸	图片宽度  800 
	
         //实际尺寸	最大宽度  1400
//	    public static int calculateInSampleSize(BitmapFactory.Options options,
//	            int reqWidth, int reqHeight) {
//	        // Raw height and width of image
//	        final int height = options.outHeight;
//
//	        final int width = options.outWidth;
//
////	        Log.d("lyl","option height is " + height);
////	        Log.d("lyl","option width is " + width);
////	        Log.d("lyl","option reqWidth is " + reqWidth);
////	        Log.d("lyl","option reqHeight is " + reqHeight);
//			LogUtil.d("option height is " + height);
//			LogUtil.d("option width is " + width);
//	        int inSampleSize = 1;
//
//	        if (width > reqWidth) {
//
//	            inSampleSize = Math.round((float) width / (float) reqWidth);
//
//	            final float totalPixels = width * height;
//
//	            // Anything more than 2x the requested pixels we'll sample down
//	            // further.
//	            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
//
//	            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
//	                inSampleSize++;
//	            }
//	        }
//	        if(BuildConfig.DEBUG){
//	        	 Log.d("lyl","inSampleSize is " + inSampleSize);
//	        }
//	        return inSampleSize;
//	    }

	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		// Determine how much to scale down the image
		int inSampleSize = Math.min(width / reqWidth, height / reqHeight);

		if(inSampleSize<1){
			inSampleSize = 1;
		}
      //  LogUtil.d("inSampleSize is " + inSampleSize);
		return inSampleSize;
	}
	
	    //拿到缩放后的图片，用于显示的尺寸
	    public static Bitmap decodeSampledBitmapFromFile(String filename,
	            int reqWidth, int reqHeight) {

	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(filename, options);

	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        return BitmapFactory.decodeFile(filename, options);
	    }
	    
	    //拿到缩放后的图片，用于显示的尺寸
	    public static Bitmap decodeSampledBitmapFromStream(Context context,Uri imageUri) {

	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	         
	       // Rect outpadding = new Rect(0, 0, options.outWidth, options.outHeight);
	        try {
				BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, options);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);
	    //    options.inSampleSize = 3;

	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        
	        try {
				return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, options) ;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return null;
	    }
	    
	    //拿到缩放后的图片，用于上传给服务器压缩
	    public static Bitmap decodeSampledBitmapFromFile(String filename) {
	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(filename, options);

	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        return BitmapFactory.decodeFile(filename, options);
	    }


	    
	    public static void imageRequest(final Context context,String url,final ImageView imageView,int width,int height) {
//	    	imageView.setImageResource(R.drawable.find_user_avatar_default);
	    	width=(width==0?100:width);
	        height = (height==0?100:height);
	    	
			 ImageLoader imageLoader =
			 ImageCacheManager.getInstance().getImageLoader();
			  
		//	String url = "http://www.00up.com/fcliaoning/1.jpg";
			 
			// mImageView.setImageBitmap(ImageCacheManager.getInstance().getBitmap(url));
	    
	        
			imageLoader.get(url, new ImageListener() {

				
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
                        if(BuildConfig.DEBUG){
                        	Log.d("lyl", "onErrorResponse is " + arg0);
                        }
                        imageView.setImageResource(R.drawable.avatar_secret);
                        
//                        imageView.setImageResource(R.drawable.person_photo_bg);
				}

				@Override
				public void onResponse(ImageContainer arg0, boolean arg1) {
					 if(BuildConfig.DEBUG){
                     	Log.d("lyl", "imageLoader onResponse is " + arg1);
                     }
//					Drawable a =  new BitmapDrawable(context.getResources(),arg0.getBitmap());
//					ScaleDrawable td = new ScaleDrawable(a,
//							Gravity.CENTER, 0, 0);
					
//					  final TransitionDrawable td =
//		                    new TransitionDrawable(new Drawable[] {
//		                            new ColorDrawable(android.R.color.transparent),
//		                            new BitmapDrawable(context.getResources(), arg0.getBitmap())
//		                    });
//					  imageView.setImageDrawable(td);
//					 
//					  td.startTransition(600);
					  
					  imageView.setImageBitmap(arg0.getBitmap());
				//	  DiskLruImageCache imageCache = (DiskLruImageCache)ImageCacheManager.getInstance().getImageCache();
//				        ImageCacheManager.getInstance().clearCache();
				//       Log.d("lyl","imageCache is " + imageCache.getCacheFolder());
				        
				}
			}, width, height);
			
	    }
	    
    public static void imageRequest(final Context context,String url,final ImageView imageView,
    		int width,int height,final boolean isTransition) {
	    	
	    	width=(width==0?100:width);
	        height = (height==0?100:height);
	    	
			 ImageLoader imageLoader =
			 ImageCacheManager.getInstance().getImageLoader();
		//	String url = "http://www.00up.com/fcliaoning/1.jpg";
			 
			// mImageView.setImageBitmap(ImageCacheManager.getInstance().getBitmap(url));
	    
	        
			imageLoader.get(url, new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
                        if(BuildConfig.DEBUG){
                        	Log.d("lyl", "onErrorResponse is " + arg0);
                        }
                        
//                        imageView.setImageResource(R.drawable.person_photo_bg);
				}

				@Override
				public void onResponse(ImageContainer arg0, boolean arg1) {
					// TODO Auto-generated method stub
					 if(BuildConfig.DEBUG){
                     	Log.d("lyl", "imageLoader onResponse is " + arg1);
                     }
 
					if(isTransition){
						  final TransitionDrawable td =
				                    new TransitionDrawable(new Drawable[] {
				                            new ColorDrawable(android.R.color.transparent),
				                            new BitmapDrawable(context.getResources(), arg0.getBitmap())
				                    });
							  imageView.setImageDrawable(td);
							 
							  td.startTransition(600);
					}else{
						  imageView.setImageBitmap(arg0.getBitmap());
					}
				//	  DiskLruImageCache imageCache = (DiskLruImageCache)ImageCacheManager.getInstance().getImageCache();
//				        ImageCacheManager.getInstance().clearCache();
				//       Log.d("lyl","imageCache is " + imageCache.getCacheFolder());
				        
				}
			}, width, height);
	    }
    
    //圆角图片
    public static void circleImageRequest(final Context context,String url,final CircleImageView imageView,
    		int width,int height,final boolean isTransition) {
	    	
	    	width=(width==0?100:width);
	        height = (height==0?100:height);
	    	
			 ImageLoader imageLoader =
			 ImageCacheManager.getInstance().getImageLoader();
		//	String url = "http://www.00up.com/fcliaoning/1.jpg";
			 
			// mImageView.setImageBitmap(ImageCacheManager.getInstance().getBitmap(url));
	    
	        
			imageLoader.get(url, new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
                        if(BuildConfig.DEBUG){
                        	Log.d("lyl", "onErrorResponse is " + arg0);
                        }
                        
//                        imageView.setImageResource(R.drawable.person_photo_bg);
				}

				@Override
				public void onResponse(ImageContainer arg0, boolean arg1) {
					// TODO Auto-generated method stub
					 if(BuildConfig.DEBUG){
                     	Log.d("lyl", "imageLoader onResponse is " + arg1);
                     }
 
//					if(isTransition){
//						  final TransitionDrawable td =
//				                    new TransitionDrawable(new Drawable[] {
//				                            new ColorDrawable(android.R.color.transparent),
//				                            new BitmapDrawable(context.getResources(), arg0.getBitmap())
//				                    });
//							  imageView.setImageDrawable(td);
//							 
//							  td.startTransition(600);
//					}else{
						  imageView.setImageBitmap(arg0.getBitmap());
//					}
				//	  DiskLruImageCache imageCache = (DiskLruImageCache)ImageCacheManager.getInstance().getImageCache();
//				        ImageCacheManager.getInstance().clearCache();
				//       Log.d("lyl","imageCache is " + imageCache.getCacheFolder());
				        
				}
			}, width, height);
	    }
    /**
     * 圆形图片
     * @param context
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public static void imageRoundRequest(final Context context,String url,
    		final ImageView imageView,int width,int height,final boolean isTransition) {
    	
    	width=(width==0?100:width);
        height = (height==0?100:height);
    	
		 ImageLoader imageLoader =
		 ImageCacheManager.getInstance().getImageLoader();
	//	String url = "http://www.00up.com/fcliaoning/1.jpg";
		 
		// mImageView.setImageBitmap(ImageCacheManager.getInstance().getBitmap(url));
    
        
		imageLoader.get(url, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
                    if(BuildConfig.DEBUG){
                    	Log.d("lyl", "onErrorResponse is " + arg0);
                    }
//                    imageView.setImageResource(R.drawable.person_photo_bg);
			}

			@Override
			public void onResponse(ImageContainer arg0, boolean arg1) {
				 if(BuildConfig.DEBUG){
                 	Log.d("lyl", "imageLoader onResponse is " + arg1);
                 }				
					if(isTransition){
						  final TransitionDrawable td =
				                    new TransitionDrawable(new Drawable[] {
				                            new ColorDrawable(android.R.color.transparent),
				                            new BitmapDrawable(context.getResources(), ImageController.toRoundBitmap(arg0.getBitmap()))
				                    });
							  imageView.setImageDrawable(td);
							 
							  td.startTransition(600);
					}else{
						  imageView.setImageBitmap(ImageController.toRoundBitmap(arg0.getBitmap()));
					}
				    
			}
		}, width, height);
    }
    //图片存文件
    public static void compressBmpToFile(Bitmap bmp,File file){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;//个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        
        while (baos.toByteArray().length / 1024 > 500) { //最大500k
          baos.reset();
          options -= 10;
          //LogUtil.d("option is " + options);
          bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        
        try {
          FileOutputStream fos = new FileOutputStream(file);
          fos.write(baos.toByteArray());
          fos.flush();
          fos.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    //从文件中读取压缩后的图片
    public static Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//800
        float ww = 400f;//480
        int be = 1;
        if (w > h && w > ww) {
          be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
          be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
          be = 1;
        newOpts.inSampleSize = be;//设置采样率
        
      //  newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
        
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//    		return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
                      //其实是无效的,大家尽管尝试
        return bitmap;
      }
    
    /**
     * 最省内存的方式读取压缩后的图片
     * @param context       
     * @param resourse_id 图片id
     * @param width        最大宽度
     * @param height       最大高度
     * @return
     */
    public static Bitmap getBitmapFromStream(Context context,int resourse_id,int width,int height) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(context.getResources().openRawResource(resourse_id), null, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565; //565分别代表RGB的深度
        options.inPurgeable = true;  
        options.inInputShareable = true;  
		return BitmapFactory.decodeStream(context.getResources().openRawResource(resourse_id), null, options) ;
    }

    /**
	 * * 转换图片成圆形          
	 * * @param bitmap 传入Bitmap对象        
	 * * @return
	 *          
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if(bitmap==null){
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_4444);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);

		return output;
	}
	
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public static Intent startPhotoZoom(Uri inuri,int outputX,int outputY,Uri outUri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(inuri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		// intent.putExtra("return-data", true);

		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getPicPath())));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);// 若为false则表示不返回数据
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
	
		
		intent.putExtra("output_uri", getCropOutUri());
		
		return intent;

	}

	private static Uri getCropOutUri (){
		File cameraFile = new File(getPicPath());
		Uri imageUri = Uri.fromFile(cameraFile);
		return imageUri;
	}
	//放大缩小图片
	 public static Bitmap zoom(Bitmap bitmap,float scale) {
	     if(bitmap==null){
			  return null;
		  }
		  Matrix matrix = new Matrix();
		  matrix.postScale(scale,scale); //长和宽放大缩小的比例
	
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		  return resizeBmp;
		 }
	 
	 /**
	      * 回收ImageView占用的图像内存;
	      * @param view
	      */
	     public static void recycleImageView(View view){
	         if(view==null) return;
	         if(view instanceof ImageView){
	             Drawable drawable=((ImageView) view).getDrawable();
	             if(drawable instanceof BitmapDrawable){
	                 Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
	                 if (bmp != null && !bmp.isRecycled()){
	                     ((ImageView) view).setImageBitmap(null);
	                     bmp.recycle();
	                     bmp=null;
	                 }
	             }
	         }
	     }

	//sd卡是否可写
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	//最节省资源的获取图片方法
	public static Bitmap readBitmap(Context context, int resId, int inSampleSize){
		Bitmap bmp = null;// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。

		if (bmp == null) {// 传说decodeStream直接调用JNI>>nativeDecodeAsset()来完成decode，
			// 无需再使用java层的createBitmap，从而节省了java层的空间。

			InputStream is = context.getResources().openRawResource(resId);
			if(inSampleSize <=0){
				inSampleSize=1;
			}
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Config.RGB_565; //565分别代表RGB的深度
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			opt.inSampleSize = inSampleSize;

			//获取资源图片
			bmp =  BitmapFactory.decodeStream(is, null, opt);
		}
		return bmp;
	}

	//最节省资源的获取图片方法
	public static Bitmap readHighQualityBitmap(Context context, int resId){
		Bitmap bmp = null;// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。

		if (bmp == null) {// 传说decodeStream直接调用JNI>>nativeDecodeAsset()来完成decode，
			InputStream is = context.getResources().openRawResource(resId);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Config.ARGB_8888; //565分别代表RGB的深度
			opt.inSampleSize = 1;
//            opt.inMutable = true;
			//获取资源图片
			bmp =  BitmapFactory.decodeStream(is, null, opt);
		}
		return bmp;
	}

	/**
	 * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
	 * more memory that there is already allocated.
	 *
	 * @param imgIn - Source image. It will be released, and should not be used more
	 * @return a copy of imgIn, but muttable.
	 */
	public static Bitmap convertToMutable(Bitmap imgIn) {
		if(imgIn==null){
			return null;
		}
		try {
			//this is the file going to use temporally to save the bytes.
			// This file will not be a image, it will store the raw image data.
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

			//Open an RandomAccessFile
			//Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
			//into AndroidManifest.xml file
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

			// get the width and height of the source bitmap.
			int width = imgIn.getWidth();
			int height = imgIn.getHeight();
			Config type = imgIn.getConfig();

			//Copy the byte to the file
			//Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
			imgIn.copyPixelsToBuffer(map);
			//recycle the source bitmap, this will be no longer used.
			imgIn.recycle();
			System.gc();// try to force the bytes from the imgIn to be released

			//Create a new bitmap to load the bitmap again. Probably the memory will be available.
			imgIn = Bitmap.createBitmap(width, height, type);
			map.position(0);
			//load it back from temporary
			imgIn.copyPixelsFromBuffer(map);
			//close the temporary file and channel , then delete that also
			channel.close();
			randomAccessFile.close();

			// delete the temp file
			file.delete();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imgIn;
	}


	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * @param activity
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}


	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}




	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

}
