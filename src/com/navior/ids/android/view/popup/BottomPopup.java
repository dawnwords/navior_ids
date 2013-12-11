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
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.navior.ids.android.R;

public abstract class BottomPopup<T> extends PopupWindow {
  private View parent;
  private Activity activity;
  private Handler handler;

  public BottomPopup(Activity activity, int parentId, int layoutId) {
    super(View.inflate(activity.getApplicationContext(), layoutId, null),
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    setAnimationStyle(R.style.AnimBottom);
    this.parent = activity.findViewById(parentId);
    this.activity = activity;
    this.handler = new Handler();
  }

  public void show(final T model){
    handler.post(new Runnable() {
      @Override
      public void run() {
        displayModel(model);
      }
    });
  }

  protected abstract void displayModel(T model);

  protected void startActivity(Class<?> cls) {
    activity.startActivity(new Intent(activity, cls));
  }

  protected void showFromBottom() {
    showAtLocation(parent, Gravity.BOTTOM, 0, 0);
  }
}
