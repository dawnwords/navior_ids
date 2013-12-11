/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2003 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author zzx
 * @date 13-7-15
 */
package com.navior.ids.android.view.mall3d.appModel;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.model.Model;
import com.navior.ids.android.view.mall3d.util.AABB;
import com.navior.ids.android.view.mall3d.util.ThirdPersonCamera;
import com.navior.ips.model.Floor;
import com.navior.ips.model.Mall;
import com.navior.ips.model.POP;
import com.navior.ips.model.Path;

import java.util.ArrayList;
import java.util.List;

public class MallModel extends Model {
  private boolean loaded;
  private Mall mall;
//  private float[] d = new float[5000000];

  private RouteModel route;

  public void setRoute(RouteModel route) {
    this.route = route;
  }

  //floors
  private ArrayList<FloorModel> floorModels = new ArrayList<FloorModel>();
  private ArrayList<Float> floorHeights = new ArrayList<Float>();

  //box
  private AABB aabb = new AABB();
  private float minX, maxX, minY, maxY;
  private float cx, cy;

  LocationModel locationModel = new LocationModel(10);

  public MallModel(Mall mall) {
    this.mall = mall;
    loaded = false;
  }

  public void load() {
    if(loaded) return;

    long t = System.currentTimeMillis();
    List<Floor> floors = mall.getL();
    float floorHeight = 0;

    for(Floor floor : floors) {
      FloorModel floorModel = new FloorModel(floor, floorHeight);
      floorHeight += ModelConstants.FLOOR_GAP;
      floorModels.add(floorModel);

      //update AABB
      AABB floorAABB = floorModel.getAABB();
      aabb.combine(floorAABB);
      cx = (aabb.getMaxX() + aabb.getMinX()) / 2;
      cy = (aabb.getMaxY() + aabb.getMinY()) / 2;
    }
    minX = aabb.getMinX();
    maxX = aabb.getMaxX();
    minY = aabb.getMinY();
    maxY = aabb.getMaxY();

    SparseIntArray floorId2Index = new SparseIntArray();
    for(int i = 0; i != floors.size(); i++) {
      floorId2Index.put(floors.get(i).getId(), i);
    }
    SparseArray<POP> popId2pop = new SparseArray<POP>();
    for(Floor floor : mall.getL()) {
      for(POP pop : floor.getPops()) {
        popId2pop.put(pop.getId(), pop);
      }
    }
    for(Path path : mall.getPs()) {
      path.setPoint1(popId2pop.get(path.getP1()));
      path.setPoint2(popId2pop.get(path.getP2()));
    }
    for(Path path : mall.getPs()) {
      POP a = path.getPoint1();
      POP b = path.getPoint2();
      if(a.getFloorId() != b.getFloorId()) {
        int f1 = floorId2Index.get(a.getFloorId());
        int f2 = floorId2Index.get(b.getFloorId());
        TunnelModel tunnel = new TunnelModel(a, b, f1, f2);

        if(f1 < f2)
          floorModels.get(f1).addTunnel(tunnel);
        else
          floorModels.get(f2).addTunnel(tunnel);
      }
    }


    ThirdPersonCamera camera = OpenglRenderer.getInstance().getCamera();
    camera.setTarget(cx, 0, cy);
    camera.setAlpha((float) (Math.PI / 2));
    camera.setBeta(0.5f);

    camera.setMinMaxTarget(new float[]{minX, maxX, -0.5f * ModelConstants.FLOOR_GAP, (mall.getL().size() - 0.5f) * ModelConstants.FLOOR_GAP, minY, maxY});

    float mx = maxX - minX, my = maxY - minY;
    float r = (float) Math.sqrt(mx * mx + my * my);
    float d = 1.2f * r;
    camera.setMinMaxDistance(0.1f * d, 3f * d);
    camera.setDistance(d);

    loaded = true;

    t = System.currentTimeMillis() - t;
    Log.i("[ips]Mall", "处理该商店消耗" + t + "ms");
    OpenglRenderer.getInstance().dismissDialog();
  }

  @Override
  public void pick() {
    if(!loaded) {
      load();
    }

    float mx = maxX - minX, my = maxY - minY;
    float r = (float) Math.sqrt(mx * mx + my * my);
    ThirdPersonCamera c = OpenglRenderer.getInstance().getCamera();
    float d = c.getDistance();
    float near = 10f, far = d + r;
    c.setZNear(near);
    c.setZFar(far);

    c.action();

    if(Parameter.getInstance().isView3D()) {
      for(FloorModel floorModel : floorModels) {
        floorModel.pick();
      }
    } else {
      floorModels.get(Parameter.getInstance().getCurrentFloorIndex()).pick();
    }
    OpenglRenderer.getInstance().allFlush();
  }

  float ry = 0;
  @Override
  public void draw(boolean selected) {
    if(!loaded) {
      load();
    }

    float mx = maxX - minX, my = maxY - minY;
    float r = (float) Math.sqrt(mx * mx + my * my);
    ThirdPersonCamera c = OpenglRenderer.getInstance().getCamera();
    float d = c.getDistance();
    float near = 10f, far = d + r;
    c.setZNear(near); // necessary for precision
    c.setZFar(far);

    c.action();


//    OpenglRenderer.currentAlpha = 1f;
//    Location location = new Location();
//    location.setX(300f);
//    location.setY(300f);
//    location.setFloorId(mall.getL().get(0).getId());
//    ry+=1f;
//    locationModel.setLocation(location, ry);
//    locationModel.draw();

    if(route!=null) {
      route.draw(selected);
    }


    if(Parameter.getInstance().isView3D()) {
      for(FloorModel floorModel : floorModels) {
        floorModel.draw(selected);
      }
    } else {
      floorModels.get(Parameter.getInstance().getCurrentFloorIndex()).draw(selected);
    }
    OpenglRenderer.getInstance().allFlush();
  }
}
