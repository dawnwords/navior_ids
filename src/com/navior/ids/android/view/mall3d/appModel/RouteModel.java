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
 * @date 13-7-19
 */
package com.navior.ids.android.view.mall3d.appModel;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.model.ArrayLine;
import com.navior.ids.android.view.mall3d.model.ModelLine;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;
import com.navior.ips.model.Floor;
import com.navior.ips.model.Mall;
import com.navior.ips.model.POP;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RouteModel {

  ModelLine line;

  public RouteModel() {
    this.setPoints(new float[]{
        100, 50, 200,
        150, 50, 200,
        150, 200, 200,
        200, 200, 300,
        200, 350, 250,
        250, 350, 300
    });
  }

  public RouteModel(float[] p) {
    this.setPoints(p);
  }

  public RouteModel(LinkedList<POP> v, Mall mall) {
    HashMap<Integer, Integer> floorIdIndex = new HashMap<Integer, Integer>();

    if(mall.getL() == null) return;
    List<Floor> floors = mall.getL();
    for(int i = 0; i != floors.size(); i++) {
      floorIdIndex.put(floors.get(i).getId(), i);
    }

    float[] coord = new float[v.size() * 3];
    int i = 0;
    for(POP p : v) {
      coord[i++] = p.getX();
      coord[i++] = floorIdIndex.get(p.getFloorId()) * ModelConstants.FLOOR_GAP + ModelConstants.ROUTE_HEIGHT;
      coord[i++] = p.getY();
    }
    setPoints(coord);
  }

  public void setPoints(float[] p) {
    ArrayLine arrayLine = new ArrayLine(p, new float[]{1, 0, 0, 1}, 10);
    line = new ModelLine();
    line.setPrimitiveType(GLES20.GL_LINE_STRIP);
    line.finish(arrayLine);
  }

  public void draw() {
    line.draw(Pass.PASS_DRAW);
  }

  public void pick() {

  }
}
