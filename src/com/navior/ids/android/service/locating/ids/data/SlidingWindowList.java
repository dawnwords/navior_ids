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
 * @date 23/09/13
 */
package com.navior.ids.android.service.locating.ids.data;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class SlidingWindowList<E extends Weighted<Date>> extends LinkedList<E> {

  public static final int DEFAULT_TEMPORAL_WINDOW_SIZE = 10000;

  private long temporalWindowSize;

  public SlidingWindowList() {
    temporalWindowSize = DEFAULT_TEMPORAL_WINDOW_SIZE;
  }

  @Override
  public boolean add(E newElement) {
    boolean result = super.add(newElement);
    trimByTemporalWindowSize();
    return result;
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    boolean result = true;
    for (E e : collection) {
      result &= add(e);
    }
    return result;
  }

  private void trimByTemporalWindowSize() {
    Date currentTime = new Date(System.currentTimeMillis() - temporalWindowSize);
    while (size() > 0 && (getFirst().getWeight().compareTo(currentTime)) < 0) {
      removeFirst();
    }
  }
}
