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
 * @date 13-12-16
 */
package com.navior.ids.android.view.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.navior.ids.android.R;

public class DimenSwitchButton extends Button {
  private boolean is3D;

  public DimenSwitchButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    is3D = true;
    switchDimension();
  }

  @Override
  public void setOnClickListener(final OnClickListener l) {
    super.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        switchDimension();
        l.onClick(v);
      }
    });
  }

  private void switchDimension() {
    setBackgroundResource(is3D ? R.drawable.dimen_three : R.drawable.dimen_two);
    is3D = !is3D;
  }
}
