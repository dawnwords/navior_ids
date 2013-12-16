package com.navior.ids.android.view.mall3d.model;

public class ArrayLine {

  int vertexCount;
  int vertexFloatCount;
  float[] vertexArray;
  public float[] color;
  public float width;

  public ArrayLine() {

  }

  public ArrayLine(float[] vertexArray, float[] color, float width) {
    this.vertexCount = vertexArray.length / 3;
    this.vertexFloatCount = vertexArray.length;
    this.vertexArray = vertexArray;
    this.color = color;
    this.width = width;
  }

  public void setVertexArray(float[] vertexArray) {
    this.vertexCount = vertexArray.length / 3;
    this.vertexFloatCount = vertexArray.length;
    this.vertexArray = vertexArray;
  }
}