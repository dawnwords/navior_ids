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
import android.content.Context;
import android.os.Build;

import com.navior.ids.android.idslocating.data.RssiRecord;
import com.navior.ids.android.idslocating.data.SlidingWindowList;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Sampler {

    private RssiStorage tempStorage;
    private SamplerListener callerListener;

    protected Sampler(SamplerListener callerListener) {
        this.callerListener = callerListener;
        tempStorage = new RssiStorage();
    }

    // public methods

    /**
     * Initialize sampler may block user's thread.
     * Caller MUST branch new thread to call this method.
     * It's just a factory, not singleton creator.
     * @param context indicates which context binds to system's Bluetooth service
     * @param listener
     * @return
     */
    public static Sampler getSampler(Context context, SamplerListener listener) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk == 17) {
            // todo check whether Samsung Galaxy
            return new SamsungSampler(context, listener);
        } else {
            return new JellyBeanSampler(context, listener);
        }
    }

    /**
     * Call this method to get rssi records.
     *
     * @return
     */
    public LinkedList<RssiRecord> getRecords() {
        return tempStorage.getRecords();
    }

    /**
     * For caller checking buffer status.
     *
     * @return true if new rssi in buffer since last check
     */
    public boolean hasNewRecord() {
        return tempStorage.hasNewRecord();
    }

    /**
     * In Android 4.3 api, startScan() may block the user's thread when Bluetooth is off, since it retries to startScan and returns false when finally fails.
     * The caller MUST branch a new thread to call this method.
     * @return true if success
     */
    public abstract boolean startScan();

    /**
     * No result returned.
     */
    public abstract void stopScan();

    /**
     * Future apis may need this method.
     * Apis at present help caller do reinitializing. So no need to call this method.
     */
    public abstract void reinitialize();

    /**
     * Caller MUST explicitly call recycle() to unbind Bluetooth Service connection, though on some platforms not necessary.
     */
    public abstract void recycle();

    // end of public methods

    // protected methods

    /**
     * Tell caller the sampler is ready.
     * Only call this method in subclasses, avoiding access callerListener in subclass.
     */
    protected void setStateReady() {
        callerListener.onStateReady();
    }

    /**
     * Tell caller the sampler is not ready.
     * see Sampler.setStateReady() for more explanation of the use.
     */
    protected void setStateNotReady() {
        callerListener.onStateNotReady();
    }

    /**
     * Tell caller error in initialize Bluetooth.
     * see Sampler.setStateReady() for more explanation of the use.
     */
    protected void onActiveBtError() {
        callerListener.onActiveBtServiceError();
    }

    /**
     * Tell caller new record has been found.
     * @param bluetoothDevice contains device name
     * @param rssi rssi value
     * @param bytes I have never used it.
     */
    protected void onScanNewResult(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
        // device name should be mapped to understandable name using Util.encode()
        String bluetoothDeviceName = Util.encode(bluetoothDevice.getName());
        callerListener.onNewRecord(bluetoothDevice, rssi, bytes);
        // if no other thread is inserting record
        if (!tempStorage.addNewRecord(new RssiRecord(bluetoothDeviceName, rssi))) {
            callerListener.onNewRecord();
        }
    }

    // end of protected methods

    /**
     * Override this class to receive message from sampler.
     */
    public static class SamplerListener {
        public void onStateReady() {}

        public void onStateNotReady() {}

        public void onActiveBtServiceError() {}

        public void onNewRecord(){}

        public void onNewRecord(BluetoothDevice device, int rssi, byte[] bytes) {}
    }

    private class RssiStorage {

        private SlidingWindowList<RssiRecord> slidingWindow;    // contains rssi records ready to be read
        private ConcurrentLinkedQueue<RssiRecord> buffer;   // contains newly-added rssi records
        private Object lock;
        private boolean isReading;  // indicate someone is reading the slidingWindow

        private RssiStorage() {
            buffer = new ConcurrentLinkedQueue<RssiRecord>();
            slidingWindow = new SlidingWindowList<RssiRecord>();
            lock = new Object();
            isReading = false;
        }

        /**
         * @param rssiRecord
         * @return true if some other thread is reading the records, then the thread should insert and return
         */
        private boolean addNewRecord(RssiRecord rssiRecord) {
            // multiple threads may access the boolean variable
            synchronized (lock) {
                if (isReading) {
                    // only one thread can access the slidingWindow.
                    // other threads should insert into properly-protected buffer
                    buffer.add(rssiRecord);
                } else {
                    slidingWindow.add(rssiRecord);
                }
                return isReading;
            }
        }

        /**
         * @return true if there are new records in buffer since last check
         */
        private boolean hasNewRecord() {
            synchronized (lock) {
                // var name 'isReading' may be a little confusing
                isReading = !buffer.isEmpty(); // if buffer is empty, the caller thread should give up reading slidingWindow
                return isReading;
            }
        }

        private LinkedList<RssiRecord> getRecords() {
            synchronized (lock) {
                isReading = true;
                slidingWindow.addAll(buffer);
                buffer.clear();
            }

            // return a copy of the slidingWindow
            LinkedList<RssiRecord> result = new LinkedList<RssiRecord>();
            result.addAll(slidingWindow);
            return result;
        }
    }
}
