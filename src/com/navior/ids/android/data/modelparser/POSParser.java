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

import com.navior.ips.model.POS;

public class POSParser implements ModelParser<POS> {
  @Override
  public POS getModel(Cursor c) {
    POS result = new POS();
    result.setId(c.getInt(c.getColumnIndex("id")));
    result.setFloorId(c.getInt(c.getColumnIndex("floorId")));
    result.setStarSn(c.getLong(c.getColumnIndex("starSn")));
    result.setX(c.getFloat(c.getColumnIndex("x")));
    result.setY(c.getFloat(c.getColumnIndex("y")));
    return result;
  }

  @Override
  public ContentValues getContentValue(POS pos) {
    ContentValues cv = new ContentValues();
    cv.put("id", pos.getId());
    cv.put("floorId", pos.getFloorId());
    cv.put("starSn", pos.getStarSn());
    cv.put("x", pos.getX());
    cv.put("y", pos.getY());
    return cv;
  }
}
