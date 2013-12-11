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
 * @date 13-12-2
 */
package com.navior.ids.android.data.modelparser;

import android.content.ContentValues;
import android.database.Cursor;

import com.navior.ips.model.Floor;

public class FloorParser implements ModelParser<Floor> {

  @Override
  public Floor getModel(Cursor c) {
    Floor floor = new Floor();
    floor.setId(c.getInt(c.getColumnIndex("id")));
    floor.setMallId(c.getInt(c.getColumnIndex("mallId")));
    floor.setNm(c.getString(c.getColumnIndex("nm")));
    floor.setW(c.getInt(c.getColumnIndex("w")));
    floor.setH(c.getInt(c.getColumnIndex("h")));
    floor.setBrief(c.getString(c.getColumnIndex("brief")));
    return floor;
  }

  @Override
  public ContentValues getContentValue(Floor floor) {
    ContentValues cv = new ContentValues();
    cv.put("id", floor.getId());
    cv.put("brief", floor.getBrief());
    cv.put("mallId", floor.getMallId());
    cv.put("nm", floor.getNm());
    cv.put("w", floor.getW());
    cv.put("h", floor.getH());
    return cv;
  }
}
