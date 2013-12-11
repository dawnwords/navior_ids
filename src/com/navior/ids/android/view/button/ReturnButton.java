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
 * @date 13-11-20
 */
package com.navior.ids.android.view.button;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navior.ids.android.R;

public class ReturnButton extends RelativeLayout {
  private OnClickListener onClickListener;
  private TextView returnText;
  private ImageView arrow;

  public ReturnButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    final Resources resources = context.getResources();
    final String back = resources.getString(R.string.back);
    final int warpContent = LayoutParams.WRAP_CONTENT;
    final int textSize = (int) resources.getDimension(R.dimen.btn_select_city_font);
    final int padding = (int) resources.getDimension(R.dimen.nav_padding);
    final int arrowW = (int) resources.getDimension(R.dimen.nav_arrow_down_h);
    final int arrowH = (int) resources.getDimension(R.dimen.nav_arrow_down_w);

    returnText = new TextView(context);
    LayoutParams params = new LayoutParams(warpContent, warpContent);
    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    params.addRule(RelativeLayout.CENTER_VERTICAL);
    returnText.setLayoutParams(params);
    returnText.setTextColor(resources.getColorStateList(R.color.nav_text_color));
    returnText.setTextSize(textSize);
    returnText.setText(back);

    arrow = new ImageView(context);
    LayoutParams params1 = new LayoutParams(arrowW, arrowH);
    params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    params1.addRule(RelativeLayout.CENTER_VERTICAL);
    arrow.setLayoutParams(params1);
    arrow.setScaleType(ImageView.ScaleType.FIT_XY);
    arrow.setImageDrawable(resources.getDrawable(R.drawable.nav_arrow_left));

    addView(returnText);
    addView(arrow);

    setPadding(padding, padding, padding, padding);
    onClickListener = new OnClickListener() {
      @Override
      public void onClick(View v) {
        arrow.performClick();
        returnText.performClick();
      }
    };
    super.setOnClickListener(onClickListener);
  }

  @Override
  public void setOnClickListener(final OnClickListener l) {
    super.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickListener.onClick(v);
        l.onClick(v);
      }
    });
  }
}
