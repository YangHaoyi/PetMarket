package com.lbt.petmarket.customView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lbt.petmarket.R;
import com.lbt.petmarket.util.CommonFunction;


public class UpdateDialog implements OnClickListener,OnDismissListener {

	private static final int WIDTH = 620;
	private static final int TILTE_HEIGHT = 230;

	private Dialog dialog;
	private Context context;
	private TextView dialog_title_tv;
	private TextView dialog_content_tv;
	private String title;
    private ImageView dialog_title;
	private String content;
	private Button ok_btn;
	private Button cancle_btn;
	private OnClic click;

	public UpdateDialog(Context context, OnClic click, String title, String content) {
		this.context = context; 
		this.click=click;
//		this.title=title;
		this.content=content;

	}
	 

	public Dialog createDialog() {
		dialog = new Dialog(context, R.style.loading_dialog_custom);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnDismissListener(this);
		dialog.setContentView(R.layout.update_dialog);

        dialog_title = (ImageView)dialog.findViewById(R.id.dialog_title);
        LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(
                CommonFunction.getZoomX(WIDTH),
                CommonFunction.getZoomX(TILTE_HEIGHT)
        );
        dialog_title.setLayoutParams(linearLp);
//		dialog_title_tv=(TextView)dialog.findViewById(R.id.dialog_title_tv);
		dialog_content_tv=(TextView)dialog.findViewById(R.id.dialog_content_tv);
		
		dialog_content_tv.setText(content);
		ok_btn=(Button)dialog.findViewById(R.id.ok_btn);
		ok_btn.setOnClickListener(this);
		cancle_btn=(Button)dialog.findViewById(R.id.cancle_btn);
		cancle_btn.setOnClickListener(this);
		dialog.show();
		return dialog;
	}
	
//	public void showTitle(){
//		if(dialog_title_tv!=null){
//			dialog_title_tv.setVisibility(View.VISIBLE);
//			dialog_title_tv.setText(title);
//		}
//
//	}
	
	public Dialog createForceUpdateDialog() {
		dialog = new Dialog(context, R.style.loading_dialog_custom);
		dialog.setCancelable(false);
		dialog.setOnDismissListener(this);
		dialog.setContentView(R.layout.update_dialog);
//		dialog_title_tv=(TextView)dialog.findViewById(R.id.dialog_title_tv);
		dialog_content_tv=(TextView)dialog.findViewById(R.id.dialog_content_tv);
//		dialog_title_tv.setText(title);
		dialog_content_tv.setText(content);
		ok_btn=(Button)dialog.findViewById(R.id.ok_btn);
		ok_btn.setOnClickListener(this);
//		cancle_btn=(Button)dialog.findViewById(R.id.cancle_btn);
//		cancle_btn.setOnClickListener(this);
		dialog.show();
		return dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_btn:
			dialog.dismiss();
			if (null!=click) {
				click.listener();
			}
			break;
		case R.id.cancle_btn:
			dialog.dismiss();
 
			break;
		default:
			break;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		dialog.dismiss();
		click.dismiss();
	}

	public interface OnClic{
		void listener();
		void dismiss();
	}
	
 
	
}
