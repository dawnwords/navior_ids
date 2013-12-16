package com.navior.ids.android.service.locating.ids.component;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.navior.ids.android.service.locating.ids.component.sampler.Sampler;
import com.navior.ids.android.service.locating.ids.component.sampler.SamsungSampler;
import com.navior.ids.android.service.locating.ids.data.RssiRecord;
import com.navior.ips.model.Location;
import com.navior.ips.model.POS;

import java.util.Collection;
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
    private final static long MAX_TIME_FOR_BT_TASK = 5000l;
    private final static long FIRST_WAIT_LOCATION_TIME = 4000l;
    private final static long LOCATION_REPORT_PERIOD = 4000l;
    // fields initialized from caller parameters
    private LocatingListener activityListener;
    // fields initialized by constructor
    private ServiceStateHandler stateHandler;
    private Calculator calculator;
    private LocationFilter locationFilter;
    private Timer locationReportTimer;
    private Timer calculatingTimer;
    // fields initialized here
    private Sampler.SamplerListener listener = new Sampler.SamplerListener() {

        @Override
        public void onStateReady() {
            stateHandler.setServiceState(LocatingServiceState.READY);
        }

        /**
         * Called when bt service connection lost.
         * It'll NOT be called if error in initializing ble.
         */
        @Override
        public void onStateNotReady() {
            stateHandler.setServiceState(LocatingServiceState.NOT_READY);
        }

        @Override
        public void onStartScanningError() {
            stateHandler.setServiceState(LocatingServiceState.NOT_READY);
            activityListener.onFailedToStartScanning();
        }

        @Override
        public void onActiveBtServiceError() {
            stateHandler.setServiceState(LocatingServiceState.NOT_READY);
            activityListener.onErrorInitializeBluetooth();
        }

        @Override
        public void onNewRecord() {
            LinkedList<RssiRecord> records = sampler.getRecords();
            // todo save records
            log(records);
            if (!stateHandler.isEarlier(LocatingServiceState.LOCATING)) {
                computeLocation(records);
                // if got new rssi after the first calculation, continue to calculate
                while (!stateHandler.isEarlier(LocatingServiceState.LOCATING)
                        && sampler.hasNewRecord()) {
                    computeLocation(sampler.getRecords());
                }
            }
        }

        private void computeLocation(LinkedList<RssiRecord> records) {

            List<RssiRecord> aggregatedRecords = calculator.aggregate(records, null);

            // ask the activity for pos records
            RssiRecord recordExample = aggregatedRecords.get(0);

            HashMap<String, POS> posInfoMap = activityListener.getPosMap(recordExample.getStarName());
            if(posInfoMap == null ) {
                calculatingTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // on timeout for requesting for pos
                        stateHandler.setServiceState(LocatingServiceState.SCANNING);
                        activityListener.onFailedToLocate();
                    }
                }, MAX_TIME_FOR_POS);
                return;
            }
            // getPosMap() could return null
            else {
                resetCalculatingTimer();
                Location location = calculator.calculate(records, posInfoMap);
                if (location != null) {
                    locationFilter.input(location);
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
                        stateHandler.setServiceState(LocatingServiceState.NOT_READY);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (sampler != null) {
                            sampler.reinitialize();
                        } else {
                            initializeSampler();
                        }
                        break;
                    case BluetoothAdapter.ERROR:
                        break;
                }
            }
        }
    };
    // initialize when first use
    private Sampler sampler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stateHandler = new ServiceStateHandler();
        calculator = new ThreePointCalculator();
        locationFilter = new ExponentialLocationFilter();
        locationReportTimer = new Timer();
        calculatingTimer = new Timer();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

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
        stateHandler.setServiceState(LocatingServiceState.INITIALIZING);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            stateHandler.setServiceState(LocatingServiceState.NOT_READY);
            activityListener.onBluetoothUnavailable();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                stateHandler.setServiceState(LocatingServiceState.NOT_READY);
                activityListener.onBluetoothOff();
            } else {
                // process of start Samsung bt service may be blocked. so start a new thread to do it
                new Thread() {
                    @Override
                    public void run() {
                        sampler = new SamsungSampler(LocatingService.this, listener);
                    }
                }.start();
            }
        }
    }

    private void startLocationReportTimer() {
        resetLocationReportTimer();
        locationReportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Location result = locationFilter.getOutput();
                if (result != null) {
                    activityListener.onNewLocation(result);
                }
            }
        }, FIRST_WAIT_LOCATION_TIME, LOCATION_REPORT_PERIOD);
    }

    private void resetLocationReportTimer() {
        locationReportTimer.cancel();
        locationReportTimer = new Timer();
    }

    private void resetCalculatingTimer() {
        calculatingTimer.cancel();
        calculatingTimer = new Timer();
    }

    private void log(Collection<RssiRecord> records) {
        // todo save records into db
    }

    private enum LocatingServiceState implements Comparable<LocatingServiceState> {

        NOT_READY,
        INITIALIZING,
        READY,
        SCANNING,
        LOCATING;
        private Timer timer;
        private StateChangeListener listener;

        LocatingServiceState() {
            this.timer = new Timer();
            listener = null;
        }
    }

    private interface StateChangeListener {
        void onReachState();

        void onTimeOut();
    }

    public class LocatingBinder extends Binder {
        public void setListener(LocatingListener listener) {
            activityListener = listener;
            initializeSampler();
        }

        public void startScanning() {
            if (stateHandler.isEarlier(LocatingServiceState.READY)) {
                stateHandler.setStateListener(
                        LocatingServiceState.READY,
                        new StateChangeListener() {
                            @Override
                            public void onReachState() {
                                sampler.startScan();
                                stateHandler.setServiceState(LocatingServiceState.SCANNING);
                            }

                            @Override
                            public void onTimeOut() {
                                activityListener.onFailedToStartScanning();
                            }
                        });
            } else {
                sampler.startScan();
                stateHandler.setServiceState(LocatingServiceState.SCANNING);
            }
        }

        public void stopScanning() {
            if (!stateHandler.isEarlier(LocatingServiceState.SCANNING)) {
                sampler.stopScan();
                stateHandler.setServiceState(LocatingServiceState.READY);
            }
        }

        public void startLocating() {
            if (stateHandler.isEarlier(LocatingServiceState.READY)) {
                stateHandler.setStateListener(
                        LocatingServiceState.READY,
                        new StateChangeListener() {
                            @Override
                            public void onReachState() {
                                startLocationReportTimer();
                                sampler.startScan();
                                stateHandler.setServiceState(LocatingServiceState.LOCATING);
                            }

                            @Override
                            public void onTimeOut() {
                                activityListener.onFailedToLocate();
                            }
                        });
            } else {
                sampler.startScan();
                startLocationReportTimer();
                stateHandler.setServiceState(LocatingServiceState.LOCATING);
            }
        }

        public void stopLocating() {
            if (!stateHandler.isEarlier(LocatingServiceState.SCANNING) && sampler != null) {
                sampler.stopScan();
            }

            if (!stateHandler.isEarlier(LocatingServiceState.READY)) {
                stateHandler.setServiceState(LocatingServiceState.READY);
            }
        }
    }

    private class ServiceStateHandler {

        private LocatingServiceState state;

        private ServiceStateHandler() {
            this.state = LocatingServiceState.NOT_READY;
        }

        private void setServiceState(LocatingServiceState state) {
            this.state = state;
            state.timer.cancel();
            state.timer = new Timer();
            if (state.listener != null) {
                state.listener.onReachState();
                state.listener = null;
            }

            if (isEarlier(LocatingServiceState.LOCATING)) {
                resetLocationReportTimer();
                resetCalculatingTimer();
            }
        }

        private void setStateListener(final LocatingServiceState state, final StateChangeListener listener) {
            state.listener = listener;
            state.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    listener.onTimeOut();
                    state.listener = null;
                }
            }, MAX_TIME_FOR_BT_TASK);

        }

        public boolean isEarlier(LocatingServiceState state) {
            return this.state.ordinal() < state.ordinal();
        }
    }


}
