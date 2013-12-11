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
 * @date 2013年7月11日
 */

package com.navior.ids.android.view.mall3d.appModel.triangulation;

public class Vertex {
  public int id;
  public float x, y;
  public boolean up;
  public Edge prevEdge, nextEdge;

  public Vertex(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public String toString() {
    return "" + this.id;
  }

  public Vertex nextVertex() {
    return nextEdge.nextVertex;
  }

  public Vertex prevVertex() {
    return prevEdge.prevVertex;
  }

}
