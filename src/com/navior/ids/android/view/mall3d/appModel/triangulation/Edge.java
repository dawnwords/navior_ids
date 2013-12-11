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

public class Edge {
  public Vertex prevVertex, nextVertex;
  public Edge prevEdge, nextEdge;

  public Edge(Vertex prevVertex, Vertex nextVertex) {
    this.prevVertex = prevVertex;
    this.nextVertex = nextVertex;
  }

  public void findEdges() {
    prevEdge = prevVertex.prevEdge;
    nextEdge = nextVertex.nextEdge;
  }

  public static boolean intersect(Edge e1, Edge e2) {
    Vertex v1 = e1.prevVertex, v2 = e1.nextVertex;
    Vertex v3 = e2.prevVertex, v4 = e2.nextVertex;

    return (cross(v1, v2, v3) * cross(v1, v2, v4) < 0) && (cross(v3, v4, v1) * cross(v3, v4, v2) < 0);
  }

  public static float dot(Vertex v1, Vertex v2, Vertex v3) { // (v2-v1)·(v3-v2)
    float dx1 = v2.x - v1.x, dy1 = v2.y - v1.y;
    float dx2 = v3.x - v2.x, dy2 = v3.y - v2.y;
    float result = dx1 * dx2 + dy1 * dy2;
    if(result < 0.001f && result > -0.001f) result = 0;
    return result;
  }

  public static float cross(Vertex v1, Vertex v2, Vertex v3) { // (v2-v1)×(v3-v1)
    float dx1 = v2.x - v1.x, dy1 = v2.y - v1.y;
    float dx2 = v3.x - v1.x, dy2 = v3.y - v1.y;
    float result = dx1 * dy2 - dx2 * dy1;
    if(result < 0.001f && result > -0.001f) result = 0;
    return result;
  }

  public static float cross(Vertex v1, Vertex v2, Vertex v3, Vertex v4) { // (v2-v1)×(v4-v3)
    float dx1 = v2.x - v1.x, dy1 = v2.y - v1.y;
    float dx2 = v4.x - v3.x, dy2 = v4.y - v3.y;
    float result = dx1 * dy2 - dx2 * dy1;
    if(result < 0.001f && result > -0.001f) result = 0;
    return result;
  }

  public static float angle(Vertex v1, Vertex v2, Vertex v3) { // 3边 b:v1~v2, c:v2~v3, a:v1~v3
    boolean cw = cross(v1, v2, v2, v3) > 0; //判定角在左边还是右边
    float dx1 = v2.x - v1.x, dy1 = v2.y - v1.y;
    float dx2 = v3.x - v2.x, dy2 = v3.y - v2.y;
    float dx3 = dx1 - dx2, dy3 = dy1 - dy2;
    float dl1 = dx1 * dx1 + dy1 * dy1;
    float dl2 = dx2 * dx2 + dy2 * dy2;
    float dl3 = dx3 * dx3 + dy3 * dy3;

    double dl1r = Math.sqrt(dl1);
    if(dl1r == 0) return 0; //若原数据有重复的点可能会导致角度出错
    double dl2r = Math.sqrt(dl2);
    if(dl2r == 0) return 0;

    double cosAlpha = (dl1 + dl2 - dl3) / dl1r / dl2r / 2; //余弦定理

    if(cosAlpha > 1) cosAlpha = 1;
    if(cosAlpha < -1) cosAlpha = -1;

    float alpha = (float) Math.acos(cosAlpha);

    if(cw) {
      return -alpha;
    } else {
      return alpha;
    }
    //输出结果[-pi,pi] 在第一行叉积等于0(三点共线)时会取可能会出错 特别是三点在空间中以v1 v3 v2的顺序共线的时候

  }
}
