package com.navior.ids.android.view.mall3d.model;

import com.navior.ids.android.view.mall3d.util.Holder;

public class ArrayQuad {

  int quadCount;
  int vertexCount;
  int vertexFloatCount;
  int texcoordFloatCount;
  int indexCount;
  float[] vertexArray;
  float[] texcoordArray;
  short[] indexArray;
  Holder<Integer> texture;
  private short currentVertexPointer;
  private int currentVertexFloatPointer;
  private int currentTexcoordFloatPointer;
  private int currentIndexPointer;

  public ArrayQuad(int quadCount, Holder<Integer> texture) {
    this.quadCount = quadCount;
    this.vertexCount = quadCount * 4;
    this.vertexFloatCount = quadCount * 12;
    this.texcoordFloatCount = quadCount * 8;
    this.indexCount = quadCount * 6;
    this.texture = texture;
    vertexArray = new float[vertexFloatCount];
    texcoordArray = new float[texcoordFloatCount];
    indexArray = new short[indexCount];
    currentVertexPointer = 0;
    currentVertexFloatPointer = 0;
    currentTexcoordFloatPointer = 0;
    currentIndexPointer = 0;
  }

  public static float[] getPosition(float px, float py, float pz, float sx, float sz) {
    float[] result = new float[12];
    for(int i = 0; i != 12; i += 3) {
      float x = ModelQuad.VERTEX_QUAD[i], y = ModelQuad.VERTEX_QUAD[i + 1], z = ModelQuad.VERTEX_QUAD[i + 2];
      result[i] = px + x * sx;
      result[i + 1] = py + y;
      result[i + 2] = pz + z * sz;
    }
    return result;
  }

  public void addQuad(float px, float py, float pz, float sx, float sz, float left, float right, float top, float bottom) {
    //assert position.length==12 && texcoord.length==8;

    short i0 = currentVertexPointer++;
    short i1 = currentVertexPointer++;
    short i2 = currentVertexPointer++;
    short i3 = currentVertexPointer++;

    indexArray[currentIndexPointer++] = i0;
    indexArray[currentIndexPointer++] = i1;
    indexArray[currentIndexPointer++] = i2;
    indexArray[currentIndexPointer++] = i1;
    indexArray[currentIndexPointer++] = i3;
    indexArray[currentIndexPointer++] = i2;

    for(int i = 0; i != 12; i += 3) {
      float x = ModelQuad.VERTEX_QUAD[i], y = ModelQuad.VERTEX_QUAD[i + 1], z = ModelQuad.VERTEX_QUAD[i + 2];
      vertexArray[currentVertexFloatPointer++] = px + x * sx;
      vertexArray[currentVertexFloatPointer++] = py + y;
      vertexArray[currentVertexFloatPointer++] = pz + z * sz;
    }

    texcoordArray[currentTexcoordFloatPointer++] = left;
    texcoordArray[currentTexcoordFloatPointer++] = 1 - top;
    texcoordArray[currentTexcoordFloatPointer++] = right;
    texcoordArray[currentTexcoordFloatPointer++] = 1 - top;
    texcoordArray[currentTexcoordFloatPointer++] = left;
    texcoordArray[currentTexcoordFloatPointer++] = 1 - bottom;
    texcoordArray[currentTexcoordFloatPointer++] = right;
    texcoordArray[currentTexcoordFloatPointer++] = 1 - bottom;
  }
}