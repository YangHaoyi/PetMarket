package com.lbt.petmarket.util;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.lbt.petmarket.R;
import com.lbt.petmarket.activity.WebActivity;


/**
 * Created by Administrator on 2015/9/7 0007.
 */
public class ShakeListenerUtils implements SensorEventListener
{
    private Activity context;

    public ShakeListenerUtils(Activity context)
    {
        super();
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        int sensorType = event.sensor.getType();
        float[] values = event.values;

        if (sensorType == Sensor.TYPE_ACCELEROMETER)
        {

            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
                    .abs(values[2]) > 17))
            {
                context.overridePendingTransition(R.anim.fade_in,
                        R.anim.fade_out);
                ((WebActivity)context).refresh();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        //当传感器精度改变时回调该方法，Do nothing.
    }

}