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
 * @date 13-7-19
 */
package com.navior.ids.android.view.mall3d.model;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.appModel.ModelConstants;
import com.navior.ids.android.view.mall3d.mesh.MeshLineStrip;
import com.navior.ids.android.view.mall3d.pipeline.PipelineName;
import com.navior.ids.android.view.mall3d.util.Combiner;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLineStrip extends Model {

  private int verticesNumber;

  public static List<ModelLineStrip> combine(List<ModelLineStrip> modelLineStrips) {
    return new Combiner<ModelLineStrip, ModelLineStrip>() {
      ArrayList<ModelLineStrip> currentList = new ArrayList<ModelLineStrip>();
      int currentVertexPointer;

      @Override
      protected ModelLineStrip open() {
        currentVertexPointer = 0;
        return new ModelLineStrip();
      }

      @Override
      protected boolean beyondLimit(ModelLineStrip i) {
        return i.getLine().length + currentVertexPointer >= ModelConstants.VERTEX_BUFFER_MAX_LENGTH;
      }

      @Override
      protected void combine(ModelLineStrip i, ModelLineStrip o) {
        currentVertexPointer += i.getLine().length;
        currentList.add(i);
      }

      @Override
      protected void close(ModelLineStrip o) {
        int[] vertexOffset = new int[currentList.size()];
        int[] vertexCount = new int[currentList.size()];
        float[] vertex = new float[currentVertexPointer];
        currentVertexPointer = 0;

        for(int d = 0; d!= currentList.size(); d++) {
          ModelLineStrip edge = currentList.get(d);
          float[] vertexSingle = edge.getLine();

          vertexOffset[d] = currentVertexPointer;
          vertexCount[d] = vertexSingle.length;

          //edge
          for(int i=0; i!=vertexSingle.length; i++) {
            vertex[currentVertexPointer++] = vertexSingle[i];
          }
        }

        o.finishLine(vertex, ModelConstants.EDGE_MATERIAL, ModelConstants.EDGE_WIDTH);

        for(int d = 0; d!= currentList.size(); d++) {
          FloatBuffer vb = o.getVerticesBuffer().duplicate(); vb.position(vertexOffset[d]);
          currentList.get(d).setupBuffers(vertexCount[d]/3, vb, ModelConstants.EDGE_MATERIAL, ModelConstants.EDGE_WIDTH);
        }
        currentList.clear();
      }
    }.run(modelLineStrips);
  }

  public FloatBuffer getVerticesBuffer() {
    return verticesBuffer;
  }

  private FloatBuffer verticesBuffer;
  private float[] color;
  private float width;
  private int topology = GLES20.GL_LINES;

  public void setTopology(int topology) {
    this.topology = topology;
  }

  public ModelLineStrip() {
    super();
  }

  public ModelLineStrip(float[] line, float[] color, float width) {
    super();
    finishLine(line, color, width);
  }

  public void finishLine(float[] line, float[] color, float width) {
    setupBuffers(line.length / 3, OpenglUtil.getFloatBuffer(line), color, width);
  }

  public ModelLineStrip(int verticesNumber, FloatBuffer verticesBuffer, float[] color, float width) {
    super();
    setupBuffers(verticesNumber, verticesBuffer, color, width);
  }

  public void setupBuffers(int verticesNumber, FloatBuffer verticesBuffer, float[] color, float width) {
    this.verticesNumber = verticesNumber;
    this.verticesBuffer = verticesBuffer;
    this.color = color;
    this.width = width;
  }

  float[] line;
  public void setLine(float[] line) {
    verticesNumber = line.length / 3;
    this.line = line;
  }

  public void setColor(float[] color) {
    this.color = color;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float[] getLine() {
    return line;
  }



  @Override
  public void pick() { // lines are not drawn when picking.
  }

  @Override
  public void draw(boolean selected) {
    if(verticesBuffer!=null)
      OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_LINESTRIP, new MeshLineStrip(
          matrixWorld, selected?ModelConstants.SELECTED_ALPHA:ModelConstants.UNSELECTED_ALPHA, verticesNumber, verticesBuffer, color, width, topology
      ));
  }

  public void finishLine() {
    finishLine(line, color, width);
  }
}
