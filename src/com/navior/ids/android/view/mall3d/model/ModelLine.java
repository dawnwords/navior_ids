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
import com.navior.ids.android.view.mall3d.mesh.MeshLine;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;
import com.navior.ids.android.view.mall3d.util.Combiner;
import com.navior.ids.android.view.mall3d.util.Holder;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLine extends Model {

  private int vertexCount;

  private int primitiveType = GLES20.GL_LINES;
  public void setPrimitiveType(int primitiveType) { this.primitiveType = primitiveType; }

  private FloatBuffer vertexBuffer;
  private float[] color;
  private float width;

  public ModelLine() {
    super();
  }

  public void finish(ArrayLine arrayLine) {
    FloatBuffer vertexBuffer = OpenglUtil.getFloatBuffer(arrayLine.vertexArray);

    this.finish(arrayLine.vertexCount, vertexBuffer, arrayLine.color, arrayLine.width);
  }

  public void finish(int vertexCount, FloatBuffer vertexBuffer, float[] color, float width) {
    this.vertexCount = vertexCount;
    this.vertexBuffer = vertexBuffer;
    this.color = color;
    this.width = width;

    setPasses();
  }

  protected void setPasses() {
    setPass(Pass.PASS_DRAW, Pipeline.PIPELINE_LINESTRIP, new MeshLine(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, vertexBuffer, color, new Holder<Float>(width), vertexCount, primitiveType
    ));
  }


  public static List<ModelLine> combine(List<ArrayLine> arrayLines, final List<ModelLine> singleModelLines) {
    return new Combiner<ArrayLine, ModelLine>() {
      ArrayList<ArrayLine> currentList = new ArrayList<ArrayLine>();
      int currentVertexFloatCount;
      private int singleModelLinePointer = 0;

      @Override
      protected ModelLine open() {
        currentVertexFloatCount = 0;
        return new ModelLine();
      }

      @Override
      protected boolean beyondLimit(ArrayLine i) {
        return i.vertexFloatCount + currentVertexFloatCount >= ModelConstants.VERTEX_BUFFER_MAX_LENGTH;
      }

      @Override
      protected void combine(ArrayLine i, ModelLine o) {
        currentVertexFloatCount += i.vertexFloatCount;
        currentList.add(i);
      }

      @Override
      protected void close(ModelLine o) {
        int[] vertexOffset = new int[currentList.size()];
        int[] vertexCount = new int[currentList.size()];
        float[] vertex = new float[currentVertexFloatCount];
        int currentVertexFloatPointer = 0;

        for(int d = 0; d!= currentList.size(); d++) {
          ArrayLine edge = currentList.get(d);
          float[] vertexSingle = edge.vertexArray;
          int vertexFloatCountSingle = edge.vertexFloatCount;

          vertexOffset[d] = currentVertexFloatPointer;
          vertexCount[d] = edge.vertexCount;

          System.arraycopy(vertexSingle, 0, vertex, currentVertexFloatPointer, vertexFloatCountSingle);
          currentVertexFloatPointer += vertexFloatCountSingle;
        }

        FloatBuffer vertexBuffer = OpenglUtil.getFloatBuffer(vertex);
        o.finish(currentVertexFloatCount / 3, vertexBuffer, ModelConstants.EDGE_MATERIAL, ModelConstants.EDGE_WIDTH);

        for(int d = 0; d!= currentList.size(); d++) {
          FloatBuffer vb = vertexBuffer.duplicate(); vb.position(vertexOffset[d]);
          singleModelLines.get(singleModelLinePointer++).finish(vertexCount[d], vb, ModelConstants.EDGE_MATERIAL, ModelConstants.EDGE_WIDTH);
        }
        currentList.clear();
      }
    }.run(arrayLines);
  }
}
