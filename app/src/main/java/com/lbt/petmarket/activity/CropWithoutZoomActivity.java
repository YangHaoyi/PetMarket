package com.lbt.petmarket.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.lbt.petmarket.R;
import com.lbt.petmarket.customView.MyToast;
import com.lbt.petmarket.util.ConstantKey;
import com.lbt.petmarket.util.ImageController;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/2/18.
 */
public class CropWithoutZoomActivity extends FragmentActivity implements View.OnClickListener {

     CropImageView cropImageView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        initView();
        initCropParams();
    }

    private void initView(){
        ImageView ivBack = (ImageView)findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        TextView tvComplete = (TextView)findViewById(R.id.tv_complete);
        tvComplete.setOnClickListener(this);
    }

    private void initCropParams(){
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);

        cropImageView.setFixedAspectRatio(true);
        cropImageView.setAspectRatio(1, 1);
        cropImageView.setGuidelines(CropImageView.DEFAULT_GUIDELINES);

//        String uri = getIntent().getStringExtra("uri");
        Uri uri = getIntent().getData();
//        Bitmap bitmap = ImageController.decodeSampledBitmapFromFile(path);
        Bitmap bitmap = ImageController.decodeSampledBitmapFromStream(this, uri);


        if(bitmap==null||bitmap.getWidth()<50||bitmap.getHeight()<50){
            MyToast.showMsg(this, "图片过小，请重新选择");
            finish();
        }

        cropImageView.setImageBitmap(bitmap);

    }

    private void crop(){
        final Bitmap croppedImage = cropImageView.getCroppedImage();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                CropWithoutZoomActivity.this.finish();
                break;
            case R.id.tv_complete:
                Bitmap croppedImage = cropImageView.getCroppedImage();
                Log.d("lyl","width is " + croppedImage.getWidth() );
                if(croppedImage.getWidth()>640){
                    float scale =  640f/croppedImage.getWidth();
                    croppedImage =  ImageController.zoom(croppedImage,scale);
//                    MyToast.showMsg(CropActivity.this,"宽度是"+croppedImage.getWidth()+"压缩中");
                }
//                String path = ImageController.getAvatarImgPath()+System.currentTimeMillis();
                String path =ImageController.getImageStorePath() + "avatar_" +System.currentTimeMillis();
                File file = new File(path);
                ImageController.compressBmpToFile(croppedImage, file);
                Intent data = new Intent();
                data.putExtra(ConstantKey.AVATAR_PATH, path);
//                setResult(1,bundle);
                setResult(1,data);
                CropWithoutZoomActivity.this.finish();
                break;
        }
    }
}
