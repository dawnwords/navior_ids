package com.navior.ids.android.idslocating.component;

import android.bluetooth.BluetoothDevice;

import com.navior.ids.android.idslocating.data.RssiRecord;
import com.navior.ips.model.Location;
import com.navior.ips.model.POS;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wangxiayang on 4/11/13.
 */
public interface LocatingListener {

    HashMap< String, POS > getPosMap(String starname);

    void onNewLocation(Location location, List<RssiRecord> rssiRecords);

    void onNewRssi(BluetoothDevice device, int rssi, byte[] bytes);

    void onFailedToStartScanning();

    void onFailedToLocate();

    void onBluetoothOff();

    void onBluetoothUnavailable();

    void onErrorInitializeBluetooth();
}
