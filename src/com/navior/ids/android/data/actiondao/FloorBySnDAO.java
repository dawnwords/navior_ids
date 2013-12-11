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

public abstract class FloorBySnDAO extends FloorDAO {
  public FloorBySnDAO(Context context) {
    super(context);
  }

  @Override
  protected String getUrl() {
    return "listFloorOfMallByStarSn.action";
  }

  @Override
  protected String selectSQL() {
    return "SELECT * FROM d_floor where mallId in " +
        "(SELECT mallId FROM d_floor WHERE id in " +
        "(SELECT floorId FROM d_pos WHERE starSn = ?))";
  }

  @Override
  protected String arg0String() {
    return "starSn";
  }
}
