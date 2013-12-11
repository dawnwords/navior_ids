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
 * @date 13-9-10
 */
package com.navior.ids.android.data.nonactiondao;

import android.database.sqlite.SQLiteDatabase;

import com.navior.ids.android.data.modelparser.ModelParser;
import com.navior.ids.android.data.modelparser.PathParser;
import com.navior.ips.model.Path;

public class PathDAO extends NonActionDAO<Path> {
  private PathParser parser;

  public PathDAO(SQLiteDatabase db) {
    super(db);
    parser = new PathParser();
  }

  @Override
  public String getSelectDataSQL() {
    return "SELECT * from d_path where mallId = ?";
  }

  @Override
  protected ModelParser<Path> getModelParser() {
    return parser;
  }

  @Override
  public String getTableName() {
    return "d_path";
  }


}
