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
package com.navior.ids.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.navior.ids.android.R;
import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.data.actiondao.FloorBySnDAO;
import com.navior.ids.android.idslocating.component.LocatingService;
import com.navior.ips.model.Floor;

import java.util.Vector;

public class WelcomeActivity extends Activity {
  private static final int MAX_VIEWS = 5;
  private Button startMainActivityButton;
  private static final int IDS_TIMEOUT = 5000;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startService(new Intent(this, LocatingService.class));

    SharedPreferences settings = getSharedPreferences(Parameter.NAVIOR, 0);
    if (settings.getBoolean(Parameter.APP_START_FIRST_TIME, true)) {
      showFirstTimePage();
      settings.edit().putBoolean(Parameter.APP_START_FIRST_TIME, false).commit();
    } else {
      showWelcomePage();
    }
  }

  private void showWelcomePage() {
    setContentView(R.layout.activity_welcome);
    switchActivity(OutdoorActivity.class);
    new FloorBySnDAO(getApplicationContext()) {
      @Override
      protected String[] getArgs() {
        return new String[]{"87654382"};
      }

      @Override
      protected void doSuccess(Vector<Floor> floors) {

      }

      @Override
      protected void doException() {

      }
    }.execute();

  }

  private void showFirstTimePage() {
    setContentView(R.layout.activity_first_time);
    startMainActivityButton = (Button) findViewById(R.id.start_main_button);
    startMainActivityButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        showWelcomePage();
      }
    });

    ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
    mViewPager.setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return MAX_VIEWS;
      }

      @Override
      public boolean isViewFromObject(View view, Object object) {
        return view == object;
      }

      @Override
      public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(WelcomeActivity.this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setImageResource(R.drawable.startpage_image1 + position);
        container.addView(imageView, 0);
        return imageView;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
      }
    });
    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrollStateChanged(int arg0) {
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
      }

      @Override
      public void onPageSelected(int position) {
        if (position == MAX_VIEWS - 1) {
          startMainActivityButton.setVisibility(View.VISIBLE);
        } else {
          startMainActivityButton.setVisibility(View.INVISIBLE);
        }
      }
    });
  }

  private void switchActivity(Class<?> activityClass) {
    startActivity(new Intent(this, activityClass));
    finish();
  }

}