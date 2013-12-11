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
 * @date 29/08/13
 */
package com.navior.ids.android.service.locating.ids.data;

import java.util.Date;

/**
 * record model for RSSI and star name pair, with a Data record for distinguishing different records
 */
public class RssiRecord implements Weighted<Date> {
  private int rssi;
  private int error;
  private String starName;
  private Date recordTime;

  public RssiRecord(String starName, int rssi) {
    setRssi(rssi);
    setStarName(starName);
    recordTime = new Date();
  }

  public int getRssi() {
    return rssi;
  }

  public void setRssi(int rssi) {
    this.rssi = rssi;
  }

  public String getStarName() {
    return starName;
  }

  public void setStarName(String starName) {
    this.starName = starName;
  }

  public Date getRecordTime() {
    return recordTime;
  }

  @Override
  public int hashCode() {
    return starName.hashCode() + rssi + recordTime.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof RssiRecord) {
      return starName.equals(((RssiRecord) o).getStarName()) && rssi == ((RssiRecord) o).getRssi() && recordTime.equals(((RssiRecord) o).getRecordTime());
    }
    return false;
  }


  @Override
  public Comparable<Date> getWeight() {
    return recordTime;
  }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}