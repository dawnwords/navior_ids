/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2003 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author wangxiayang
 * @date 10/10/13
 */
package com.navior.ids.android.idslocating.data;

import com.navior.ips.model.POS;

public class PosDistance implements Comparable<PosDistance> {
  public POS pos;
  public double distance;
  public int rssi;
  public double rssiError;

  @Override
  public boolean equals(Object o) {
    return o instanceof PosDistance && ( pos != null && ((PosDistance) o).pos != null ) && pos.equals(((PosDistance) o).pos);
  }

  @Override
  public int compareTo(PosDistance posDistance) {
    return posDistance.rssi -  this.rssi;
  }
}
