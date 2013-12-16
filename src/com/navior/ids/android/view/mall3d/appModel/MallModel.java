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

public class MallModel {
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

//  LocationModel locationModel = new LocationModel(10);

  public MallModel(Mall mall) {
    this.mall = mall;
    loaded = false;
  }

  public void load() {
    if(loaded) return;

    Model.pickDrawStart();

    long t = System.currentTimeMillis();
    List<Floor> floors = mall.getL();
    float floorHeight = 0;

    ModelConstants.FLOOR_GAP = ModelConstants.FLOOR_GAP_RADIO * (mall.getW() + mall.getH());

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
    SparseArray<POP> popId2pop = new SparseArray<POP>();
    for(int i = 0; i != floors.size(); i++) {
      Floor floor = floors.get(i);
      floorId2Index.put(floor.getId(), i);
      for(POP pop : floor.getPops()) {
        popId2pop.put(pop.getId(), pop);
      }
    }
    for(Path path : mall.getPs()) {
      POP a = popId2pop.get(path.getP1());
      POP b = popId2pop.get(path.getP2());
      if(a.getFloorId() != b.getFloorId()) {
        int f1 = floorId2Index.get(a.getFloorId());
        int f2 = floorId2Index.get(b.getFloorId());
        TunnelModel tunnel = new TunnelModel(a, b, f1, f2);

        if(f1 > f2)
          floorModels.get(f1).addTunnel(tunnel);
        else
          floorModels.get(f2).addTunnel(tunnel);
      }
    }

    ThirdPersonCamera camera = OpenglRenderer.getInstance().getCamera();
    camera.setTarget(cx, 0, cy);
    camera.setAlpha(OpenglRenderer.DEFAULT_3D_CAMERA_ALPHA);
    camera.setBeta(OpenglRenderer.DEFAULT_3D_CAMERA_BETA);

    camera.setMinMaxTarget(new float[]{minX, maxX, -0.5f * ModelConstants.FLOOR_GAP, (mall.getL().size() - 0.5f) * ModelConstants.FLOOR_GAP, minY, maxY});

    float mx = maxX - minX, my = maxY - minY;
    float r = (float) Math.sqrt(mx * mx + my * my);
    float d = 1.2f * r;
    camera.setMinMaxDistance(0.1f * d, 3f * d);
    camera.setDistance(d);

    loaded = true;

    t = System.currentTimeMillis() - t;
    System.out.println("Renderer 处理该商店消耗" + t + "ms");
    OpenglRenderer.getInstance().dismissDialog();
  }

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

    OpenglRenderer.getInstance().currentAlpha.set(1f);

    if(OpenglRenderer.getInstance().isView3D()) {
      for(FloorModel floorModel : floorModels) {
        floorModel.pick();
      }
    } else {
      floorModels.get(Parameter.getInstance().getCurrentFloorIndex()).pick();
    }
    OpenglRenderer.getInstance().allFlush();
  }

  public void draw() {
    if(!loaded) {
      load();
    }

    float mx = maxX - minX, my = maxY - minY;
    float r = (float) Math.sqrt(mx * mx + my * my);
    ThirdPersonCamera c = OpenglRenderer.getInstance().getCamera();
    float d = c.getDistance();
    float near = 10f, far = d*2 + r;
    c.setZNear(near); // necessary for precision
    c.setZFar(far);

    c.action();

    if(route!=null) {
      route.draw();
    }

    if(OpenglRenderer.getInstance().isView3D() && !OpenglRenderer.getInstance().isSwitching()) {
      for(FloorModel floorModel : floorModels) {
        floorModel.draw();
      }
    } else {
      floorModels.get(Parameter.getInstance().getCurrentFloorIndex()).draw();
    }
    OpenglRenderer.getInstance().allFlush();
  }
}
