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
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelColorIndexed extends Model {

  protected int vertexCount;
  protected FloatBuffer vertexBuffer;
  protected FloatBuffer colorBuffer;
  protected FloatBuffer pickBuffer;

  protected int indexCount;
  protected ShortBuffer indexBuffer;


  public ModelColorIndexed() {
    super();
  }

  public void finish(ArrayColorIndexed arrayColorIndexed) {
    FloatBuffer positionBuffer = OpenglUtil.getFloatBuffer(arrayColorIndexed.vertexArray);
    FloatBuffer colorBuffer = OpenglUtil.getFloatBuffer(arrayColorIndexed.colorArray);
    FloatBuffer pickBuffer = OpenglUtil.getFloatBuffer(arrayColorIndexed.pickArray);
    ShortBuffer indexBuffer = OpenglUtil.getShortBuffer(arrayColorIndexed.indexArray);

    finish(arrayColorIndexed.vertexCount, positionBuffer, colorBuffer, pickBuffer, arrayColorIndexed.indexCount, indexBuffer);
  }

  public void finish(int vertexCount, FloatBuffer vertexBuffer, FloatBuffer colorBuffer, FloatBuffer pickBuffer, int indexCount, ShortBuffer indexBuffer) {
    this.vertexCount = vertexCount;
    this.vertexBuffer = vertexBuffer;
    this.colorBuffer = colorBuffer;
    this.pickBuffer = pickBuffer;
    this.indexCount = indexCount;
    this.indexBuffer = indexBuffer;

    setPasses();
  }


  protected void setPasses() {
    setPass(Pass.PASS_DRAW, Pipeline.PIPELINE_COLORBUFFER, new MeshColorBuffer(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, vertexBuffer, colorBuffer, indexBuffer, indexCount, GLES20.GL_TRIANGLES
    ));

    setPass(Pass.PASS_PICK, Pipeline.PIPELINE_COLORBUFFER, new MeshColorBuffer(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, vertexBuffer, pickBuffer, indexBuffer, indexCount, GLES20.GL_TRIANGLES
    ));
  }

  public static List<ModelColorIndexed> combine(List<ArrayColorIndexed> arrayColorIndexeds, final List<ModelColorIndexed> singleModelColorIndexeds) {
    return new Combiner<ArrayColorIndexed, ModelColorIndexed>() {
      private ArrayList<ArrayColorIndexed> currentList = new ArrayList<ArrayColorIndexed>();
      private int currentVertexFloatCount;
      private int currentIndexCount;
      private int singleModelColorIndexedPointer = 0;

      @Override
      protected ModelColorIndexed open() {
        currentVertexFloatCount = 0;
        currentIndexCount = 0;
        return new ModelColorIndexed();
      }

      @Override
      protected boolean beyondLimit(ArrayColorIndexed i) {
        boolean vertexLimit = i.vertexFloatCount + currentVertexFloatCount >= ModelConstants.VERTEX_BUFFER_MAX_LENGTH;
        boolean indexLimit = i.indexCount + currentIndexCount >= ModelConstants.INDEX_BUFFER_MAX_LENGTH;
        return vertexLimit || indexLimit;
      }

      @Override
      protected void combine(ArrayColorIndexed i, ModelColorIndexed o) {
        currentVertexFloatCount += i.vertexFloatCount;
        currentIndexCount += i.indexCount;
        currentList.add(i);
      }

      @Override
      protected void close(ModelColorIndexed o) {
        int[] vertexOffset = new int[currentList.size()];
        int[] vertexCount = new int[currentList.size()];
        int[] indexOffset = new int[currentList.size()];
        int[] indexCount = new int[currentList.size()];
        float[] vertex = new float[currentVertexFloatCount];
        float[] color = new float[currentVertexFloatCount];
        float[] pick = new float[currentVertexFloatCount];
        short[] index = new short[currentIndexCount];
        int currentFloatPointer = 0;
        int currentIndexPointer = 0;

        for(int d = 0; d != currentList.size(); d++) {
          ArrayColorIndexed roof = currentList.get(d);
          int vertexCountSingle = roof.vertexCount;
          int vertexFloatCountSingle = roof.vertexFloatCount;
          int indexCountSingle = roof.indexCount;
          float[] vertexSingle = roof.vertexArray;
          float[] colorSingle = roof.colorArray;
          float[] pickSingle = roof.pickArray;
          short[] indexSingle = roof.indexArray;

          vertexOffset[d] = currentFloatPointer;
          vertexCount[d] = vertexCountSingle;
          indexOffset[d] = currentIndexPointer;
          indexCount[d] = roof.indexCount;

          System.arraycopy(vertexSingle, 0, vertex, currentFloatPointer, vertexFloatCountSingle);
          System.arraycopy(colorSingle, 0, color, currentFloatPointer, vertexFloatCountSingle);
          System.arraycopy(pickSingle, 0, pick, currentFloatPointer, vertexFloatCountSingle);
          currentFloatPointer += vertexFloatCountSingle;

          for(int i = 0; i != indexCountSingle; i++) {
            index[currentIndexPointer++] = (short) (indexSingle[i] + vertexOffset[d] / 3);
          }
        }

        FloatBuffer positionBuffer = OpenglUtil.getFloatBuffer(vertex);
        FloatBuffer colorBuffer = OpenglUtil.getFloatBuffer(color);
        FloatBuffer pickBuffer = OpenglUtil.getFloatBuffer(pick);
        ShortBuffer indexBuffer = OpenglUtil.getShortBuffer(index);
        o.finish(currentFloatPointer/3, positionBuffer, colorBuffer, pickBuffer, currentIndexPointer, indexBuffer);

        for(int d = 0; d != currentList.size(); d++) {
          FloatBuffer vb = positionBuffer.duplicate();
          FloatBuffer cb = colorBuffer.duplicate();
          FloatBuffer pb = pickBuffer.duplicate();
          ShortBuffer ib = indexBuffer.duplicate();
          ib.position(indexOffset[d]);
          singleModelColorIndexeds.get(singleModelColorIndexedPointer++).finish(vertexCount[d], vb, cb, pb, indexCount[d], ib);
        }
        currentList.clear();
      }
    }.run(arrayColorIndexeds);
  }
}
