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

import com.navior.ips.model.Path;

public class PathParser implements ModelParser<Path> {
  @Override
  public Path getModel(Cursor c) {
    Path path = new Path();
    path.setId(c.getInt(c.getColumnIndex("id")));
    path.setMallId(c.getInt(c.getColumnIndex("mallId")));
    path.setP1(c.getInt(c.getColumnIndex("p1")));
    path.setP2(c.getInt(c.getColumnIndex("p2")));
    path.setV(c.getInt(c.getColumnIndex("v")));
    return path;
  }

  @Override
  public ContentValues getContentValue(Path path) {
    ContentValues cv = new ContentValues();
    cv.put("id", path.getId());
    cv.put("mallId", path.getMallId());
    cv.put("p1", path.getP1());
    cv.put("p2", path.getP2());
    cv.put("v", path.getV());
    return cv;
  }
}
