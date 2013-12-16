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
import com.navior.ids.android.view.mall3d.mesh.MeshBillBoardXZ;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.nio.FloatBuffer;

public class ModelBillboardXZ extends ModelTexture {

  public static final float[] TEXCOORD = new float[]{0, 0, 1, 0, 0, 1, 1, 1};
  public static final float[] VERTEX_BILLBOARD = new float[]{0, 1, 0.5f,  0, 1, -0.5f,  0, 0, 0.5f,  0, 0, -0.5f};
  public static final short[] INDEX = new short[]{0, 1, 2, 1, 3, 2};

  private FloatBuffer centerBuffer;

  public ModelBillboardXZ(String fileName, float x, float y, float z, float scale) {
    super();

    float cy = y + scale/2;
    centerBuffer = OpenglUtil.getFloatBuffer(new float[] {
        x,cy,z,
        x,cy,z,
        x,cy,z,
        x,cy,z
    });

    setBuffersTextureId(
        OpenglUtil.getTextureSetResourceName().load(fileName),
        getPosition(x, y, z, scale),
        TEXCOORD,
        INDEX
    );
  }

  private float[] getPosition(float px, float py, float pz, float scale) {
    float[] result = new float[12];
    for(int i = 0; i != 12; i += 3) {
      float x = VERTEX_BILLBOARD[i], y = VERTEX_BILLBOARD[i + 1], z = VERTEX_BILLBOARD[i + 2];
      result[i  ] = px + x * scale;
      result[i+1] = py + y * scale;
      result[i+2] = pz + z * scale;
    }
    return result;
  }

  @Override
  public void setPasses() {
    MeshBillBoardXZ mesh = new MeshBillBoardXZ(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, verticesBuffer, centerBuffer, texcoordBuffer, texture, pickColor, indexBuffer, indexCount, GLES20.GL_TRIANGLES
    );

    setPass(Pass.PASS_DRAW, Pipeline.PIPELINE_BILLBOARDXZ, mesh);
    setPass(Pass.PASS_PICK, Pipeline.PIPELINE_BILLBOARDXZ_PICK, mesh);
  }
}
