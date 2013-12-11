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
 * @date 13-11-19
 */
package com.navior.ids.android.view.popup;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.navior.ids.android.R;
import com.navior.ids.android.activity.IndoorActivity;
import com.navior.ids.android.data.Parameter;
import com.navior.ips.model.Mall;

public class MallDetailPopup extends BottomPopup<Mall> {

  public MallDetailPopup(Activity activity) {
    super(activity, R.id.outdoor_popup, R.layout.popup_mall_detail);
  }

  @Override
  public void displayModel(final Mall mall) {
    TextView mallName = (TextView) getContentView().findViewById(R.id.popup_mall_detail_title);
    Button enter = (Button) getContentView().findViewById(R.id.popup_mall_detail_enter);

    mallName.setText(mall.getNm());
    enter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Parameter.getInstance().setCurrentMall(mall);
        startActivity(IndoorActivity.class);
      }
    });
    showFromBottom();
  }
}
