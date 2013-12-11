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

import com.navior.ips.model.Mall;

public class MallParser implements ModelParser<Mall> {
  @Override
  public Mall getModel(Cursor c) {
    Mall mall = new Mall();
    mall.setId(c.getInt(c.getColumnIndex("id")));
    mall.setCityId(c.getInt(c.getColumnIndex("cityId")));
    mall.setLat(c.getDouble(c.getColumnIndex("lat")));
    mall.setLng(c.getDouble(c.getColumnIndex("lng")));
    mall.setLogo(c.getString(c.getColumnIndex("logo")));
    mall.setType(c.getInt(c.getColumnIndex("type")));
    mall.setNm(c.getString(c.getColumnIndex("nm")));
    mall.setW(c.getInt(c.getColumnIndex("w")));
    mall.setH(c.getInt(c.getColumnIndex("h")));
    mall.setAddr(c.getString(c.getColumnIndex("addr")));
    mall.setFocus_count(c.getInt(c.getColumnIndex("focus_count")));
    return mall;
  }

  @Override
  public ContentValues getContentValue(Mall mall) {
    ContentValues cv = new ContentValues();
    cv.put("id", mall.getId());
    cv.put("cityId", mall.getCityId());
    cv.put("lat", mall.getLat());
    cv.put("lng", mall.getLng());
    cv.put("logo", mall.getLogo());
    cv.put("type", mall.getType());
    cv.put("nm", mall.getNm());
    cv.put("w", mall.getW());
    cv.put("h", mall.getH());
    cv.put("addr", mall.getAddr());
    cv.put("focus_count", mall.getFocus_count());
    return cv;
  }
}
