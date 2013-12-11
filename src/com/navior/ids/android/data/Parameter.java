/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2010 NAVIOR CO.,LTD. All Rights Reserved.===========
 * =====================`==========END_COPYRIGHT================================
 *
 * @author Xinlu Wang
 * @date 2010-5-14
 */
package com.navior.ids.android.data;

import android.graphics.Point;
import android.os.Environment;

import com.amap.api.maps.model.LatLng;
import com.navior.ips.model.City;
import com.navior.ips.model.Floor;
import com.navior.ips.model.Location;
import com.navior.ips.model.Mall;
import com.navior.ips.model.POI;
import com.navior.ips.model.POP;
import com.navior.ips.model.Shop;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class Parameter {
  public static final String[] CREATE_TABLE = {
      "CREATE TABLE d_city (id int(10), nm nvarchar(45), abbr char(1), code varchar(45), lat double, lng double, mapsize double)",
      "CREATE TABLE d_mall (id int(10), cityId int(10), lat double, lng double, logo varchar(64), type int(10), nm nvarchar(64), w int(10), h int(10), addr nvarchar(256), focus_count int(10))",
      "CREATE TABLE d_malldetail (id int(10), sinatopic nvarchar(64), bus nvarchar(1024), phone nvarchar(256), open nvarchar(256), brief nvarchar(2048))",
      "CREATE TABLE d_floor (id int(10), mallId int(10), w int(10), h int(10), brief nvarchar(256), nm nvarchar(128))",
      "CREATE TABLE d_shop (id int(10), mallId int(10), floorId int(10), rno varchar(32), t int(10), cgid int(10), ico int(10), pos varchar(128), tel varchar(128), nm nvarchar(256), logo varchar(128), brief ntext, lt double, lr varchar(256), op text, shape text)",
      "CREATE TABLE d_shopdetail (id int(10), pos varchar(128), tel varchar(128), brief ntext)",
      "CREATE TABLE d_path (id int(10), mallId int(10), p1 int(10), p2 int(10), v int(10))",
      "CREATE TABLE d_poi (id int(10), floorId int(10), x float, y float, shopId int(10), pathId int(10))",
      "CREATE TABLE d_pop (id int(10), floorId int(10), x float, y float)",
      "CREATE TABLE d_pos (id int(10), floorId int(10), x float, y float, starSn int(20))"
  };
  public static final String[] CITY_DATA = {
      "(1,'北京','B','010',39.907961,116.397450,11.440000)",
      "(2,'长春','C','0431',43.891404,125.325932,1.390000)",
      "(3,'长沙','C','0731',28.194631,112.993303,0.680000)",
      "(4,'重庆','C','023',29.557262,106.577068,1.050000)",
      "(5,'成都','C','028',30.658050,104.065965,1.040000)",
      "(6,'大连','D','0411',38.918084,121.635834,0.940000)",
      "(7,'福州','F','0591',26.085569,119.297718,0.990000)",
      "(8,'广州','G','020',23.139618,113.320241,1.890000)",
      "(9,'贵阳','G','0851',26.578414,106.712495,0.640000)",
      "(10,'哈尔滨','H','0451',45.767559,126.620693,1.230000)",
      "(11,'杭州','H','0571',30.255525,120.163983,1.070000)",
      "(12,'合肥','H','0551',31.865967,117.283194,0.770000)",
      "(13,'济南','J','0531',36.665088,117.024782,1.370000)",
      "(14,'昆明','K','0871',25.037450,102.713593,0.410000)",
      "(15,'洛阳','L','0379',34.674878,112.449056,0.380000)",
      "(16,'南京','N','025',32.040401,118.785660,1.750000)",
      "(17,'青岛','Q','0532',36.083319,120.355243,0.770000)",
      "(18,'厦门','S','0592',24.453806,118.078646,0.350000)",
      "(19,'上海','S','021',31.192518,121.439998,6.140000)",
      "(20,'深圳','S','0755',22.544396,114.085365,2.020000)",
      "(21,'沈阳','S','024',41.783673,123.394508,2.000000)",
      "(22,'石家庄','S','0311',38.042688,114.511876,1.280000)",
      "(23,'苏州','S','0512',31.313544,120.624164,0.470000)",
      "(24,'天津','T','022',39.126432,117.201118,1.300000)",
      "(25,'武汉','W','027',30.580648,114.291798,1.340000)",
      "(26,'西安','X','029',34.259538,108.960535,1.500000)",
      "(27,'香港','X','0852',22.280613,114.180945,3.570000)",
      "(28,'徐州','X','0516',34.266509,117.186994,0.390000)",
      "(29,'郑州','Z','0371',34.724818,113.636390,0.580000)"
  };

  public static final String SERVER_ROOT_URL = "http://192.168.1.115:6602/ips/";
  public static final String NAVIOR = "www.navior.ips";
  public static final String EXTERNAL_STORAGE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + NAVIOR + File.separator;
  public static final String EXTERNAL_STORAGE_CAMERA_FOLDER = EXTERNAL_STORAGE_FOLDER + "c" + File.separator;
  public static final String EXTERNAL_STORAGE_IMAGE_FOLDER = EXTERNAL_STORAGE_FOLDER + "i" + File.separator;
  public static final String EXTERNAL_STORAGE_DATA_FOLDER = EXTERNAL_STORAGE_FOLDER + "d" + File.separator;
  public static final String EXTERNAL_STORAGE_VIDEO_FOLDER = EXTERNAL_STORAGE_FOLDER + "v" + File.separator;
  public static final String DB_PATH = EXTERNAL_STORAGE_DATA_FOLDER + "ids.data";
  public static final String APP_START_FIRST_TIME = "start_first_time";
  public static final String TOP_CITIES = "top_city_";
  public static final int MAX_TOP_CITY_NUM = 4;

  private static final Parameter instance = new Parameter();
  private LatLng outdoorPosition;
  private City currentCity;
  private Mall currentMall;
  private MyObservable<Location, Void> indoorPositionObservable;
  private MyObservable<Integer, Integer> floorIndexObservable;
  private MyObservable<Shop, Point> shopMyObservable;
  private MyObservable<Void, Void> requestRender;
  private MyObservable<POP, String> startObservable, destObservable;
  private MyObservable<Boolean, Void> changeViewObserver;

  static {
    createDir(EXTERNAL_STORAGE_FOLDER);
    createDir(EXTERNAL_STORAGE_CAMERA_FOLDER);
    createDir(EXTERNAL_STORAGE_DATA_FOLDER);
    createDir(EXTERNAL_STORAGE_IMAGE_FOLDER);
    createDir(EXTERNAL_STORAGE_VIDEO_FOLDER);
  }

  private static void createDir(String dirName) {
    File dir = new File(dirName);
    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  private static void deleteDir(String dirName) {
    File dir = new File(dirName);
    if (dir.exists()) {
      for (File file : dir.listFiles()) {
        file.delete();
      }
    }
  }

  public static Parameter getInstance() {
    return instance;
  }

  public static void clearData() {
    deleteDir(EXTERNAL_STORAGE_CAMERA_FOLDER);
    deleteDir(EXTERNAL_STORAGE_DATA_FOLDER);
    deleteDir(EXTERNAL_STORAGE_IMAGE_FOLDER);
    deleteDir(EXTERNAL_STORAGE_VIDEO_FOLDER);
  }

  private Parameter() {
    indoorPositionObservable = new MyObservable<Location, Void>();
    floorIndexObservable = new MyObservable<Integer, Integer>();
    shopMyObservable = new MyObservable<Shop, Point>();
    requestRender = new MyObservable<Void, Void>();
    startObservable = new MyObservable<POP, String>();
    destObservable = new MyObservable<POP, String>();
    changeViewObserver = new MyObservable<Boolean, Void>();
  }

  /**
   * CURRENT_CITY OPERATION
   */

  public City getCurrentCity() {
    return currentCity;
  }

  public void setCurrentCity(City currentCity) {
    this.currentCity = currentCity;
  }

  /**
   * CURRENT_MALL OPERATION
   */
  public Mall getCurrentMall() {
    return currentMall;
  }

  public void setCurrentMall(Mall currentMall) {
    this.currentMall = currentMall;
  }

  /**
   * CURRENT_FLOOR OPERATION
   */

  public int getCurrentFloorIndex() {
    return floorIndexObservable.obj;
  }

  public void setCurrentFloorIndex(int currentFloor) {
    floorIndexObservable.setObj(currentFloor, currentFloor);
  }

  public void addFloorIndexObserver(Observer floorObserver) {
    floorIndexObservable.addObserver(floorObserver);
  }

  public void deleteFloorIndexObserver(Observer floorObserver) {
    floorIndexObservable.deleteObserver(floorObserver);
  }

  public void clearFloorIndexObservers() {
    floorIndexObservable.deleteObservers();
  }

  /**
   * SELECT_SHOP OPERATION
   */
  public Shop getSelectedShop() {
    return shopMyObservable.obj;
  }

  public void setSelectedShop(Shop shop, Point clickPosition) {
    shopMyObservable.setObj(shop, clickPosition);
  }

  public void addShopObserver(Observer shopObserver) {
    shopMyObservable.addObserver(shopObserver);
  }

  public void deleteShopObserver(Observer shopObserver) {
    shopMyObservable.deleteObserver(shopObserver);
  }

  /**
   * MY_POSITION OPERATION
   */
  public LatLng getOutdoorPosition() {
    return outdoorPosition;
  }

  public void setOutdoorPosition(LatLng outdoorPosition) {
    this.outdoorPosition = outdoorPosition;
  }

  /**
   * MY_LOCATION OPERATION
   */
  public Location getMyLocation() {
    return indoorPositionObservable.obj;
  }

  public void setIndoorPosition(Location location) {
    indoorPositionObservable.setObj(location);
  }

  public void addIndoorPositionObserver(Observer observer) {
    indoorPositionObservable.addObserver(observer);
  }

  public void deleteIndoorPositionObserver(Observer observer) {
    indoorPositionObservable.deleteObserver(observer);
  }

  /**
   * REQUEST_RENDER OPERATION
   */
  public void requestRender() {
    requestRender.setObj(null);
  }

  public void addRequestRenderObserver(Observer observer) {
    requestRender.addObserver(observer);
  }

  public void deleteRequestRenderObserver(Observer observer) {
    requestRender.deleteObserver(observer);
  }

  public void clearRequestRenderObserver() {
    requestRender.deleteObservers();
  }

  /**
   * START OPERATION
   */
  public void setStart(Object obj) {
    if (obj instanceof Shop) {
      Shop shop = (Shop) obj;
      startObservable.setObj(getPOPByShop(shop), shop.getNm());
    } else if (obj instanceof Location) {
      startObservable.setObj(getPOPByLocation((Location) obj), "我的位置");
    }
  }

  public POP getStart() {
    return startObservable.obj;
  }

  public void addStartObserver(Observer observer) {
    startObservable.addObserver(observer);
  }

  public void deleteStartObserver(Observer observer) {
    startObservable.deleteObserver(observer);
  }

  /**
   * DEST OPERATION
   */
  public void setDest(Object obj) {
    if (obj instanceof Shop) {
      Shop shop = (Shop) obj;
      destObservable.setObj(getPOPByShop(shop), shop.getNm());
    } else if (obj instanceof Location) {
      destObservable.setObj(getPOPByLocation((Location) obj), "我的位置");
    }
  }

  public POP getDest() {
    return destObservable.obj;
  }

  public void addDestObserver(Observer observer) {
    destObservable.addObserver(observer);
  }

  public void deleteDestObserver(Observer observer) {
    destObservable.deleteObserver(observer);
  }

  public void setView3D(boolean view3D) {
    changeViewObserver.setObj(view3D);
  }

  public Boolean isView3D() {
    return changeViewObserver.obj;
  }

  public void addChangeViewObserver(Observer observer) {
    changeViewObserver.addObserver(observer);
  }

  public void deleteChangeViewObserver(Observer observer) {
    changeViewObserver.deleteObserver(observer);
  }

  public void clearChangeViewObserver() {
    changeViewObserver.deleteObservers();
  }


  private POP getPOPByLocation(Location location) {
    POP pop = new POP();
    pop.setX(location.getX());
    pop.setY(location.getY());
    pop.setFloorId(location.getFloorId());
    return pop;
  }

  private static POP getPOPByShop(Shop shop) {
    Mall mall = Parameter.getInstance().getCurrentMall();
    if (mall != null) {
      for (Floor floor : mall.getL()) {
        for (Shop s : floor.getG()) {
          if (shop.equals(s)) {
            for (POI poi : floor.getPois()) {
              if (poi.getShopId() == s.getId()) {
                POP pop = new POP();
                pop.setX(poi.getX());
                pop.setY(poi.getY());
                pop.setFloorId(poi.getFloorId());
                return pop;
              }
            }
          }
        }
      }
    }
    return null;
  }

  private class MyObservable<T, U> extends Observable {
    T obj;
    U args;

    void setObj(T obj) {
      setObj(obj, null);
    }

    void setObj(T obj, U args) {
      this.obj = obj;
      this.args = args;
      setChanged();
      notifyObservers(args);
    }
  }

}
