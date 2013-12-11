package com.navior.ids.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.navior.ids.android.R;
import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.data.actiondao.CityDAO;
import com.navior.ids.android.view.list.CityIndexListView;
import com.navior.ips.model.City;

import java.util.LinkedList;
import java.util.Vector;

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
public class CityActivity extends Activity {
  public static final String SELECT_CITY = "select_city";
  public static final int REQUEST_CODE = 1725;

  private CityIndexListView cityList;
  private LinkedList<City> topCities;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initUI();
    fetchCity();
  }

  private void initUI() {
    setContentView(R.layout.activity_city);
    cityList = (CityIndexListView) findViewById(R.id.city_list);
    findViewById(R.id.city_return).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  private void fetchCity() {
    new CityDAO(getApplicationContext()) {
      @Override
      protected void doSuccess(final Vector<City> result) {
        setTopCities(result);
        cityList.setList(topCities, result, new CityIndexListView.OnCitySelectedListener() {
          @Override
          public void onCitySelected(int cityId) {
            for (City city : result) {
              if (city.getId() == cityId) {
                selectCity(city);
              }
            }
          }
        });
      }

      @Override
      protected void doException() {

      }
    }.execute();
  }

  private void selectCity(City city) {
    if (topCities.contains(city)) {
      topCities.remove(city);
    }
    topCities.addFirst(city);
    saveTopCities();

    Intent returnIntent = new Intent();
    returnIntent.putExtra(SELECT_CITY, city);
    setResult(RESULT_OK, returnIntent);
    finish();
  }

  private void setTopCities(Vector<City> cities) {
    topCities = new LinkedList<City>();
    SharedPreferences settings = getSharedPreferences(Parameter.NAVIOR, 0);
    for (int i = 0; i < Parameter.MAX_TOP_CITY_NUM; i++) {
      int topCityId = settings.getInt(Parameter.TOP_CITIES + i, -1);
      if (topCityId > 0) {
        for (City city : cities) {
          if (city.getId() == topCityId) {
            topCities.add(city);
            break;
          }
        }
      }
    }
  }

  private void saveTopCities() {
    SharedPreferences.Editor edit = getSharedPreferences(Parameter.NAVIOR, 0).edit();
    if (edit != null) {
      for (int i = 0; i < Parameter.MAX_TOP_CITY_NUM && i < topCities.size(); i++) {
        edit.putInt(Parameter.TOP_CITIES + i, topCities.get(i).getId());
      }
      edit.commit();
    }
  }
}