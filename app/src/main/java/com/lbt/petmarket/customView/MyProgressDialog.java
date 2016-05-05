package com.lbt.petmarket.customView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbt.petmarket.R;


/**
 * @Description:自定义对话框 
 * @author http://blog.csdn.net/finddreams 
 */  
public class MyProgressDialog extends ProgressDialog {

	private AnimationDrawable mAnimation;
    private Context mContext;
    private ImageView mImageView;
    private String mLoadingTip;
    private TextView mLoadingTv;
    private int count = 0;
    private String oldLoadingTip;
    private int mResid;

    public MyProgressDialog(Context context) {
        super(context);
        this.mContext = context;
        setCanceledOnTouchOutside(false);
    }

    public MyProgressDialog(Context context, String content, int id) {
        super(context);
        this.mContext = context;
        this.mLoadingTip = content;
        this.mResid = id;
//      setCanceledOnTouchOutside(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        initView();  
        initData();
        getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)));
    }  
  
    private void initData() {


//        mImageView.setBackgroundResource(mResid);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
//        mAnimation = (AnimationDrawable) mImageView.getBackground();
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
//        mImageView.post(new Runnable() {
//            @Override
//            public void run() {
//                mAnimation.start();
//            }
//        });
        if(mLoadingTip!=null){
            mLoadingTv.setVisibility(View.VISIBLE);
            mLoadingTv.setText(mLoadingTip);
        }

    }

    @Override
    public void show() {
        super.show();

//        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotate);
//        operatingAnim.setInterpolator(new LinearInterpolator());
//        mImageView.startAnimation(operatingAnim);
    }

    public void setContent(String str) {
//        mLoadingTv.setText(str);
    }  
  
    private void initView() {  
        setContentView(R.layout.my_progress);
        
        mLoadingTv = (TextView) findViewById(R.id.loadingTv);
    }  
  
}  