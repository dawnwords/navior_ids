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
 * @date 13-7-16
 */
package com.navior.ids.android.view.mall3d.appModel.triangulation;

import java.util.ArrayList;

public abstract class Triangulator {
  // ------------------Input------------------
  protected ArrayList<Vertex> vertices = new ArrayList<Vertex>();
  // ------------------Output------------------
  protected ArrayList<Triangle> triangles;
  private ArrayList<Vertex> oldVertices = vertices;
  protected ArrayList<Edge> edges = new ArrayList<Edge>();

  protected float[] angles;
  protected float clockwise; // >0 clockwise, <0 counter-clockwise

  public ArrayList<Vertex> getVertices() {
    return oldVertices;
  }

  public Vertex newVertex(float x, float y) {
    Vertex v = new Vertex(x, y);
    v.id = vertices.size();
    vertices.add(v);
    return v;
  }

  public Edge newEdge(int id1, int id2) {
    Vertex v1 = vertices.get(id1);
    Vertex v2 = vertices.get(id2);

    Edge e = new Edge(v1, v2);
    edges.add(e);
    v1.nextEdge = e;
    v2.prevEdge = e;
    return e;
  }

  public Edge newEdge(Vertex v1, Vertex v2) {
    Edge e = new Edge(v1, v2);
    edges.add(e);
    v1.nextEdge = e;
    v2.prevEdge = e;
    return e;
  }

  public Edge newEdge(Vertex v1, Vertex v2, boolean reverse) {
    Edge e = new Edge(v1, v2);
    edges.add(e);
    if(!reverse) {
      v1.nextEdge = e;
      v2.prevEdge = e;
    } else {
      v2.nextEdge = e;
      v1.prevEdge = e;
    }
    return e;
  }

  public boolean confirmVertexEdge() {
    oldVertices = new ArrayList<Vertex>();
    for(Vertex v : vertices)
      oldVertices.add(v);

    for(Edge edge : edges) {
      edge.findEdges();
    }

    //check clockwise or not. O(n)
    float ccw = 0;
    angles = new float[vertices.size()];
    for(int i = 0; i != vertices.size(); i++) { // winding order assumption
      Vertex current = vertices.get(i);
      float c = Edge.angle(current.prevVertex(), current, current.nextVertex());
      ccw += c;
      angles[i] = c;
    }
    boolean clockwise = ccw < 0; //usually ccw == +-2pi.

    setClockwise(clockwise); //如果不是顺时针就把它转换成顺时针

    return clockwise;
  }

  private void setClockwise(boolean clockwise) {
    if(clockwise) {
      for(Vertex v : vertices) {
        Edge t = v.prevEdge;
        v.prevEdge = v.nextEdge;
        v.nextEdge = t;
      }
      for(Edge e : edges) {
        Edge t = e.prevEdge;
        e.prevEdge = e.nextEdge;
        e.nextEdge = t;
        Vertex s = e.prevVertex;
        e.prevVertex = e.nextVertex;
        e.nextVertex = s;
      }
    }
  }

  public ArrayList<Triangle> getTriangles() {
    return triangles;
  }

  public float[] getAngles() {
    return angles;
  }

  public abstract boolean triangulate();
}
