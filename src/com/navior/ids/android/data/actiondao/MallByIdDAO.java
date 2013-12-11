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
package com.navior.ids.android.data.actiondao;

import android.content.Context;

public abstract class MallByIdDAO extends MallDAO {
  public MallByIdDAO(Context context) {
    super(context);
  }

  @Override
  protected String getUrl() {
    return "listMallOfCityByMallId.action\n";
  }

  @Override
  protected String selectSQL() {
    return "SELECT * FROM d_mall where mallId = ?";
  }

  @Override
  protected String arg0String() {
    return "mallId";
  }
}
