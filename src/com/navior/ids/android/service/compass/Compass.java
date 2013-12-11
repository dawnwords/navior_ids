/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2010 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author cs1
 * @date 13-10-8
 */
package com.navior.ids.android.service.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class Compass {
  private double northRadius;
  private boolean shouldRecord;

  private SensorManager sensorManager;
  private Sensor magneticSensor;
  private SensorEventListener magneticSensorEventListener;

  public Compass(Context context) {
    sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    magneticSensorEventListener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent e) {
        double x = e.values[0];
        double y = e.values[1];
        double yAngel = Math.acos(y / Math.sqrt(x * x + y * y)) * x / Math.abs(x);
        if (!Double.isNaN(yAngel)) {
          onDirectionChange(getCorrectYAngel(yAngel));
          if (shouldRecord) {
            northRadius = yAngel;
            shouldRecord = false;
          }
        }

      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int i) {
      }
    };
  }

  public double getCorrectYAngel(double yAngle) {
    return (northRadius - yAngle + 2 * Math.PI) % (2 * Math.PI);
  }

  public void start() {
    if (magneticSensor != null) {
      sensorManager.registerListener(magneticSensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    }
  }

  public void stop() {
    if (magneticSensor != null) {
      sensorManager.unregisterListener(magneticSensorEventListener);
    }
  }

  public void correct() {
    shouldRecord = true;
  }

  protected abstract void onDirectionChange(double radius);
}
