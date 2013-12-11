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
import com.navior.ids.android.view.mall3d.mesh.MeshColor;
import com.navior.ids.android.view.mall3d.mesh.MeshTexture;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;
import com.navior.ids.android.view.mall3d.util.Holder;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ModelTexture extends Model {

  protected FloatBuffer verticesBuffer;
  protected FloatBuffer texcoordBuffer;

  protected int indexCount;
  protected ShortBuffer indexBuffer;

  protected Holder<Integer> texture;

  public ModelTexture() {
  }

  protected void setBuffersTextureId(Holder<Integer> textureId, float[] vertex, float[] texcoord, short[] index) {
    verticesBuffer = OpenglUtil.getFloatBuffer(vertex);
    texcoordBuffer = OpenglUtil.getFloatBuffer(texcoord);
    indexBuffer = OpenglUtil.getShortBuffer(index);
    indexCount = index.length;
    this.texture = textureId;
    setPasses();
  }

  protected float[] pickColor; public void setPickColor(float[] pickColor) { this.pickColor = pickColor; }
  public void setPasses() {
    setPass(Pass.PASS_DRAW, Pipeline.PIPELINE_TEXTURE, new MeshTexture(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, verticesBuffer, texcoordBuffer, texture, indexBuffer, indexCount, GLES20.GL_TRIANGLES
    ));

    setPass(Pass.PASS_PICK, Pipeline.PIPELINE_COLOR, new MeshColor(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, verticesBuffer, pickColor, indexBuffer, indexCount, GLES20.GL_TRIANGLES
    ));
  }
}
