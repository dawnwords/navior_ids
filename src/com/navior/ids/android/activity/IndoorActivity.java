/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2010 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author cs1
 * @date 13-10-30
 */
package com.navior.ids.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.navior.ids.android.R;
import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.data.actiondao.FloorBySnDAO;
import com.navior.ids.android.data.actiondao.FloorDAO;
import com.navior.ids.android.idslocating.component.LocatingListener;
import com.navior.ids.android.idslocating.component.LocatingService;
import com.navior.ids.android.idslocating.data.RssiRecord;
import com.navior.ids.android.view.list.FloorSelector;
import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.OpenglView;
import com.navior.ids.android.view.popup.BottomPopup;
import com.navior.ids.android.view.popup.LoadingDialog;
import com.navior.ids.android.view.popup.ShopDetailPopup;
import com.navior.ips.model.Floor;
import com.navior.ips.model.Location;
import com.navior.ips.model.Mall;
import com.navior.ips.model.POS;
import com.navior.ips.model.Shop;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class IndoorActivity extends Activity {
  private FloorSelector selector;
  private Button dimenSwitcher;
  private OpenglView openglView;
  private LocatingService.LocatingBinder locator;
  private HashMap<String, POS> starMap;
  private boolean isNavigating;
  private Observer shopSelectObserver;
  private BottomPopup shopPopup;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final Mall currentMall = Parameter.getInstance().getCurrentMall();
    if (currentMall == null) {
      finish();
      return;
    }
    initUI();
    fetchFloorData(currentMall);
    bindLocatingService();
  }

  @Override
  public void onPause() {
    if (openglView != null) {
      openglView.onPause();
    }
    if (locator != null) {
        // will not keep scanning
      locator.stopScanning(false);
    }
    Parameter.getInstance().deleteShopObserver(shopSelectObserver);
    super.onPause();
  }

  @Override
  public void onResume() {
    if (openglView != null) {
      openglView.onResume();
    }
    if (shopSelectObserver == null) {
      shopSelectObserver = new Observer() {
        @Override
        public void update(Observable observable, Object data) {
          if (shopPopup != null) {
            Shop shop = Parameter.getInstance().getSelectedShop();
            if (shop == null) {
              shopPopup.dismiss();
            } else {
              shopPopup.show(shop);
            }
          }
        }
      };
    }
    Parameter.getInstance().addShopObserver(shopSelectObserver);
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    if (shopPopup != null) {
      shopPopup.dismiss();
      shopPopup = null;
    }
    Parameter.getInstance().setCurrentMall(null);
    Parameter.getInstance().setSelectedShop(null, null);
    super.onDestroy();
  }

  private void initUI() {
    setContentView(R.layout.activity_indoor);
    selector = (FloorSelector) findViewById(R.id.indoor_floor_selector);
    dimenSwitcher = (Button) findViewById(R.id.dimen_switcher);
    shopPopup = new ShopDetailPopup(IndoorActivity.this);

    dimenSwitcher.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (OpenglRenderer.getInstance().switchView()) {
          dimenSwitcher.setBackgroundResource(OpenglRenderer.getInstance().isView3D() ? R.drawable.dimen_two : R.drawable.dimen_three);
        }
      }
    });
    findViewById(R.id.indoor_return).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    findViewById(R.id.tab_me).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        myLocation();
      }
    });
    dimenSwitcher.setBackgroundResource(OpenglRenderer.getInstance().isView3D() ? R.drawable.dimen_three : R.drawable.dimen_two);
  }

  private void bindLocatingService() {
    bindService(new Intent(this, LocatingService.class), new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        locator = (LocatingService.LocatingBinder) service;
        locator.setListener(new LocatingListener() {
          @Override
          public HashMap<String, POS> getPosMap(final String starName) {
            fetchFloorData(starName);
            return starMap;
          }

            @Override
            public void onNewRssi(BluetoothDevice device, int rssi, byte[] bytes) {

            }

            @Override
          public void onNewLocation(Location location, List<RssiRecord> rssiRecords) {
            Parameter.getInstance().setIndoorPosition(location);
            if (!isNavigating) {
                // completely stop scanning
              locator.stopScanning(false);
            }
          }

          @Override
          public void onFailedToStartScanning() {
          }

          @Override
          public void onFailedToLocate() {
            toast(R.string.fail_to_locate_indoors);
          }

          @Override
          public void onBluetoothOff() {

            toast(R.string.bluetooth_unavailable);
            locator.stopScanning(false);
          }

          @Override
          public void onBluetoothUnavailable() {
          }

          @Override
          public void onErrorInitializeBluetooth() {
            toast(R.string.bluetooth_unavailable);
            locator.stopScanning(false);
          }
        });
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
      }
    }, BIND_AUTO_CREATE);
  }

  private void myLocation() {
    if (locator != null) {
      toast(R.string.locating);
      locator.startScanning(true);
    }
  }

  private void fetchFloorData(final String starName) {
    new FloorBySnDAO(getApplicationContext()) {
      @Override
      protected String[] getArgs() {
        return new String[]{starName};
      }

      @Override
      protected void doSuccess(Vector<Floor> result) {
        starMap = new HashMap<String, POS>();
        for (Floor floor : result) {
          for (POS pos : floor.getPoss()) {
            starMap.put("" + pos.getStarSn(), pos);
          }
        }
      }

      @Override
      protected void doException() {
        toast(R.string.net_error);
      }
    }.execute();
  }

  private void fetchFloorData(final Mall currentMall) {
    final ProgressDialog dialog = LoadingDialog.show(this, getString(R.string.loading_floor), new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        finish();
      }
    });
    new FloorDAO(getApplicationContext()) {
      @Override
      protected String[] getArgs() {
        return new String[]{currentMall.getId() + ""};
      }

      @Override
      protected void doSuccess(Vector<Floor> result) {
        currentMall.setL(result);

        selector.setFloorList(result);
        openglView = ((OpenglView) findViewById(R.id.indoor_mall_view));
        openglView.start(selector, dialog);
      }

      @Override
      protected void doException() {
        toast(R.string.net_error);
      }
    }.execute();
  }

  private void toast(int strId) {
    Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
  }

}