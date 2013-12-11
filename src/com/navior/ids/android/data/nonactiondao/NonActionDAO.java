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
 * @date 13-8-12
 */
package com.navior.ids.android.data.nonactiondao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.navior.ids.android.data.modelparser.ModelParser;

import java.util.LinkedList;
import java.util.List;

public abstract class NonActionDAO<T> {
  protected SQLiteDatabase db;

  public NonActionDAO(SQLiteDatabase db) {
    this.db = db;
  }

  public void insert(List<T> dataList) {
    for (T model : dataList) {
      db.insert(getTableName(), null, getModelParser().getContentValue(model));
    }
  }

  public List<T> fetch(String[] args) {
    LinkedList<T> result = new LinkedList<T>();
    Cursor c2 = db.rawQuery(getSelectDataSQL(), args);
    while (c2.moveToNext()) {
      result.add(getModelParser().getModel(c2));
    }
    return result;
  }

  protected abstract String getTableName();

  protected abstract String getSelectDataSQL();

  protected abstract ModelParser<T> getModelParser();
}
