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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.navior.ids.android.idslocating.data.RssiRecord;


public class JellyBeanSampler extends Sampler {

    // fields initialized in constructor
    private BluetoothAdapter mBluetoothAdapter;
    // fields initialized here
    private BluetoothAdapter.LeScanCallback mCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            onScanNewResult(bluetoothDevice, rssi, bytes);
        }
    };

    public JellyBeanSampler(Context context, SamplerListener listener) {
        super(listener);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if(mBluetoothAdapter == null) {
                // todo Reason?
                onActiveBtError();
            }
            else {
                // Android 4.3 API is much simpler than Samsung's
                setStateReady();
            }
        }
        else {
            // todo Reason?
            onActiveBtError();
        }
    }

    @Override
    public boolean startScan() {
        return mBluetoothAdapter.startLeScan(mCallback);
    }

    @Override
    public void stopScan() {
        mBluetoothAdapter.stopLeScan(mCallback);
    }

    @Override
    public void reinitialize() {
        // nothing
    }

    @Override
    public void recycle() {
        // nothing
    }
}
