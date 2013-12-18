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
package com.navior.ids.android.idslocating.component.sampler;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.navior.ids.android.idslocating.data.RssiRecord;
import com.samsung.android.sdk.bt.gatt.BluetoothGatt;
import com.samsung.android.sdk.bt.gatt.BluetoothGattAdapter;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCallback;

import java.util.Timer;
import java.util.TimerTask;

public class SamsungSampler extends Sampler {

    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mCallback = new BluetoothGattCallback() {

        @Override
        public void onAppRegistered(int i) {
            setStateReady();
        }

        @Override
        public void onScanResult(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            onScanNewResult(bluetoothDevice, rssi, bytes);
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
            // may be redundant
            mBluetoothGatt.stopScan();
            mBluetoothGatt.unregisterApp();
            mBluetoothGatt = null;
        }
    };


    public SamsungSampler(Context context, SamplerListener listener) {
        super(listener);

        if (!BluetoothGattAdapter.getProfileProxy(context, mServiceListener, BluetoothGattAdapter.GATT)) {
            // bluetooth may be unavailable
            // todo reason?
            onActiveBtError();
        }
    }

    @Override
    public boolean startScan() {
        return mBluetoothGatt.startScan();
    }

    @Override
    public void stopScan() {
        mBluetoothGatt.stopScan();
    }

    @Override
    public void reinitialize() {
        // nothing to do. Samsung ble service will restart and register this app automatically.
    }

    /**
     * Don't forget to call this, or some resources may 'leak'.
     */
    @Override
    public void recycle() {
        mBluetoothGatt.unregisterApp();
        BluetoothGattAdapter.closeProfileProxy(BluetoothGattAdapter.GATT, mBluetoothGatt);
    }
}
