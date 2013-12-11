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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.navior.ids.android.service.locating.ids.data.RssiRecord;
import com.samsung.android.sdk.bt.gatt.BluetoothGatt;
import com.samsung.android.sdk.bt.gatt.BluetoothGattAdapter;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCallback;

import java.util.Timer;
import java.util.TimerTask;

public class SamsungSampler extends Sampler {

  private final static long BT_START_TIME_MAX = 5000;
  // fields initialized on first use
  private Timer timer;  // set a upper time bound in activating the Bt device
  // fields initialized with caller parameter
  // fields initialized in constructor
  private BluetoothGatt mBluetoothGatt;
  // fields initialized here
  private BluetoothGattCallback mCallback = new BluetoothGattCallback() {

    @Override
    public void onAppRegistered(int i) {
      timer.cancel();
      timer = new Timer();
      setStateReady();
    }

    @Override
    public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
      String bluetoothDeviceName = Util.encode(bluetoothDevice.getName());
      // if no other thread is inserting record
      if (!tempStorage.addNewRecord(new RssiRecord(bluetoothDeviceName, rssi))) {
        callerListener.onNewRecord();
      }
    }
  };
  private BluetoothProfile.ServiceListener mServiceListener = new BluetoothProfile.ServiceListener() {
    @Override
    public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
      if (profile == BluetoothGattAdapter.GATT) {
        mBluetoothGatt = (BluetoothGatt) bluetoothProfile;
        mBluetoothGatt.registerApp(mCallback);
      }
    }

    @Override
    public void onServiceDisconnected(int i) {
      setStateNotReady();
      mBluetoothGatt.stopScan();
      mBluetoothGatt.unregisterApp();
      mBluetoothGatt = null;
    }
  };


  public SamsungSampler(Context context, SamplerListener listener) {
    super( listener );

      if (!BluetoothGattAdapter.getProfileProxy(context, mServiceListener, BluetoothGattAdapter.GATT)) {
          callerListener.onActiveBtServiceError();
      } else {
          timer = new Timer();
          timer.schedule(new TimerTask() {
              @Override
              public void run() {
                  callerListener.onActiveBtServiceError();
              }
          }, BT_START_TIME_MAX);
      }
  }

  @Override
  public void startScan() {
    if (!mBluetoothGatt.startScan()) {
      callerListener.onStartScanningError();
    }
  }

  @Override
  public void stopScan() {
    mBluetoothGatt.stopScan();
  }

    @Override
    public void reinitialize() {
        // nothing to do. Samsung ble service will restart and register this app automatically.
    }

    @Override
    public void recycle() {
        mBluetoothGatt.unregisterApp();
        BluetoothGattAdapter.closeProfileProxy(BluetoothGattAdapter.GATT, mBluetoothGatt);
    }
}
