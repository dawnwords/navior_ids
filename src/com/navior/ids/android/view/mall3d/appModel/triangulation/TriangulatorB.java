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

public class TriangulatorB extends Triangulator {

  @Override
  public boolean triangulate() {
    triangles = new ArrayList<Triangle>();

    int correctTriangleNumber = vertices.size() - 2;

    // O(n^3) worst, n^2 normal cases, potential O(n^2)
    for(int i = 0; i != correctTriangleNumber; i++) { //n-2 triangles
      boolean change = false;
      for(Vertex v : vertices) {

        Vertex p = v.prevVertex(), n = v.nextVertex(); //try triangle(p,v,n)
        Edge pe = v.prevEdge, ne = v.nextEdge;

        boolean straight = false;
        float c = Edge.cross(p, v, v, n);
        if(c > 0) { // clockwise
          continue;
        } else if(c == 0) {
          straight = true; //如果共线就直接生成三角形，v1 v3 v2顺序的共线可能会出问题
        }

        boolean safe = true;
        if(!straight) {
          for(Vertex vertex : vertices) { //
            if(vertex != p && vertex != v && vertex != n) { //vertex is on/in triangle(p,v,n)
              if((Edge.cross(vertex, v, p) * Edge.cross(vertex, v, n) <= 0) &&
                 (Edge.cross(vertex, p, v) * Edge.cross(vertex, p, n) <= 0) &&
                 (Edge.cross(vertex, n, p) * Edge.cross(vertex, n, v) <= 0)) {
                safe = false;
                break;
              }
            }
          }

          if(!safe) { //这个顶点不行
            continue;
          }
        }

        change = true;

        triangles.add(new Triangle(p, v, n));

        vertices.remove(v);
        edges.remove(pe);
        edges.remove(ne);

        Edge nEdge = new Edge(p, n);
        edges.add(nEdge);

        nEdge.prevVertex = p;
        nEdge.nextVertex = n;
        nEdge.prevEdge = p.prevEdge;
        nEdge.nextEdge = n.nextEdge;

        pe.prevEdge.nextEdge = nEdge;
        ne.nextEdge.prevEdge = nEdge;
        p.nextEdge = nEdge;
        n.prevEdge = nEdge;

        break; //found one triangle
      }
      if(!change) //no more triangles exist => fail
        break;
    }

    if(vertices.size() == 3) {
      triangles.add(new Triangle(vertices.get(0), vertices.get(1), vertices.get(2)));
    }

    return (triangles.size() == correctTriangleNumber);
  }
}
