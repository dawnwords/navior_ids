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
import com.navior.ids.android.view.mall3d.mesh.MeshColorBufferIndexed;
import com.navior.ids.android.view.mall3d.mesh.MeshColorIndexed;
import com.navior.ids.android.view.mall3d.pipeline.PipelineName;
import com.navior.ids.android.view.mall3d.util.Combiner;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ModelColorIndexed extends Model {

  protected int verticesNumber;
  protected FloatBuffer verticesBuffer;
  protected FloatBuffer colorBuffer;

  protected int indicesNumber;
  protected ShortBuffer indicesBuffer;

  public static List<ModelColorIndexed> combine(List<ModelColorIndexed> modelColorIndexeds) {
    return new Combiner<ModelColorIndexed, ModelColorIndexed>() {
      ArrayList<ModelColorIndexed> currentList = new ArrayList<ModelColorIndexed>();
      int currentVertexPointer;
      int currentIndexPointer;

      @Override
      protected ModelColorIndexed open() {
        currentVertexPointer = 0;
        currentIndexPointer = 0;
        return new ModelColorIndexed();
      }

      @Override
      protected boolean beyondLimit(ModelColorIndexed i) {
        boolean vertexLimit = i.getVertexArray().length + currentVertexPointer >= ModelConstants.VERTEX_BUFFER_MAX_LENGTH;
        boolean indexLimit = i.getIndexArray().length + currentIndexPointer >= ModelConstants.INDEX_BUFFER_MAX_LENGTH;
        return vertexLimit || indexLimit;
      }

      @Override
      protected void combine(ModelColorIndexed i, ModelColorIndexed o) {
        currentVertexPointer += i.getVertexArray().length;
        currentIndexPointer += i.getIndexArray().length;
        currentList.add(i);
      }

      @Override
      protected void close(ModelColorIndexed o) {
        int[] vertexOffset = new int[currentList.size()];
        int[] vertexCount = new int[currentList.size()];
        int[] indexOffset = new int[currentList.size()];
        int[] indexCount = new int[currentList.size()];
        float[] vertex = new float[currentVertexPointer];
        float[] color = new float[currentVertexPointer];
        short[] index = new short[currentVertexPointer];
        currentVertexPointer = 0;
        currentIndexPointer = 0;

        for(int d = 0; d != currentList.size(); d++) {
          ModelColorIndexed roof = currentList.get(d);
          float[] vertexSingle = roof.getVertexArray();
          float[] colorSingle = roof.getColorArray();
          short[] indexSingle = roof.getIndexArray();

          vertexOffset[d] = currentVertexPointer;
          vertexCount[d] = vertexSingle.length;
          indexOffset[d] = currentIndexPointer;
          indexCount[d] = indexSingle.length;

          //roof
          for(int i = 0; i != vertexSingle.length; i++) {
            vertex[currentVertexPointer] = vertexSingle[i];
            color[currentVertexPointer++] = colorSingle[i];
          }
          for(int i = 0; i != indexSingle.length; i++) {
            index[currentIndexPointer++] = (short) (indexSingle[i] + vertexOffset[d] / 3);
          }
        }

        o.setupBuffersColor(vertex, color, index);

        for(int d = 0; d != currentList.size(); d++) {
          FloatBuffer vb = o.getVerticesBuffer().duplicate();
          FloatBuffer cb = o.getColorBuffer().duplicate();
          ShortBuffer ib = o.getIndicesBuffer().duplicate();
          ib.position(indexOffset[d]);
          currentList.get(d).setupBuffers(vertexCount[d], vb, cb, indexCount[d], ib);
        }
        currentList.clear();
      }
    }.run(modelColorIndexeds);
  }

  public FloatBuffer getVerticesBuffer() {
    return verticesBuffer;
  }

  public FloatBuffer getColorBuffer() {
    return colorBuffer;
  }

  public ShortBuffer getIndicesBuffer() {
    return indicesBuffer;
  }

  public short[] getIndexArray() {
    return indexArray;
  }

  public float[] getColorArray() {
    return colorArray;
  }

  public float[] getVertexArray() {
    return vertexArray;
  }

  public int getIndicesNumber() {
    return indicesNumber;
  }

  public int getVerticesNumber() {
    return verticesNumber;
  }

  protected float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};

  public ModelColorIndexed() {
    super();
  }

  public ModelColorIndexed(String meshFileName) {
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

    indicesNumber = Integer.parseInt(input.next());
    short[] indices = new short[indicesNumber];
    for(int i = 0; i != indicesNumber; i++) {
      indices[i] = Short.parseShort(input.next());
    }

    //will change to string based resource management system
    setupBuffersColor(vertices, colors, indices);

    input.close();
  }

  public ModelColorIndexed(float[] vertices, float[] colors, short[] indices) {
    super();
    setupBuffersColor(vertices, colors, indices);
  }

  public ModelColorIndexed(int verticesNumber, FloatBuffer verticesBuffer, FloatBuffer colorBuffer, int indicesNumber, ShortBuffer indicesBuffer) {
    super();
    setupBuffers(verticesNumber, verticesBuffer, colorBuffer, indicesNumber, indicesBuffer);
  }

  public void setupBuffers(int verticesNumber, FloatBuffer verticesBuffer, FloatBuffer colorBuffer, int indicesNumber, ShortBuffer indicesBuffer) {
    this.verticesNumber = verticesNumber;
    this.verticesBuffer = verticesBuffer;
    this.colorBuffer = colorBuffer;
    this.indicesNumber = indicesNumber;
    this.indicesBuffer = indicesBuffer;
  }

  public void setupBuffersColor(float[] vertices, float[] color, short[] indices) {
    verticesBuffer = OpenglUtil.getFloatBuffer(vertices);
    colorBuffer = OpenglUtil.getFloatBuffer(color);
    indicesBuffer = OpenglUtil.getShortBuffer(indices);
    indicesNumber = indices.length;
    vertexArray = null;
    colorArray = null;
    indexArray = null;
  }

  private float[] computeColor(double nx, double ny, double nz) {
    float[] colors = new float[3];
    if(ny > 0.9f) {
      colors[0] = color[0] * ModelConstants.ROOF_MATERIAL[0];
      colors[1] = color[1] * ModelConstants.ROOF_MATERIAL[1];
      colors[2] = color[2] * ModelConstants.ROOF_MATERIAL[2];
    } else {
      double dot = nx * ModelConstants.LIGHT_DIRECTION_XZ[0] + nz * ModelConstants.LIGHT_DIRECTION_XZ[1];
      if(dot < -0.5f) dot = -0.5f;
      dot = (dot + 2.0f) / 3.0f;
      colors[0] = (float) (dot * color[0] * ModelConstants.WALL_MATERIAL[0]);
      colors[1] = (float) (dot * color[1] * ModelConstants.WALL_MATERIAL[1]);
      colors[2] = (float) (dot * color[2] * ModelConstants.WALL_MATERIAL[2]);
    }
    return colors;
  }

  private short vertexArrayPointer;
  private int indexArrayPointer;
  private float[] vertexArray;
  private float[] colorArray;
  private short[] indexArray;

  //verticesNumber = number of vertices.
  //indicesNumber = 3 * number of triangles.
  public void startNewArray(int verticesNumber, int indicesNumber) {
    this.verticesNumber = verticesNumber;
    this.indicesNumber = indicesNumber;
    vertexArrayPointer = 0;
    indexArrayPointer = 0;
    vertexArray = new float[verticesNumber * 3];
    colorArray = new float[verticesNumber * 3];
    indexArray = new short[indicesNumber];
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

  public int newTriangle(int a, int b, int c) {
    indexArray[indexArrayPointer] = (short) a;
    indexArray[indexArrayPointer + 1] = (short) b;
    indexArray[indexArrayPointer + 2] = (short) c;
    int result = indexArrayPointer;
    indexArrayPointer += 3;
    return result;
  }

  public void endNewArray() {
//    setupBuffersColor(vertexArray, colorArray, indexArray);
  }

  public void setColor(float[] color) {
    this.color = color;
  }

  public float[] getColor() {
    return color;
  }

  @Override
  public void draw(boolean selected) {
    if(verticesBuffer != null)
      OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_COLORBUFFER, new MeshColorBufferIndexed(
          matrixWorld, selected?ModelConstants.SELECTED_ALPHA:ModelConstants.UNSELECTED_ALPHA, verticesBuffer, colorBuffer, indicesNumber, indicesBuffer, GLES20.GL_TRIANGLES
      ));
  }

  @Override
  public void pick() {
    if(verticesBuffer != null)
      OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_COLOR, new MeshColorIndexed(
          matrixWorld, 1, verticesBuffer, getPickColor(), indicesNumber, indicesBuffer, GLES20.GL_TRIANGLES
      ));
  }

  public void finishNewArray() {
    setupBuffersColor(vertexArray, colorArray, indexArray);
  }
}
