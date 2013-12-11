package com.navior.ids.android.service.locating.ids.component;

import com.navior.ips.model.Location;
import com.navior.ips.model.POS;

import java.util.HashMap;

/**
 * Created by wangxiayang on 4/11/13.
 */
public interface LocatingListener {

    HashMap< String, POS > getPosMap(String starname);

    void onNewLocation(Location location);

    void onFailedToStartScanning();

    void onFailedToLocate();

    void onBluetoothOff();

    void onBluetoothUnavailable();

    void onErrorInitializeBluetooth();
}
