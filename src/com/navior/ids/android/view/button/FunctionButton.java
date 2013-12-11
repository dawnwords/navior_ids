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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navior.ids.android.R;

public class FunctionButton extends RelativeLayout {

  private OnClickListener onClickListener;

  public FunctionButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FunctionButton);
    final Drawable drawable = a.getDrawable(R.styleable.FunctionButton_image);
    final String textString = a.getString(R.styleable.FunctionButton_text);
    final ColorStateList textColor = a.getColorStateList(R.styleable.FunctionButton_textColor);
    a.recycle();

    final ImageView image = new ImageView(context);
    final int btnSize = (int) context.getResources().getDimension(R.dimen.function_btn_size);
    LayoutParams params = new LayoutParams(btnSize, btnSize);
    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
    image.setLayoutParams(params);
    image.setImageDrawable(drawable);
    image.setScaleType(ImageView.ScaleType.FIT_XY);

    final TextView text = new TextView(context);
    LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
    text.setLayoutParams(params1);
    text.setGravity(Gravity.CENTER_HORIZONTAL);
    text.setText(textString);
    text.setTextColor(textColor);

    addView(image);
    addView(text);

    final int padding = (int) context.getResources().getDimension(R.dimen.function_btn_padding);
    setPadding(padding, padding, padding, padding);
    onClickListener = new OnClickListener() {
      @Override
      public void onClick(View v) {
        image.performClick();
        text.performClick();
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
