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
import com.navior.ids.android.view.mall3d.mesh.MeshColorBuffer;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;
import com.navior.ids.android.view.mall3d.util.Combiner;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelColor extends Model {

  protected int vertexCount;
  protected FloatBuffer vertexBuffer;
  protected FloatBuffer colorBuffer;
  protected FloatBuffer pickBuffer;

  public ModelColor() {
    super();
  }

  public void finish(ArrayColor arrayColor) {
    FloatBuffer positionBuffer = OpenglUtil.getFloatBuffer(arrayColor.vertexArray);
    FloatBuffer colorBuffer = OpenglUtil.getFloatBuffer(arrayColor.colorArray);
    FloatBuffer pickBuffer = OpenglUtil.getFloatBuffer(arrayColor.pickArray);

    finish(arrayColor.vertexCount, positionBuffer, colorBuffer, pickBuffer);
  }

  public void finish(int vertexCount, FloatBuffer vertexBuffer, FloatBuffer colorBuffer, FloatBuffer pickBuffer) {
    this.vertexCount = vertexCount;
    this.vertexBuffer = vertexBuffer;
    this.colorBuffer = colorBuffer;
    this.pickBuffer = pickBuffer;

    setPasses();
  }

  protected void setPasses() {
    setPass(Pass.PASS_DRAW, Pipeline.PIPELINE_COLORBUFFER, new MeshColorBuffer(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, vertexBuffer, colorBuffer, null, vertexCount, GLES20.GL_TRIANGLE_STRIP
    ));

    setPass(Pass.PASS_PICK, Pipeline.PIPELINE_COLORBUFFER, new MeshColorBuffer(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, vertexBuffer, pickBuffer, null, vertexCount, GLES20.GL_TRIANGLE_STRIP
    ));
  }


  public static List<ModelColor> combine(List<ArrayColor> arrayColors, final List<ModelColor> singleModelColors) {
    return new Combiner<ArrayColor, ModelColor>() {
      private ArrayList<ArrayColor> currentCombinationList = new ArrayList<ArrayColor>();
      private int currentFloatCount;
      private int singleModelColorPointer = 0;

      @Override
      protected ModelColor open() {
        currentFloatCount = 0;
        return new ModelColor();
      }

      @Override
      protected boolean beyondLimit(ArrayColor i) {
        return i.vertexFloatCount + 6 + currentFloatCount >= ModelConstants.VERTEX_BUFFER_MAX_LENGTH;
      }

      @Override
      protected void combine(ArrayColor i, ModelColor o) {
        currentFloatCount += 6 + i.vertexFloatCount;
        currentCombinationList.add(i);
      }

      @Override
      protected void close(ModelColor o) {
        int[] vertexOffset = new int[currentCombinationList.size()];
        int[] vertexCount = new int[currentCombinationList.size()];
        float[] vertex = new float[currentFloatCount];
        float[] color = new float[currentFloatCount];
        float[] pick = new float[currentFloatCount];
        int currentVertexFloatPointer = 0;

        for(int d = 0; d!= currentCombinationList.size(); d++) {
          ArrayColor wall = currentCombinationList.get(d);
          int vertexCountSingle = wall.vertexCount;
          int vertexFloatCountSingle = wall.vertexFloatCount;
          float[] vertexSingle = wall.vertexArray;
          float[] colorSingle = wall.colorArray;
          float[] pickSingle = wall.pickArray;

          vertexOffset[d] = currentVertexFloatPointer;
          vertexCount[d] = vertexCountSingle +2;

          //dummy point
          for(int i=0; i!=3; i++) {
            vertex[currentVertexFloatPointer] = vertexSingle[i];
            color[currentVertexFloatPointer] = colorSingle[i];
            pick[currentVertexFloatPointer++] = pickSingle[i];
          }

          //wall
          System.arraycopy(vertexSingle, 0, vertex, currentVertexFloatPointer, vertexFloatCountSingle);
          System.arraycopy(colorSingle, 0, color, currentVertexFloatPointer, vertexFloatCountSingle);
          System.arraycopy(pickSingle, 0, pick, currentVertexFloatPointer, vertexFloatCountSingle);
          currentVertexFloatPointer += vertexFloatCountSingle;

          //dummy point
          for(int i=vertexSingle.length-3; i!=vertexSingle.length; i++) {
            vertex[currentVertexFloatPointer] = vertexSingle[i];
            color[currentVertexFloatPointer] = colorSingle[i];
            pick[currentVertexFloatPointer++] = pickSingle[i];
          }
        }

        FloatBuffer vertexBuffer = OpenglUtil.getFloatBuffer(vertex);
        FloatBuffer colorBuffer = OpenglUtil.getFloatBuffer(color);
        FloatBuffer pickBuffer = OpenglUtil.getFloatBuffer(pick);
        o.finish(currentVertexFloatPointer/3, vertexBuffer, colorBuffer, pickBuffer);

        for(int d = 0; d!= currentCombinationList.size(); d++) {
          FloatBuffer vb = vertexBuffer.duplicate(); vb.position(vertexOffset[d]);
          FloatBuffer cb = colorBuffer.duplicate(); cb.position(vertexOffset[d]);
          FloatBuffer pb = pickBuffer.duplicate(); cb.position(vertexOffset[d]);
          singleModelColors.get(singleModelColorPointer++).finish(vertexCount[d], vb, cb, pb);
        }
        currentCombinationList.clear();
      }
    }.run(arrayColors);
  }
}