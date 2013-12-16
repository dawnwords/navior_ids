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

package com.navior.ids.android.view.mall3d.appModel;

import com.navior.ids.android.view.mall3d.appModel.triangulation.Triangle;
import com.navior.ids.android.view.mall3d.appModel.triangulation.Triangulator;
import com.navior.ids.android.view.mall3d.appModel.triangulation.TriangulatorB;
import com.navior.ids.android.view.mall3d.appModel.triangulation.Vertex;
import com.navior.ids.android.view.mall3d.model.ArrayColor;
import com.navior.ids.android.view.mall3d.model.ArrayColorIndexed;
import com.navior.ids.android.view.mall3d.model.ArrayLine;
import com.navior.ids.android.view.mall3d.model.Model;
import com.navior.ids.android.view.mall3d.model.ModelColor;
import com.navior.ids.android.view.mall3d.model.ModelColorIndexed;
import com.navior.ids.android.view.mall3d.model.ModelLine;
import com.navior.ids.android.view.mall3d.model.ModelQuad;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ids.android.view.mall3d.util.AABB;
import com.navior.ips.model.Shop;
import com.navior.ips.model.type.ShapeType;

import java.util.ArrayList;

public class ShopModel extends Model {

  // Before building
  private Shop shop;
  private ArrayList<Vertex> vertices;
  private ArrayList<Triangle> triangles;
  private float[] angles;
  // building
  private Triangulator g = new TriangulatorB();
  private boolean clockwise;
  private ArrayColor wallData = null; public ArrayColor getWallData() { return wallData; }
  private ArrayColorIndexed roofData = null; public ArrayColorIndexed getRoofData() { return roofData; }
  private ArrayLine edgeData = null; public ArrayLine getEdgeData() { return edgeData; }
  public void clearArrayData() { wallData = null; roofData = null; edgeData = null; }
  // After building
  private float low;
  private float high;
  private AABB aabb = new AABB();
  private ModelColorIndexed roof = null;
  private ModelColor wall = null;
  private ModelLine edge = null;
  private boolean valid;
  public boolean isValid() { return valid; }

  public static final float BG_OPACITY = 0.85f;
  public static final float STR_OPACITY = 1.0f;

  private float floorHeight;
  public float getFloorHeight() { return floorHeight; }

  public Shop getShop() { return shop; }
  public ModelColorIndexed getRoof() { return roof; }
  public ModelLine getEdge() { return edge; }
  public ModelColor getWall() { return wall; }
  public AABB getAABB() { return aabb; }

  private ModelQuad shopIcon = null;
  public ModelQuad getShopIcon() { return shopIcon; }
  public void setShopIcon(ModelQuad shopIcon) { this.shopIcon = shopIcon; }

  public ShopModel(Shop shop, boolean isBG, float floorHeight) {
    this.floorHeight = floorHeight;
    modelPick();
    float[] pickColor = getPickColor();

    this.shop = shop;
    if(isBG) {
      this.low = floorHeight - ModelConstants.BG_HEIGHT;
      this.high = floorHeight;
    } else {
      this.low = floorHeight;
      this.high = floorHeight + ModelConstants.SHOP_HEIGHT;
    }

    float[] points = shop.getShape();
    if(points == null) return;
    for(int i = 0; i < points.length - 1; i += 2) {
      float x = points[i];
      float y = points[i + 1];
      aabb.combine(x, y);
    }

    boolean correct = false;
    try {
      correct = triangulate();
    } catch(Exception e) {
      e.printStackTrace();
    }
    if(!correct) {
      System.out.println("Triangulator Triangulation failed, id=" + shop.getId());
    }

    roofData = new ArrayColorIndexed(); roofData.setPickColor(pickColor);
    roof = new ModelColorIndexed();
    wallData = new ArrayColor(); wallData.setPickColor(pickColor);
    wall = new ModelColor();
    edgeData = new ArrayLine();
    edge = new ModelLine();

    if(shop.getT() == ShapeType.STR.getValue()) {
      this.setColor(new float[]{1, 1, 1, 1});
      this.setEdgeColor(new float[]{0, 0, 0, 1f});
    } else if(shop.getT() == ShapeType.DSBLD.getValue()) {
      this.setColor(new float[]{0.6f, 0.6f, 0.6f, STR_OPACITY});
      this.setEdgeColor(new float[]{1, 1, 1, 1});
    } else if(shop.getT() == ShapeType.EMPTY.getValue()) {
      roof = null;
      wall = null;
      edge = null;
    } else if(shop.getT() == ShapeType.BG.getValue()) {
      this.setColor(new float[]{0.8f, 0.8f, 0.8f, BG_OPACITY});
      this.setEdgeColor(new float[]{0, 0, 1, 1});
    } else {
      this.setColor(new float[]{1,1,1, 1});
      this.setEdgeColor(ModelConstants.EDGE_MATERIAL);
    }

    if(roof !=null)
      build(!clockwise);
  }

  Vertex lastVertex = null, currentVertex, firstVertex = null;

  // triangles if correct, null if incorrect.
  private boolean triangulate() {
    if(shop.getT() == (ShapeType.PICON.getValue()))
      return true;

    byte[] ops = shop.getOp();
    float[] fls = shop.getShape();
    int index = 0;

    boolean closed = false;
    float x;
    float y;

    if(ops == null)
      return true;
    for(byte op : ops) { //解析ops 未检查但不应出现多个move(0)或close(4)
      switch(op) {
      case 0:
        firstVertex = lastVertex = g.newVertex(fls[index++], fls[index++]);
        break;

      case 1:
        x = fls[index++];
        y = fls[index++];
        if((firstVertex != null) && (Math.abs(x - firstVertex.x) < 0.01f) //剔除重复点
            && (Math.abs(y - firstVertex.y) < 0.01f)) {
          continue;
        }
        currentVertex = g.newVertex(x, y);
        g.newEdge(lastVertex, currentVertex);
        lastVertex = currentVertex;
        break;

      case 2:
        lastVertex = bezierToPolygon(g, lastVertex, fls, index);
        index += 4;
        x = fls[index++];
        y = fls[index++];
        if((firstVertex != null) && (Math.abs(x - firstVertex.x) < 0.01f)
            && (Math.abs(y - firstVertex.y) < 0.01f)) {
          continue;
        }
        currentVertex = g.newVertex(x, y);
        g.newEdge(lastVertex, currentVertex);
        lastVertex = currentVertex;
        break;

      case 4:
        g.newEdge(lastVertex, g.getVertices().get(0));
        closed = true;
        break;

      default:
        break;
      }
      if(closed)
        break;
    }
    if(!closed) { //有些原数据未close，手动加上
      g.newEdge(lastVertex, firstVertex);
    }

    clockwise = g.confirmVertexEdge();

    boolean correct = g.triangulate();

    if(correct) {
      vertices = g.getVertices();
      triangles = g.getTriangles();
      angles = g.getAngles();
    } else {
      vertices = g.getVertices();
      triangles = null;
    }

    g = null;
    return correct;
  }

  private Vertex bezierToPolygon(Triangulator g, Vertex lastVertex, float[] fls, int index) {

    float p1x = lastVertex.x, p1y = lastVertex.y;
    float p2x = fls[index++], p2y = fls[index++];
    float p3x = fls[index++], p3y = fls[index++];
    float p4x = fls[index++], p4y = fls[index];

    float d = Math.abs(p1x - p2x) + Math.abs(p1y - p2y)
        + Math.abs(p2x - p3x) + Math.abs(p2y - p3y)
        + Math.abs(p3x - p4x) + Math.abs(p3y - p4y);
    double ld = Math.log10(d);
    int subDivideLevel = (int) (d / ld / ld / ModelConstants.BEZIER_DIVIDE_INTERVAL); //除以对数以保证短曲线的分割数量
    if(subDivideLevel < 1)
      subDivideLevel = 1;

    for(int i = 1; i != subDivideLevel; i++) { // not including the last point!

      float t = (float) i / (float) subDivideLevel;
      float t1 = 1 - t;

      float c1 = t1 * t1 * t1, c2 = 3 * t1 * t1 * t, c3 = 3 * t1 * t * t, c4 = t * t * t;

      float x = c1 * p1x + c2 * p2x + c3 * p3x + c4 * p4x; //两控制点的曲线的方程 p = t*t*t*p1 + 3*t*t*t1*p2 + 3*t*t1*t1*p3 + t1*t1*t1*p4
      float y = c1 * p1y + c2 * p2y + c3 * p3y + c4 * p4y;

      Vertex currentVertex = g.newVertex(x, y);
      g.newEdge(lastVertex, currentVertex);
      lastVertex = currentVertex;
    }

    return lastVertex;
  }

  private void build(boolean reverse) {
    valid = (triangles != null);
    if(triangles == null) {
      return;
    }

    // Roof
    roofData.startNewArray(vertices.size(), 3 * triangles.size());
    for(Vertex v : vertices) {
      roofData.newVertex(v.x, high, v.y);
    }
    for(Triangle t : triangles) {
      roofData.newTriangle(t.v1.id, t.v3.id, t.v2.id);
    }

    // Wall
    boolean[] duplicateVertex = new boolean[angles.length];
    int duplicateCount = 0;
    for(int i=0; i!=angles.length; i++) { //根据角度决定颜色是平滑过渡还是突变，也就是是否要额外创建两个顶点。平滑过渡的要区相邻两个四边形颜色的平均值。
      if(Math.abs(angles[i]) > ModelConstants.DUPLICATE_VERTEX_ANGLE_THRESHOLD) {
        duplicateCount++;
        duplicateVertex[i] = true;
      } else {
        duplicateVertex[i] = false;
      }
    }

    Vertex last = vertices.get(vertices.size()-1);
    wallData.startNewArray((vertices.size() + 1) * 2 + duplicateCount * 2, last.x, high, last.y, last.x, low, last.y);
    boolean lastDuplicate = true;
    for(int i = 0; i != vertices.size(); i++) {
      Vertex current = vertices.get(i);
      wallData.newTwoVertices(current.x, high, current.y, current.x, low, current.y, lastDuplicate, duplicateVertex[i], reverse);
      lastDuplicate = duplicateVertex[i];
    }
    wallData.endNewArray(duplicateVertex[vertices.size() - 1]);

    //edge
    float[] edgePoints = new float[vertices.size()*3 *2]; //(xyz -> xyz) * n
    edgePoints[0] = vertices.get(0).x;
    edgePoints[1] = high + ModelConstants.EDGE_Y_OFFSET;
    edgePoints[2] = vertices.get(0).y;
    for(int i=1; i!=vertices.size(); i++) {
      edgePoints[i*6-3] = vertices.get(i).x;
      edgePoints[i*6-2] = high + ModelConstants.EDGE_Y_OFFSET;
      edgePoints[i*6-1] = vertices.get(i).y;
      edgePoints[i*6  ] = vertices.get(i).x;
      edgePoints[i*6+1] = high + ModelConstants.EDGE_Y_OFFSET;
      edgePoints[i*6+2] = vertices.get(i).y;
    }
    int i = vertices.size();
    edgePoints[i*6-3] = vertices.get(0).x;
    edgePoints[i*6-2] = high + ModelConstants.EDGE_Y_OFFSET;
    edgePoints[i*6-1] = vertices.get(0).y;
    edgeData.setVertexArray(edgePoints);
    edgeData.width = ModelConstants.EDGE_WIDTH;

    vertices = null;
    triangles = null;
  }

  public void setColor(float[] color) {
    if(roof != null) {
      roofData.setDrawColor(color);
      wallData.setDrawColor(color);
    }
  }

  public void setEdgeColor(float[] edgeColor) {
    if(edge != null) {
      edgeData.color = edgeColor;
    }
  }

  public void pick() {
    if(roof != null) {
      roof.draw(Pass.PASS_PICK);
      wall.draw(Pass.PASS_PICK);
    }
  }

  public void draw() {
    if(roof != null) {
      roof.draw(Pass.PASS_DRAW);
      wall.draw(Pass.PASS_DRAW);
    }
    if(edge != null) {
      edge.draw(Pass.PASS_DRAW);
    }
  }
}
