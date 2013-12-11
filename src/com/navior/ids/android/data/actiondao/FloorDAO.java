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
import com.navior.ids.android.data.modelparser.FloorParser;
import com.navior.ids.android.data.nonactiondao.POIDAO;
import com.navior.ids.android.data.nonactiondao.POPDAO;
import com.navior.ids.android.data.nonactiondao.POSDAO;
import com.navior.ids.android.data.nonactiondao.ShopDAO;
import com.navior.ips.model.Floor;

import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Vector;

public abstract class FloorDAO extends DAO<Vector<Floor>> {

  private POIDAO poiDAO;
  private POSDAO posDAO;
  private POPDAO popDAO;
  private ShopDAO shopDAO;

  private FloorParser parser;

  public FloorDAO(Context context) {
    super(context);
    parser = new FloorParser();
  }

  @Override
  protected void initDB() {
    super.initDB();
    poiDAO = new POIDAO(db);
    posDAO = new POSDAO(db);
    popDAO = new POPDAO(db);
    shopDAO = new ShopDAO(db);
  }

  @Override
  protected Type getType() {
    return new TypeToken<Vector<Floor>>() {
    }.getType();
  }

  @Override
  protected String getUrl() {
    return "listFloorOfMall.action";
  }

  @Override
  protected Vector<Floor> doSelect() throws Exception {
    Vector<Floor> result = new Vector<Floor>();
    Cursor c = db.rawQuery(selectSQL(), getArgs());
    while (c.moveToNext()) {
      Floor floor = parser.getModel(c);
      result.add(floor);
      String[] args = new String[]{floor.getId() + ""};
      floor.setPois(poiDAO.fetch(args));
      floor.setPoss(posDAO.fetch(args));
      floor.setPops(popDAO.fetch(args));
      floor.setG(shopDAO.fetch(args));
    }

    if (result.size() == 0) {
      throw new Exception();
    }
    return result;
  }

  @Override
  protected void setNameValuePair(LinkedList<BasicNameValuePair> pairs) {
    pairs.add(new BasicNameValuePair(arg0String(), getArgs()[0]));
  }

  @Override
  protected void doInsert(Vector<Floor> floors) {
    if (floors == null) {
      return;
    }

    db.beginTransaction();
    for (Floor floor : floors) {
      db.insert("d_floor", null, parser.getContentValue(floor));
      poiDAO.insert(floor.getPois());
      popDAO.insert(floor.getPops());
      posDAO.insert(floor.getPoss());
      shopDAO.insert(floor.getG());
    }
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  protected String selectSQL() {
    return "SELECT * FROM d_floor WHERE mallId=?";
  }

  protected String arg0String() {
    return "mallId";
  }
}
