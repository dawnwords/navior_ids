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
 * @date 13-11-5
 */
package com.navior.ids.android.data.actiondao;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.reflect.TypeToken;
import com.navior.ids.android.data.modelparser.CityParser;
import com.navior.ips.model.City;

import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Vector;

public abstract class CityDAO extends DAO<Vector<City>> {
  private CityParser parser;

  public CityDAO(Context context) {
    super(context);
    parser = new CityParser();
  }

  @Override
  protected Type getType() {
    return new TypeToken<Vector<City>>() {
    }.getType();
  }

  @Override
  protected String getUrl() {
    return "listCity.action";
  }

  @Override
  protected void setNameValuePair(LinkedList<BasicNameValuePair> pairs) {
  }

  @Override
  protected String[] getArgs() {
    return null;
  }

  @Override
  protected Vector<City> doSelect() {
    Vector<City> result = new Vector<City>();
    Cursor c = db.rawQuery("SELECT * FROM d_city", null);
    while (c.moveToNext()) {
      result.add(parser.getModel(c));
    }
    return result;
  }

  @Override
  protected void doInsert(Vector<City> cities) {
    if (cities == null) {
      return;
    }

    db.beginTransaction();
    for (City city : cities) {
      db.insert("d_city", null, parser.getContentValue(city));
    }
    db.setTransactionSuccessful();
    db.endTransaction();
  }
}
