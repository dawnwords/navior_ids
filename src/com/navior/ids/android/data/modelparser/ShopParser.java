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

import com.navior.ids.android.utils.Util;
import com.navior.ips.model.Shop;

public class ShopParser implements ModelParser<Shop> {
  @Override
  public Shop getModel(Cursor c) {
    Shop shop = new Shop();
    shop.setId(c.getInt(c.getColumnIndex("id")));
    shop.setMallId(c.getInt(c.getColumnIndex("mallId")));
    shop.setFloorId(c.getInt(c.getColumnIndex("floorId")));
    shop.setRno(c.getString(c.getColumnIndex("rno")));
    shop.setT(c.getInt(c.getColumnIndex("t")));
    shop.setCgid(c.getInt(c.getColumnIndex("cgid")));
    shop.setIco(c.getInt(c.getColumnIndex("ico")));
    shop.setNm(c.getString(c.getColumnIndex("nm")));
    shop.setLogo(c.getString(c.getColumnIndex("logo")));
    shop.setLt(c.getDouble(c.getColumnIndex("lt")));
    shop.setLr(Util.getGson().fromJson(c.getString(c.getColumnIndex("lr")), float[].class));
    shop.setOp(Util.getGson().fromJson(c.getString(c.getColumnIndex("op")), byte[].class));
    shop.setShape(Util.getGson().fromJson(c.getString(c.getColumnIndex("shape")), float[].class));
    return shop;
  }

  @Override
  public ContentValues getContentValue(Shop shop) {
    ContentValues cv = new ContentValues();
    cv.put("id", shop.getId());
    cv.put("mallId", shop.getMallId());
    cv.put("floorId", shop.getFloorId());
    cv.put("rno", shop.getRno());
    cv.put("t", shop.getT());
    cv.put("cgid", shop.getCgid());
    cv.put("ico", shop.getIco());
    cv.put("nm", shop.getNm());
    cv.put("logo", shop.getLogo());
    cv.put("lt", shop.getLt());
    cv.put("lr", Util.getGson().toJson(shop.getLr(), float[].class));
    cv.put("op", Util.getGson().toJson(shop.getOp(), byte[].class));
    cv.put("shape", Util.getGson().toJson(shop.getShape(), float[].class));
    return cv;
  }
}
