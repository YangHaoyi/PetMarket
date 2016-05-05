package com.lbt.petmarket.customView;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.lbt.petmarket.R;
import com.lbt.petmarket.util.CommonFunction;
import com.lbt.petmarket.util.Constant;


public class MyToast {

	private static Toast result;
	public static void showMsg(Context context,CharSequence msg){
		if(context == null){
			return;
		}
		if(result==null){
			result = new Toast(context);
			TextView showText = new TextView(context);
			showText.setText(msg);
			showText.setTextColor(context.getResources().getColor(R.color.main_font_red));
//        showText.setTypeface(showText.getTypeface(), Typeface.BOLD);
//			int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
//					context.getResources().getDisplayMetrics());
			int textSize = CommonFunction.getZoomX(12);
			showText.setTextSize(textSize);//16
			showText.setBackgroundResource(R.drawable.msg_background);

			result.setView(showText);

			result.setDuration(Toast.LENGTH_SHORT);

			result.setGravity(Gravity.CENTER, 0, 0);
		}else{
			((TextView)result.getView()).setText(msg);
		}
		result.show();

//		return result;
	}

	public static void clearResult(){
		if(result!=null){
			result.cancel();
			result = null;
		}
	}

	public static void showDebugMsg(Context context,CharSequence msg){
        if(Constant.DEBUG == false){
            return;
        }
		if(context == null){
			return;
		}
//		if(result==null){
            AlertDialog. Builder builder = new AlertDialog. Builder ( context );
            builder . setMessage ( msg)
                    . setCancelable ( true )

                    . setPositiveButton ( "Yes" , new DialogInterface. OnClickListener () {
                        public void onClick ( DialogInterface dialog , int id ) {
                              dialog.cancel ();
                        }
                    });
//                    .setNegativeButton ( "No" , new DialogInterface . OnClickListener () {
//                        public void onClick ( DialogInterface dialog , int id ) {
//                            dialog.cancel ();
//                        }
//                    });
            AlertDialog alert = builder.create ();
            alert.show();
//			result = new Toast(context);
//			TextView showText = new TextView(context);
//			showText.setText(msg);
//			showText.setTextColor(Color.WHITE);
//			int textSize = CommonFunction.getZoomX(12);
//			showText.setTextSize(textSize);//16
//			showText.setBackgroundResource(R.drawable.msg_background);
//			result.setView(showText);
//			result.setDuration(Toast.LENGTH_LONG);
//			result.setGravity(Gravity.CENTER, 0, 0);
//		}else{
//			((TextView)result.getView()).setText(msg);
//		}
//		result.show();
	}
}
