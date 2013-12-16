package com.navior.ids.android.view.mall3d.model;

public class ArrayColorIndexed {

  int vertexCount;
  int vertexFloatCount;
  int indexCount;
  float[] vertexArray;
  float[] colorArray;
  float[] pickArray;
  short[] indexArray;
  private int currentFloatPointer;
  private int currentVertexPointer;
  private int currentIndexPointer;

  private float[] drawColor; public void setDrawColor(float[] drawColor) { this.drawColor = drawColor; }
  private float[] pickColor; public void setPickColor(float[] pickColor) { this.pickColor = pickColor; }

  //vertexCount = number of vertices.
  //vertexFloatCount = number of vertex floats
  //indexCount = 3 * number of triangles.
  public void startNewArray(int vertexCount, int indexCount) {
    this.vertexCount = vertexCount;
    this.vertexFloatCount = vertexCount * 3;
    this.indexCount = indexCount;
    currentFloatPointer = 0;
    currentVertexPointer = 0;
    currentIndexPointer = 0;
    vertexArray = new float[vertexFloatCount];
    colorArray = new float[vertexFloatCount];
    pickArray = new float[vertexFloatCount];
    indexArray = new short[indexCount];
  }

  public int newVertex(float x, float y, float z) {
    vertexArray[currentFloatPointer  ] = x;
    vertexArray[currentFloatPointer+1] = y;
    vertexArray[currentFloatPointer+2] = z;
    colorArray[currentFloatPointer  ] = drawColor[0];
    colorArray[currentFloatPointer+1] = drawColor[1];
    colorArray[currentFloatPointer+2] = drawColor[2];
    pickArray[currentFloatPointer  ] = pickColor[0];
    pickArray[currentFloatPointer+1] = pickColor[1];
    pickArray[currentFloatPointer+2] = pickColor[2];
    currentFloatPointer += 3;
    return currentVertexPointer++;
  }

  public void newTriangle(int a, int b, int c) {
    indexArray[currentIndexPointer  ] = (short) a;
    indexArray[currentIndexPointer+1] = (short) b;
    indexArray[currentIndexPointer+2] = (short) c;
    currentIndexPointer += 3;
  }
}