package com.navior.ids.android.view.mall3d.model;

import com.navior.ids.android.view.mall3d.appModel.ModelConstants;

public class ArrayColor {

  int vertexCount;
  int vertexFloatCount;
  float[] vertexArray;
  float[] colorArray;
  float[] pickArray;
  private int currentFloatPointer;
  private int currentVertexPointer;

  private float[] drawColor; public void setDrawColor(float[] drawColor) { this.drawColor = drawColor; }
  private float[] pickColor; public void setPickColor(float[] pickColor) { this.pickColor = pickColor; }

  //vertexCount = number of vertices.
  public void startNewArray(
      int vertexCount,
      float x1, float y1, float z1, //first high vertex position
      float x2, float y2, float z2  //first low vertex position
  ) {
    this.vertexCount = vertexCount;
    this.vertexFloatCount = vertexCount * 3;
    currentFloatPointer = 0;
    currentVertexPointer = 0;
    vertexArray = new float[vertexFloatCount];
    colorArray = new float[vertexFloatCount];
    pickArray = new float[vertexFloatCount];

    float[] black = new float[]{0,0,0};
    i1 = newVertex(x1,y1,z1, black);
    i2 = newVertex(x2,y2,z2, black);

    this.x1 = x1; this.y1 = y1; this.z1 = z1;
  }

  private int newVertex(float x, float y, float z, float[] rgb) {
    vertexArray[currentFloatPointer  ] = x;
    vertexArray[currentFloatPointer+1] = y;
    vertexArray[currentFloatPointer+2] = z;
    colorArray[currentFloatPointer  ] = rgb[0];
    colorArray[currentFloatPointer+1] = rgb[1];
    colorArray[currentFloatPointer+2] = rgb[2];
    pickArray[currentFloatPointer  ] = pickColor[0];
    pickArray[currentFloatPointer+1] = pickColor[1];
    pickArray[currentFloatPointer+2] = pickColor[2];

    currentFloatPointer += 3;
    return currentVertexPointer++;
  }

  private int i1, i2; //last high vertex index, last low vertex index.
  private float x1,y1,z1; //last high vertex position
  public void newTwoVertices(
      float x3, float y3, float z3, //new high vertex position
      float x4, float y4, float z4, //new low vertex position
      boolean lastDuplicate, boolean duplicate, boolean reverse
  ) {
    //normal = normalize(34 * 31). (right handed, normal out).
    float dx1 = x4 - x3, dy1 = y4 - y3, dz1 = z4 - z3;
    float dx2 = x1 - x3, dy2 = y1 - y3, dz2 = z1 - z3;
    double nx = dy1 * dz2 - dy2 * dz1;
    double nz = dx1 * dy2 - dx2 * dy1;
    double nl = Math.sqrt(nx * nx + nz * nz); //也许有精度上的问题导致NaN产生，尚未解决

    nx /= nl;
    nz /= nl;
    if(reverse) {
      nx = -nx;
      nz = -nz;
    }

    float[] color = computeColor(nx, 0, nz);

    if(lastDuplicate) {
      colorArray[i1*3  ] = color[0];
      colorArray[i1*3+1] = color[1];
      colorArray[i1*3+2] = color[2];
      colorArray[i2*3  ] = color[0];
      colorArray[i2*3+1] = color[1];
      colorArray[i2*3+2] = color[2];
    } else {
      colorArray[i1*3  ] = (colorArray[i1*3  ] + color[0]) / 2;
      colorArray[i1*3+1] = (colorArray[i1*3+1] + color[1]) / 2;
      colorArray[i1*3+2] = (colorArray[i1*3+2] + color[2]) / 2;
      colorArray[i2*3  ] = (colorArray[i2*3  ] + color[0]) / 2;
      colorArray[i2*3+1] = (colorArray[i2*3+1] + color[1]) / 2;
      colorArray[i2*3+2] = (colorArray[i2*3+2] + color[2]) / 2;
    }

    i1 = newVertex(x3, y3, z3, color); x1 = x3; y1 = y3; z1 = z3;
    i2 = newVertex(x4, y4, z4, color);

    if(duplicate) {
      i1 = newVertex(x3, y3, z3, color);
      i2 = newVertex(x4, y4, z4, color);
    }
  }

  public void endNewArray(boolean lastDuplicate) {
    if(!lastDuplicate) { //为前两个和最后两个顶点平均颜色(他们位置相同)
      float r,g,b;
      r = (colorArray[i1*3  ] + colorArray[0])/2;
      g = (colorArray[i1*3+1] + colorArray[1])/2;
      b = (colorArray[i1*3+2] + colorArray[2])/2;
      colorArray[0] = r;
      colorArray[1] = g;
      colorArray[2] = b;
      colorArray[3] = r;
      colorArray[4] = g;
      colorArray[5] = b;
      colorArray[i1*3  ] = r;
      colorArray[i1*3+1] = g;
      colorArray[i1*3+2] = b;
      colorArray[i2*3  ] = r;
      colorArray[i2*3+1] = g;
      colorArray[i2*3+2] = b;
    }
  }

  protected float[] computeColor(double nx, double ny, double nz) {
    float[] colors = new float[3];
    if(ny > 0.9f) {
      colors[0] = drawColor[0] * ModelConstants.ROOF_MATERIAL[0];
      colors[1] = drawColor[1] * ModelConstants.ROOF_MATERIAL[1];
      colors[2] = drawColor[2] * ModelConstants.ROOF_MATERIAL[2];
    } else {
      double dot = nx * ModelConstants.LIGHT_DIRECTION_XZ[0] + nz * ModelConstants.LIGHT_DIRECTION_XZ[1];
      if(dot < 0) dot = dot/2;
      dot = (dot + 2.0f) / 3.0f;
      colors[0] = (float) (dot * drawColor[0] * ModelConstants.WALL_MATERIAL[0]);
      colors[1] = (float) (dot * drawColor[1] * ModelConstants.WALL_MATERIAL[1]);
      colors[2] = (float) (dot * drawColor[2] * ModelConstants.WALL_MATERIAL[2]);
    }
    return colors;
  }
}