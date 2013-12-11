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
 * @date 2013-7-8
 */

package com.navior.ids.android.view.mall3d.model;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.appModel.ModelConstants;
import com.navior.ids.android.view.mall3d.mesh.MeshColor;
import com.navior.ids.android.view.mall3d.mesh.MeshColorBuffer;
import com.navior.ids.android.view.mall3d.pipeline.PipelineName;
import com.navior.ids.android.view.mall3d.util.Combiner;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ModelColor extends Model {

  protected int verticesNumber;

  protected FloatBuffer verticesBuffer;
  protected FloatBuffer colorBuffer;

  protected float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};

  public ModelColor() {
    super();
  }

  public ModelColor(String meshFileName) {
    super();
    File file = new File(meshFileName);
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    }
    Scanner input = new Scanner(inputStream);

    verticesNumber = Integer.parseInt(input.next());
    float[] vertices = new float[verticesNumber * 3];
    float[] colors = new float[verticesNumber * 3];
    for(int i = 0; i != verticesNumber; i++) {
      vertices[i * 3] = Float.parseFloat(input.next());
      vertices[i * 3 + 1] = Float.parseFloat(input.next());
      vertices[i * 3 + 2] = Float.parseFloat(input.next());
      colors[i * 3] = Float.parseFloat(input.next());
      colors[i * 3 + 1] = Float.parseFloat(input.next());
      colors[i * 3 + 2] = Float.parseFloat(input.next());
    }

    //will change to string based resource management system
    setupBuffersColor(vertices, colors);

    input.close();
  }

  public ModelColor(float[] vertices, float[] colors) {
    super();
    setupBuffersColor(vertices, colors);
  }

  public ModelColor(int verticesCount, FloatBuffer vertexBuffer, FloatBuffer colorBuffer) {
    super();
    setupBuffers(verticesCount, vertexBuffer, colorBuffer);
  }

  public static List<ModelColor> combine(List<ModelColor> modelColors) {
    return new Combiner<ModelColor, ModelColor>() {
      ArrayList<ModelColor> currentList = new ArrayList<ModelColor>();
      int currentPointer;

      @Override
      protected ModelColor open() {
        currentPointer = 0;
        return new ModelColor();
      }

      @Override
      protected boolean beyondLimit(ModelColor i) {
        return i.getVertexArray().length + 6 + currentPointer >= ModelConstants.VERTEX_BUFFER_MAX_LENGTH;
      }

      @Override
      protected void combine(ModelColor i, ModelColor o) {
        currentPointer += 6 + i.getVertexArray().length;
        currentList.add(i);
      }

      @Override
      protected void close(ModelColor o) {
        int[] offset = new int[currentList.size()];
        int[] count = new int[currentList.size()];
        float[] vertex = new float[currentPointer];
        float[] color = new float[currentPointer];
        currentPointer = 0;

        for(int d = 0; d!= currentList.size(); d++) {
          ModelColor wall = currentList.get(d);
          float[] vertexSingle = wall.getVertexArray();
          float[] colorSingle = wall.getColorArray();

          offset[d] = currentPointer;
          count[d] = vertexSingle.length + 6;

          //dummy point
          for(int i=0; i!=3; i++) {
            vertex[currentPointer] = vertexSingle[i];
            color[currentPointer++] = colorSingle[i];
          }

          //wall
          for(int i=0; i!=vertexSingle.length; i++) {
            vertex[currentPointer] = vertexSingle[i];
            color[currentPointer++] = colorSingle[i];
          }

          //dummy point
          for(int i=vertexSingle.length-3; i!=vertexSingle.length; i++) {
            vertex[currentPointer] = vertexSingle[i];
            color[currentPointer++] = colorSingle[i];
          }
        }

        o.setupBuffersColor(vertex, color);

        for(int d = 0; d!= currentList.size(); d++) { //给shopModel填上带有offset的vb/cb
          FloatBuffer vb = o.getVerticesBuffer().duplicate(); vb.position(offset[d]);
          FloatBuffer cb = o.getColorBuffer().duplicate(); cb.position(offset[d]);
          currentList.get(d).setupBuffers(count[d] / 3, vb, cb);
        }
        currentList.clear();
      }
    }.run(modelColors);
  }

  public void setupBuffers(int verticesCount, FloatBuffer vertexBuffer, FloatBuffer colorBuffer) {
    this.verticesNumber = verticesCount;
    this.verticesBuffer = vertexBuffer;
    this.colorBuffer = colorBuffer;
    vertexArray = null;
    colorArray = null;
  }

  public FloatBuffer getVerticesBuffer() {
    return verticesBuffer;
  }

  public FloatBuffer getColorBuffer() {
    return colorBuffer;
  }

  public void setupBuffersColor(float[] vertices, float[] color) {
    setupBuffers(vertices.length / 3, OpenglUtil.getFloatBuffer(vertices), OpenglUtil.getFloatBuffer(color));
  }

  protected float[] computeColor(double nx, double ny, double nz) {
    float[] colors = new float[3];
    if(ny > 0.9f) {
      colors[0] = color[0] * ModelConstants.ROOF_MATERIAL[0];
      colors[1] = color[1] * ModelConstants.ROOF_MATERIAL[1];
      colors[2] = color[2] * ModelConstants.ROOF_MATERIAL[2];
    } else {
      double dot = nx * ModelConstants.LIGHT_DIRECTION_XZ[0] + nz * ModelConstants.LIGHT_DIRECTION_XZ[1];
      if(dot < 0) dot = dot/2;
      dot = (dot + 2.0f) / 3.0f;
      colors[0] = (float) (dot * color[0] * ModelConstants.WALL_MATERIAL[0]);
      colors[1] = (float) (dot * color[1] * ModelConstants.WALL_MATERIAL[1]);
      colors[2] = (float) (dot * color[2] * ModelConstants.WALL_MATERIAL[2]);
    }
    return colors;
  }

  private short vertexArrayPointer;
  private float[] vertexArray;
  private float[] colorArray;

  //----Wall Construction----
  //verticesNumber = number of vertices.
  public void startNewArray(int verticesNumber, float x1, float y1, float z1, float x2, float y2, float z2) {
    this.verticesNumber = verticesNumber;
    vertexArrayPointer = 0;
    vertexArray = new float[verticesNumber * 3];
    colorArray = new float[verticesNumber * 3];
    i1 = newVertex(x1,y1,z1, 0,0,0);
    i2 = newVertex(x2,y2,z2, 0,0,0);
    this.x1 = x1; this.y1 = y1; this.z1 = z1;
  }

  public int newVertex(float x, float y, float z, float r, float g, float b) {
    vertexArray[vertexArrayPointer] = x;
    vertexArray[vertexArrayPointer + 1] = y;
    vertexArray[vertexArrayPointer + 2] = z;
    colorArray[vertexArrayPointer] = r;
    colorArray[vertexArrayPointer + 1] = g;
    colorArray[vertexArrayPointer + 2] = b;
    int result = vertexArrayPointer / 3;
    vertexArrayPointer += 3;
    return result;
  }

  public int newVertex(float x, float y, float z, float[] rgb) {
    vertexArray[vertexArrayPointer] = x;
    vertexArray[vertexArrayPointer + 1] = y;
    vertexArray[vertexArrayPointer + 2] = z;
    colorArray[vertexArrayPointer] = rgb[0];
    colorArray[vertexArrayPointer + 1] = rgb[1];
    colorArray[vertexArrayPointer + 2] = rgb[2];
    int result = vertexArrayPointer / 3;
    vertexArrayPointer += 3;
    return result;
  }

  int i1, i2;
  float x1,y1,z1;
  public void newTwoVertices(
      float x3, float y3, float z3,
      float x4, float y4, float z4,
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
  public void finishNewArray() {
    setupBuffersColor(vertexArray, colorArray);
  }
  public float[] getVertexArray() {return vertexArray;}
  public float[] getColorArray() {return colorArray;}
  //----Wall Construction----

  public void setColor(float[] color) {
    this.color = color;
  }
  public float[] getColor() {
    return color;
  }


  @Override
  public void draw(boolean selected) {
    if(verticesBuffer!=null)
      OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_COLORBUFFER, new MeshColorBuffer(
          matrixWorld, selected?ModelConstants.SELECTED_ALPHA:ModelConstants.UNSELECTED_ALPHA, verticesNumber, verticesBuffer, colorBuffer, GLES20.GL_TRIANGLE_STRIP
      ));
  }

  @Override
  public void pick() {
    if(verticesBuffer!=null)
      OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_COLOR, new MeshColor(
          matrixWorld, 1, verticesNumber, verticesBuffer, getPickColor(), GLES20.GL_TRIANGLE_STRIP
      ));
  }
}
