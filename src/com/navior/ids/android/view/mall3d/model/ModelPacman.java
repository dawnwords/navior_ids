package com.navior.ids.android.view.mall3d.model;

import android.opengl.Matrix;
import android.util.SparseIntArray;

import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.util.ArrayList;
import java.util.List;

public class ModelPacman extends ModelColorIndexed {

  class Triangle {
    public short v1;
    public short v2;
    public short v3;

    public Triangle(int v1, int v2, int v3) {
      this.v1 = (short) v1;
      this.v2 = (short) v2;
      this.v3 = (short) v3;
    }
  }

  public List<float[]> currVertices = new ArrayList<float[]>();
  public List<Triangle> currTriangles = new ArrayList<Triangle>();
  public SparseIntArray midPoints = new SparseIntArray();

  //level-4 requires 25+-ms to load, which I think is acceptable.
  public ModelPacman(float radius, int level) {
    super();

    currVertices.add(new float[]{1, 0, 0});
    currVertices.add(new float[]{-1, 0, 0});
    currVertices.add(new float[]{0, 1, 0});
    currVertices.add(new float[]{0, -1, 0});
    currVertices.add(new float[]{0, 0, 1});
    currVertices.add(new float[]{0, 0, -1});

    currTriangles.add(new Triangle(0, 4, 2));
    currTriangles.add(new Triangle(4, 1, 2));
    currTriangles.add(new Triangle(1, 5, 2));
    currTriangles.add(new Triangle(5, 0, 2));
    currTriangles.add(new Triangle(4, 0, 3));
    currTriangles.add(new Triangle(1, 4, 3));
    currTriangles.add(new Triangle(5, 1, 3));
    currTriangles.add(new Triangle(0, 5, 3));

    for(int i = 0; i != level; i++) {
      List<Triangle> nextTriangles = new ArrayList<Triangle>(currTriangles.size() * 4);
      for(Triangle triangle : currTriangles) {
        short v1 = triangle.v1, v2 = triangle.v2, v3 = triangle.v3;
        short v4 = midPoint(v1, v2), v5 = midPoint(v2, v3), v6 = midPoint(v3, v1);

        nextTriangles.add(new Triangle(v1, v4, v6));
        nextTriangles.add(new Triangle(v4, v2, v5));
        nextTriangles.add(new Triangle(v4, v5, v6));
        nextTriangles.add(new Triangle(v6, v5, v3));
      }
      currTriangles = nextTriangles;
    }

    float[] vertexData = new float[currVertices.size() * 3];
    float[] colorData = new float[currVertices.size() * 3];
    short[] indexData = new short[currTriangles.size() * 3];

    float sqrt2d2 = (float) (Math.sqrt(2)/2f);
    for(int i = 0; i != currVertices.size(); i++) {
      float x = currVertices.get(i)[0], y = currVertices.get(i)[1], z = currVertices.get(i)[2];
      boolean changed = false;
      if(x >= 0 && y >= 0) {
        if(x > y) {
          x -= y; y = 0;
        } else {
          y -= x; x = 0;
        }
        if(x + y < 0.15f) {
          x = y = 0;
        }
        changed = true;
      }
      //rotate 45f around z clockwise
      float nx = (x+y) * sqrt2d2;
      float ny = (-x+y) * sqrt2d2;

      vertexData[i * 3    ] = nx * radius;
      vertexData[i * 3 + 1] = ny * radius;
      vertexData[i * 3 + 2] = z * radius;
      if(changed) {
        colorData[i * 3    ] = 0.8f;
        colorData[i * 3 + 1] = 0.8f;
        colorData[i * 3 + 2] = 0;
      } else {
        colorData[i * 3    ] = 0.7f;
        colorData[i * 3 + 1] = 0.7f + ny * 0.2f;
        colorData[i * 3 + 2] = 0;
      }
    }

    for(int i = 0; i != currTriangles.size(); i++) {
      indexData[i * 3    ] = currTriangles.get(i).v1;
      indexData[i * 3 + 1] = currTriangles.get(i).v2;
      indexData[i * 3 + 2] = currTriangles.get(i).v3;
    }

    this.vertexCount = currVertices.size();
    this.vertexBuffer = OpenglUtil.getFloatBuffer(vertexData);
    this.colorBuffer = OpenglUtil.getFloatBuffer(colorData);
    this.indexCount = indexData.length;
    this.indexBuffer = OpenglUtil.getShortBuffer(indexData);
    Matrix.setIdentityM(matrixWorld, 0);

    currVertices = null;
    currTriangles = null;

    setPasses();
  }

  public short midPoint(short a, short b) {
    int key;
    if(a > b) {
      short t = a;
      a = b;
      b = t;
    }
    key = a << 16 | b;

    Integer c;
    if((c = midPoints.get(key)) != 0) {
      return c.shortValue();
    }

    float[] p1 = currVertices.get(a), p2 = currVertices.get(b);

    float[] m = new float[3];
    m[0] = p1[0] + p2[0];
    m[1] = p1[1] + p2[1];
    m[2] = p1[2] + p2[2];
    normalize(m);

    return (short) addPoint(m, key);
  }

  public int addPoint(float[] v, int key) {
    currVertices.add(v);
    int i = currVertices.size() - 1;
    midPoints.put(key, i);
    return i;
  }

  public static void normalize(float[] c) {
    float x = c[0], y = c[1], z = c[2];
    double l2 = x * x + y * y + z * z;
    double d = 1.0 / Math.sqrt(l2);
    c[0] *= d;
    c[1] *= d;
    c[2] *= d;
  }

}
