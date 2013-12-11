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
 * @date 13-12-4
 */
package com.navior.ids.android.view.popup;

import android.app.Activity;
import android.widget.TextView;

import com.navior.ids.android.R;
import com.navior.ips.model.Shop;

public class ShopDetailPopup extends BottomPopup<Shop> {
  public ShopDetailPopup(Activity activity) {
    super(activity, R.id.indoor_popup, R.layout.popup_shop_detail);
  }

  @Override
  public void displayModel(Shop shop) {
    TextView shopNm = (TextView) getContentView().findViewById(R.id.popup_shop_nm);
    shopNm.setText(shop.getNm());
    showFromBottom();
  }
}
