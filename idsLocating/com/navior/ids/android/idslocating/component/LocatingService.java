package com.navior.ids.android.idslocating.component;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.navior.ids.android.idslocating.component.sampler.Sampler;
import com.navior.ids.android.idslocating.data.RssiRecord;
import com.navior.ips.model.Location;
import com.navior.ips.model.POS;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangxiayang on 31/10/13.
 */
public class LocatingService extends Service {

    private final static long MAX_TIME_FOR_POS = 10000l;
    private final static long FIRST_WAIT_LOCATION_TIME = 4000l;
    private final static long LOCATION_REPORT_PERIOD = 4000l;
    private LocatingListener activityListener;
    private LocatingServiceState serviceState;
    private Calculator calculator;
    private LocationFilter locationFilter;
    private Timer locationReportTimer;
    private Timer calculatingTimer;
    private List<RssiRecord> aggregatedRecords;
    private Sampler sampler;
    // fields initialized here
    private Sampler.SamplerListener listener = new Sampler.SamplerListener() {

        @Override
        public void onActiveBtServiceError() {
            activityListener.onErrorInitializeBluetooth();
        }

        @Override
        public void onNewRecord(BluetoothDevice device, int rssi, byte[] bytes) {
            activityListener.onNewRssi(device, rssi, bytes);
        }

        @Override
        public void onNewRecord() {

            LinkedList<RssiRecord> records = sampler.getRecords();
            if (serviceState == LocatingServiceState.LOCATING) {
                computeLocation(records);
                // if got new rssi after the first calculation, continue to calculate
                while (serviceState == LocatingServiceState.LOCATING
                        && sampler.hasNewRecord()) {
                    computeLocation(sampler.getRecords());
                }
            }
        }

        private void computeLocation(LinkedList<RssiRecord> records) {

            aggregatedRecords = calculator.aggregate(records, null);

            // ask the activity for pos records
            RssiRecord recordExample = aggregatedRecords.get(0);

            HashMap<String, POS> posInfoMap = activityListener.getPosMap(recordExample.getStarName());
            // getPosMap() could return null
            if (posInfoMap == null) {
                // waiting for activity preparing the pos map
                calculatingTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // on timeout for requesting for pos
                        setServiceState(LocatingServiceState.SCANNING);
                        activityListener.onFailedToLocate();
                    }
                }, MAX_TIME_FOR_POS);
                return;
            } else {
                resetCalculatingTimer();
                aggregatedRecords = calculator.aggregate(records, posInfoMap);
                Location location = calculator.calculate(aggregatedRecords, posInfoMap);
                if (location != null) {
                    activityListener.onNewLocation(location, aggregatedRecords);
                    //locationFilter.input(location);
                } else {
                    activityListener.onFailedToLocate();
                }
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        activityListener.onBluetoothOff();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.ERROR:
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        serviceState = LocatingServiceState.NOT_AVAILABLE;
        calculator = new ThreePointCalculator();
        locationFilter = new AverageLocationFilter();
        locationReportTimer = new Timer();
        calculatingTimer = new Timer();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocatingBinder();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        if (sampler != null) {
            sampler.recycle();
        }
        super.onDestroy();
    }

    private void initializeSampler() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            setServiceState(LocatingServiceState.NOT_AVAILABLE);
            activityListener.onBluetoothUnavailable();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                setServiceState(LocatingServiceState.NOT_SCANNING);
                activityListener.onBluetoothOff();
            }
            // though bluetooth may be off, sampler can still be initialized with no error
            // process of start Samsung bt service may be blocked. so start a new thread to do it
            new Thread() {
                @Override
                public void run() {
                    sampler = Sampler.getSampler(LocatingService.this, listener);
                }
            }.start();
        }
    }

    private void startLocationReportTimer() {
        stopLocationReportTimer();
        locationReportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Location result = locationFilter.getOutput();
                if (result != null) {

                    activityListener.onNewLocation(result, aggregatedRecords);
                }
            }
        }, FIRST_WAIT_LOCATION_TIME, LOCATION_REPORT_PERIOD);
    }

    private void stopLocationReportTimer() {
        locationReportTimer.cancel();
        // each timer can be only cancelled once
        locationReportTimer = new Timer();
    }

    private void resetCalculatingTimer() {
        calculatingTimer.cancel();
        calculatingTimer = new Timer();
    }

    private void setServiceState(LocatingServiceState newState) {
        this.serviceState = newState;
    }

    private enum LocatingServiceState implements Comparable<LocatingServiceState> {

        NOT_AVAILABLE,
        NOT_SCANNING,
        SCANNING,
        LOCATING;
    }

    public class LocatingBinder extends Binder {
        public void setListener(LocatingListener listener) {
            activityListener = listener;
            // caller need feed back. So if we start initialize before the callback is passed in, caller will receive nothing when error happened
            initializeSampler();
        }

        /**
         * Start BLE scanning.
         *
         * @param willCalculateLocation true if caller wants it to calculate location when scanning
         */
        public void startScanning(final boolean willCalculateLocation) {
            if (sampler == null) {
                activityListener.onFailedToLocate();
                return;
            }

            // timer to avoid long time waiting for start scan
            final Timer startScanTimer = new Timer();
            startScanTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setServiceState(LocatingServiceState.NOT_SCANNING);
                    sampler.stopScan();
                    reportScanningError(willCalculateLocation);
                }
            }, FIRST_WAIT_LOCATION_TIME);
            Thread t = new Thread() {
                @Override
                public void run() {
                    if (!sampler.startScan()) {
                        reportScanningError(willCalculateLocation);
                    } else {
                        setServiceScanningState(willCalculateLocation);
                        //startLocationReportTimer();
                        startScanTimer.cancel();
                    }
                }
            };
            t.start();
        }

        private void setServiceScanningState(boolean willCalculateLocating) {
            if (willCalculateLocating) {
                setServiceState(LocatingServiceState.LOCATING);
            } else {
                setServiceState(LocatingServiceState.SCANNING);
            }
        }

        private void reportScanningError(boolean willCalculateLocation) {
            if (willCalculateLocation) {
                activityListener.onFailedToLocate();
            } else {
                activityListener.onFailedToStartScanning();
            }
        }

        /**
         * Stop BLE scan.
         * See willKeepScanning for its effect.
         *
         * @param willKeepScanning <em>true</em> if caller JUST wants to stop calculating location, but keeps on scanning.
         *                         Caller will still receive scanning result through the callback.
         *                         <em>false</em> if caller wants to stop BLE scanning completely.
         */
        public void stopScanning(boolean willKeepScanning) {
            if (willKeepScanning) {
                if (sampler != null) {
                    if (serviceState == LocatingServiceState.LOCATING) {
                        setServiceState(LocatingServiceState.SCANNING);
                        stopLocationReportTimer();
                    }
                    // never stop scan. We need to keep collecting user's location data.
                }
            } else {
                if (sampler != null) {
                    sampler.stopScan();
                }
                setServiceState(LocatingServiceState.NOT_SCANNING);
            }
        }

        public List<RssiRecord> getAllRssi() {
            return aggregatedRecords;
        }
    }
}
