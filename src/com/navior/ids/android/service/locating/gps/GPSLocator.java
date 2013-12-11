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
 * @date 13-10-30
 */
package com.navior.ids.android.service.locating.gps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

public abstract class GPSLocator implements AMapLocationListener {
  private LocationManagerProxy mAMapLocManager;

  public void enableGPS(Context context) {
    if (mAMapLocManager == null) {
      mAMapLocManager = LocationManagerProxy.getInstance(context);
    }
    mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 1000, 10, this);
  }

  public void disableGPS() {
    if (mAMapLocManager != null) {
      mAMapLocManager.removeUpdates(this);
    }
  }

  public void destroyGPS() {
    if (mAMapLocManager != null) {
      mAMapLocManager.removeUpdates(this);
      mAMapLocManager.destory();
    }
    mAMapLocManager = null;
  }

  @Override
  public void onLocationChanged(Location location) {
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }

  @Override
  public void onProviderEnabled(String provider) {
  }

  @Override
  public void onProviderDisabled(String provider) {
  }
}
