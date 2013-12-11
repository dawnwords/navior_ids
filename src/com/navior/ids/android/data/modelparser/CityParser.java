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
 * @date 13-12-1
 */
package com.navior.ids.android.data.modelparser;

import android.content.ContentValues;
import android.database.Cursor;

import com.navior.ips.model.City;

public class CityParser implements ModelParser<City> {
  @Override
  public City getModel(Cursor c) {
    City city = new City();
    city.setId(c.getInt(c.getColumnIndex("id")));
    city.setName(c.getString(c.getColumnIndex("nm")));
    city.setAbbr(c.getString(c.getColumnIndex("abbr")));
    city.setCode(c.getString(c.getColumnIndex("code")));
    city.setLat(c.getDouble(c.getColumnIndex("lat")));
    city.setLng(c.getDouble(c.getColumnIndex("lng")));
    city.setMapsize(c.getDouble(c.getColumnIndex("mapsize")));
    return city;
  }

  @Override
  public ContentValues getContentValue(City city) {
    ContentValues result = new ContentValues();
    result.put("id", city.getId());
    result.put("nm", city.getName());
    result.put("abbr", city.getAbbr());
    result.put("code", city.getCode());
    result.put("lat", city.getLat());
    result.put("lng", city.getLng());
    result.put("mapsize", city.getMapsize());
    return result;
  }
}
