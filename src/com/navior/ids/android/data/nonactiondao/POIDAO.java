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

import android.database.sqlite.SQLiteDatabase;

import com.navior.ids.android.data.modelparser.ModelParser;
import com.navior.ids.android.data.modelparser.POIParser;
import com.navior.ips.model.POI;

public class POIDAO extends NonActionDAO<POI> {
  private POIParser parser;

  public POIDAO(SQLiteDatabase db) {
    super(db);
    parser = new POIParser();
  }

  @Override
  public String getSelectDataSQL() {
    return "SELECT * FROM d_poi WHERE floorId = ?";
  }

  @Override
  protected ModelParser<POI> getModelParser() {
    return parser;
  }

  @Override
  public String getTableName() {
    return "d_poi";
  }


}
