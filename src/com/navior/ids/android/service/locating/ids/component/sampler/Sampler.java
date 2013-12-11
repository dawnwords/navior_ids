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
 * @date 30/09/13
 */
package com.navior.ids.android.service.locating.ids.component.sampler;


import com.navior.ids.android.service.locating.ids.data.SlidingWindowList;
import com.navior.ids.android.service.locating.ids.data.RssiRecord;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Sampler {

  protected boolean isReady;
  protected RssiStorage tempStorage;
  protected SamplerListener callerListener;

  protected Sampler( SamplerListener callerListener ) {
      isReady = false;
      setCallerListener( callerListener );
    tempStorage = new RssiStorage();
  }

    private void setCallerListener( SamplerListener listener ) {
        this.callerListener = listener;
    }

  /**
   * For locator getting records.
   *
   * @return
   */
  public LinkedList<RssiRecord> getRecords() {
    return tempStorage.getRecords();
  }

  /**
   * For locator checking buffer status
   *
   * @return
   */
  public boolean hasNewRecord() {
    return tempStorage.hasNewRecord();
  }

  public abstract void startScan();

  public abstract void stopScan();

  public abstract void reinitialize();

  public abstract void recycle();

  public boolean getState() {
      return isReady;
  }

  protected void setStateReady() {
      isReady = true;
      callerListener.onStateReady();
  }

    protected void setStateNotReady() {
        isReady = false;
        callerListener.onStateNotReady();
    }

  /**
   * IDSLocator should implement it to receive message from sampler.
   */
  public interface SamplerListener {
    void onStateReady();

    void onStateNotReady();

    void onStartScanningError();

    void onActiveBtServiceError();

    void onNewRecord();
  }

  protected class RssiStorage {

    private SlidingWindowList<RssiRecord> slidingWindow;
    private ConcurrentLinkedQueue<RssiRecord> buffer;
    private boolean isReading;  // indicate someone is reading the slidingWindow

    protected RssiStorage() {
      buffer = new ConcurrentLinkedQueue<RssiRecord>();
      slidingWindow = new SlidingWindowList<RssiRecord>();
    }

    protected boolean addNewRecord(RssiRecord rssiRecord) {
      if (isReading) {
          buffer.add(rssiRecord);
      } else {
        slidingWindow.add(rssiRecord);
      }
      return isReading;
    }

    protected boolean hasNewRecord() {
        isReading = !buffer.isEmpty(); // if buffer is empty, the caller thread should give up reading slidingWindow
      return isReading;
    }

    protected LinkedList<RssiRecord> getRecords() {
      isReading = true;
        slidingWindow.addAll(buffer);
        buffer.clear();

      LinkedList<RssiRecord> result = new LinkedList<RssiRecord>();
      result.addAll(slidingWindow);
      return result;
    }
  }
}
