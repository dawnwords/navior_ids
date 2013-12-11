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

import com.navior.ips.model.POI;

public class POIParser implements ModelParser<POI> {
  @Override
  public POI getModel(Cursor c) {
    POI poi = new POI();
    poi.setId(c.getInt(c.getColumnIndex("id")));
    poi.setX(c.getFloat(c.getColumnIndex("x")));
    poi.setY(c.getFloat(c.getColumnIndex("y")));
    poi.setFloorId(c.getInt(c.getColumnIndex("floorId")));
    poi.setShopId(c.getInt(c.getColumnIndex("shopId")));
    poi.setPathId(c.getInt(c.getColumnIndex("pathId")));
    return poi;
  }

  @Override
  public ContentValues getContentValue(POI poi) {
    ContentValues cv = new ContentValues();
    cv.put("id", poi.getId());
    cv.put("x", poi.getX());
    cv.put("y", poi.getY());
    cv.put("floorId", poi.getFloorId());
    cv.put("shopId", poi.getShopId());
    cv.put("pathId", poi.getPathId());
    return cv;
  }
}
