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
 * @date 13-11-6
 */
package com.navior.ids.android.data.actiondao;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.reflect.TypeToken;
import com.navior.ids.android.data.modelparser.MallParser;
import com.navior.ids.android.data.nonactiondao.PathDAO;
import com.navior.ips.model.Mall;

import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Vector;

public abstract class MallDAO extends DAO<Vector<Mall>> {
  private MallParser parser;
  private PathDAO pathDao;

  public MallDAO(Context context) {
    super(context);
    parser = new MallParser();
  }

  @Override
  protected void initDB() {
    super.initDB();
    pathDao = new PathDAO(db);
  }

  @Override
  protected Type getType() {
    return new TypeToken<Vector<Mall>>() {
    }.getType();
  }

  @Override
  protected String getUrl() {
    return "listMallOfCity.action";
  }

  @Override
  protected Vector<Mall> doSelect() throws Exception {
    Vector<Mall> malls = new Vector<Mall>();
    Cursor c = db.rawQuery(selectSQL(), getArgs());
    while (c.moveToNext()) {
      Mall mall = parser.getModel(c);
      mall.setPs(pathDao.fetch(new String[]{mall.getId() + ""}));
      malls.add(mall);
    }

    if (malls.size() == 0) {
      throw new Exception();
    }
    return malls;
  }

  @Override
  protected void doInsert(Vector<Mall> malls) {
    if (malls == null) {
      return;
    }
    db.beginTransaction();
    for (Mall mall : malls) {
      db.insert("d_mall", null, parser.getContentValue(mall));
      pathDao.insert(mall.getPs());
    }
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  @Override
  protected void setNameValuePair(LinkedList<BasicNameValuePair> pairs) {
    pairs.add(new BasicNameValuePair(arg0String(), getArgs()[0]));
  }

  protected String selectSQL() {
    return "SELECT * FROM d_mall WHERE cityId=?";
  }

  protected String arg0String() {
    return "cityId";
  }
}
