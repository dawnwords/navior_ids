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
package com.navior.ids.android.view.button;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navior.ids.android.R;

public class CitySelectButton extends RelativeLayout {
  private final OnClickListener onClickListener;
  private TextView cityName;
  private final ImageView arrow;

  public CitySelectButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    final Resources resources = context.getResources();
    final String city = resources.getString(R.string.city);
    final int warpContent = LayoutParams.WRAP_CONTENT;
    final int textSize = (int) resources.getDimension(R.dimen.btn_select_city_font);
    final int padding = (int) resources.getDimension(R.dimen.nav_padding);
    final int arrowW = (int) resources.getDimension(R.dimen.nav_arrow_down_w);
    final int arrowH = (int) resources.getDimension(R.dimen.nav_arrow_down_h);

    cityName = new TextView(context);
    LayoutParams params = new LayoutParams(warpContent, warpContent);
    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    params.addRule(RelativeLayout.CENTER_VERTICAL);
    cityName.setLayoutParams(params);
    cityName.setTextColor(resources.getColorStateList(R.color.nav_text_color));
    cityName.setTextSize(textSize);
    cityName.setText(city);

    arrow = new ImageView(context);
    LayoutParams params1 = new LayoutParams(arrowW, arrowH);
    params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    params1.addRule(RelativeLayout.CENTER_VERTICAL);
    arrow.setLayoutParams(params1);
    arrow.setScaleType(ImageView.ScaleType.FIT_XY);
    arrow.setImageDrawable(resources.getDrawable(R.drawable.nav_arrow_down));

    addView(cityName);
    addView(arrow);

    setPadding(padding, padding, padding, padding);
    onClickListener = new OnClickListener() {
      @Override
      public void onClick(View v) {
        arrow.performClick();
        cityName.performClick();
      }
    };
    super.setOnClickListener(onClickListener);
  }

  public void setText(String text) {
    cityName.setText(text);
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
