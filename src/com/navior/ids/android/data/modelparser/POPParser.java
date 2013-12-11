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

import com.navior.ips.model.POP;

public class POPParser implements ModelParser<POP> {
  @Override
  public POP getModel(Cursor c) {
    POP pop = new POP();
    pop.setId(c.getInt(c.getColumnIndex("id")));
    pop.setFloorId(c.getInt(c.getColumnIndex("floorId")));
    pop.setX(c.getFloat(c.getColumnIndex("x")));
    pop.setY(c.getFloat(c.getColumnIndex("y")));
    return pop;
  }

  @Override
  public ContentValues getContentValue(POP pop) {
    ContentValues cv = new ContentValues();
    cv.put("id", pop.getId());
    cv.put("floorId", pop.getFloorId());
    cv.put("x", pop.getX());
    cv.put("y", pop.getY());
    return cv;
  }
}
