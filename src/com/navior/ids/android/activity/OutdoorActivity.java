package com.navior.ids.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.navior.ids.android.R;
import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.data.actiondao.CityDAO;
import com.navior.ids.android.data.actiondao.MallDAO;
import com.navior.ids.android.view.button.CitySelectButton;
import com.navior.ids.android.view.popup.BottomPopup;
import com.navior.ids.android.view.popup.MallDetailPopup;
import com.navior.ips.model.City;
import com.navior.ips.model.Mall;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
public class OutdoorActivity extends AbstractMapActivity {
  private CitySelectButton citySelectBtn;
  private LinkedList<Marker> showingMarkers;
  private Marker currentSelectedMarker;
  private BottomPopup mallDetailPopup;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    citySelectBtn = (CitySelectButton) findViewById(R.id.btn_select_city);
    showingMarkers = new LinkedList<Marker>();
    mallDetailPopup = new MallDetailPopup(this);
    citySelectBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectCity();
      }
    });

    findViewById(R.id.tab_more).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(OutdoorActivity.this, MoreActivity.class));
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mallDetailPopup != null) {
      mallDetailPopup.dismiss();
      mallDetailPopup = null;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CityActivity.REQUEST_CODE && resultCode == RESULT_OK) {
      City city = (City) data.getExtras().get(CityActivity.SELECT_CITY);
      LatLngBounds bounds = setDefaultCenter(new LatLng(city.getLat(), city.getLng()), city.getMapsize());
      getMallByCity(city, bounds);
    }
  }

  @Override
  protected void onCameraChange(CameraPosition cameraPosition, LatLngBounds bounds) {
    Iterator<Marker> markerIt = showingMarkers.iterator();
    while (markerIt.hasNext()) {
      Marker marker = markerIt.next();
      if (!bounds.contains(marker.getPosition())) {
        marker.remove();
        markerIt.remove();
      }
    }

    City currentCity = Parameter.getInstance().getCurrentCity();
    if (currentCity != null) {
      List<Mall> malls = currentCity.getMalls();
      if (malls != null) {
        addShowingMallMark(bounds, malls);
      }
    }
  }

  @Override
  protected int getContentView() {
    return R.layout.activity_outdoor;
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    clearSelectedMarker();
    currentSelectedMarker = marker;
    currentSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark_on));
    mallDetailPopup.show(marker.getObject());
    return true;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return null;
  }

  @Override
  public void onMapClick(LatLng latLng) {
    clearSelectedMarker();
  }

  @Override
  public void onLocationChanged(LatLng outdoorPosition, final String cityCode) {
    final LatLngBounds bounds = setDefaultCenter(outdoorPosition);
    Parameter.getInstance().setOutdoorPosition(outdoorPosition);

    new CityDAO(getApplicationContext()) {
      @Override
      protected void doSuccess(Vector<City> result) {
        for (City city : result) {
          if (city.getCode().equals(cityCode)) {
            getMallByCity(city, bounds);
            deactivate();
            return;
          }
        }
        toast(R.string.no_such_city);
      }

      @Override
      protected void doException() {
        toast(R.string.net_error);
      }
    }.execute();
  }

  private void clearSelectedMarker() {
    if (currentSelectedMarker != null) {
      currentSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark));
      currentSelectedMarker = null;
    }
    if (mallDetailPopup != null) {
      mallDetailPopup.dismiss();
    }
  }

  private void getMallByCity(final City city, final LatLngBounds bounds) {
    Parameter.getInstance().setCurrentCity(city);
    citySelectBtn.setText(city.getName());

    for (Marker marker : showingMarkers) {
      marker.remove();
    }
    showingMarkers.clear();

    new MallDAO(getApplicationContext()) {

      @Override
      protected String[] getArgs() {
        return new String[]{city.getId() + ""};
      }

      @Override
      protected void doSuccess(Vector<Mall> result) {
        city.setMalls(result);
        addShowingMallMark(bounds, result);
        refreshMap();
      }

      @Override
      protected void doException() {
      }
    }.execute();
  }

  private void addShowingMallMark(LatLngBounds bounds, List<Mall> malls) {
    final BitmapDescriptor mapMarkerOn = BitmapDescriptorFactory.fromResource(R.drawable.map_mark_on);
    final BitmapDescriptor mapMarkerOff = BitmapDescriptorFactory.fromResource(R.drawable.map_mark);
    outer:
    for (Mall mall : malls) {
      LatLng latLng = new LatLng(mall.getLat(), mall.getLng());
      if (bounds.contains(latLng)) {
        for (Marker marker : showingMarkers) {
          if (mall == marker.getObject()) {
            continue outer;
          }
        }
        Marker marker = addMallMarker(mall,
            (currentSelectedMarker != null && currentSelectedMarker.getObject() == mall) ? mapMarkerOn : mapMarkerOff);
        marker.setObject(mall);
        showingMarkers.add(marker);
      }
    }
  }

  private void toast(int msgId) {
    Toast.makeText(getApplicationContext(), msgId, Toast.LENGTH_SHORT).show();
  }

  private void selectCity() {
    startActivityForResult(new Intent(getApplicationContext(), CityActivity.class), CityActivity.REQUEST_CODE);
  }
}