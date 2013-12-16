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
 * @date 13-9-16
 */
package com.navior.ids.android.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.navior.ids.android.R;
import com.navior.ids.android.service.locating.gps.GPSLocator;
import com.navior.ips.model.Mall;

public abstract class AbstractMapActivity extends FragmentActivity implements InfoWindowAdapter, OnMarkerClickListener,
    AMapLocationListener, LocationSource, OnMapClickListener, OnCameraChangeListener {

  private AMap aMap;
  private GPSLocator locator;
  private OnLocationChangedListener locationListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getContentView());
    locator = new GPSLocator() {
      @Override
      public void onLocationChanged(AMapLocation location) {
        if (locationListener != null) {
          AbstractMapActivity.this.onLocationChanged(location);
          locationListener.onLocationChanged(location);
        }
      }
    };
    initMap();
  }

  @Override
  protected void onPause() {
    deactivate();
    super.onPause();
  }

  /************************************
   *        USELESS INTERFACES        *
   ************************************/
  @Override
  public void onLocationChanged(Location location) {
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {
  }

  @Override
  public void onProviderEnabled(String s) {
  }

  @Override
  public void onProviderDisabled(String s) {
  }

  @Override
  public void onCameraChange(CameraPosition cameraPosition) {
  }
  /************************************
   *     END OF USELESS INTERFACES    *
   ************************************/

  @Override
  public void activate(OnLocationChangedListener listener) {
    locationListener = listener;
    locator.enableGPS(this);
  }

  @Override
  public void deactivate() {
    locationListener = null;
    locator.destroyGPS();
  }

  @Override
  public View getInfoContents(Marker marker) {
    return null;
  }

  @Override
  public void onCameraChangeFinish(CameraPosition cameraPosition) {
    onCameraChange(cameraPosition, aMap.getProjection().getVisibleRegion().latLngBounds);
  }

  @Override
  public void onLocationChanged(final AMapLocation location) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    String cityCode = location.getCityCode();
    onLocationChanged(latLng, cityCode);
  }

  protected LatLngBounds setDefaultCenter(LatLng position) {
    return setDefaultCenter(position, 15);
  }

  protected LatLngBounds setDefaultCenter(LatLng position, double level) {
    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
        .target(position).zoom(level > 15 ? (int) level : 15).build()));
    return aMap.getProjection().getVisibleRegion().latLngBounds;
  }

  protected Marker addMallMarker(Mall mall, BitmapDescriptor markerIcon) {
    LatLng latLng = new LatLng(mall.getLat(), mall.getLng());
    return aMap.addMarker(new MarkerOptions().position(latLng)
        .title(mall.getNm()).snippet(mall.getAddr())
        .icon(markerIcon));
  }

  private void initMap() {
    if (aMap == null) {
      aMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    }

    final BitmapDescriptor outdoorLocation = BitmapDescriptorFactory.fromResource(R.drawable.outdoor_location);
    MyLocationStyle myLocationStyle = new MyLocationStyle().myLocationIcon(outdoorLocation)
        .radiusFillColor(color(R.color.myposition_fill)).strokeWidth(1)
        .strokeColor(color(R.color.myposition_stroke));

    aMap.setMyLocationStyle(myLocationStyle);
    aMap.setOnMapClickListener(this);
    aMap.setOnMarkerClickListener(this);
    aMap.setInfoWindowAdapter(this);
    aMap.setLocationSource(this);
    aMap.setOnCameraChangeListener(this);
    aMap.setMyLocationEnabled(true);

    UiSettings uiSettings = aMap.getUiSettings();
    uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
    uiSettings.setZoomControlsEnabled(false);
    uiSettings.setMyLocationButtonEnabled(false);
  }

  private int color(int colorId) {
    return getResources().getColor(colorId);
  }

  protected abstract void onLocationChanged(LatLng latLng, String cityCode);

  protected abstract void onCameraChange(CameraPosition cameraPosition, LatLngBounds bounds);

  protected abstract int getContentView();

}
