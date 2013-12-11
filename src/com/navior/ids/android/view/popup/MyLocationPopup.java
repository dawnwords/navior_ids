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
 * @date 13-12-3
 */
package com.navior.ids.android.view.popup;

import android.app.Activity;
import android.view.View;

import com.navior.ids.android.R;
import com.navior.ips.model.Location;

public class MyLocationPopup extends BottomPopup<Location> {
  private View parent;
  private Activity activity;

  public MyLocationPopup(Activity activity, int parentId) {
    super(activity, parentId, R.layout.popup_my_location);
  }

  @Override
  public void displayModel(Location location) {

  }
}
