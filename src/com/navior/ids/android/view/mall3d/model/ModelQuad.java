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

import com.navior.ids.android.view.mall3d.util.Holder;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

public class ModelQuad extends ModelTexture {

  public static final float[] TEXCOORD = new float[]{0, 0, 1, 0, 0, 1, 1, 1};
  public static final float[] VERTICES = new float[]{-0.5f, 0, -0.5f, 0.5f, 0, -0.5f, -0.5f, 0, 0.5f, 0.5f, 0, 0.5f};
  public static final short[] INDICES = new short[]{0, 1, 2, 1, 3, 2};

  public ModelQuad(String fileName, float x, float y, float z, float sx, float sz) {
    super();
    setBuffersTextureId(
        OpenglUtil.getTextureSetResourceName().load(fileName),
        getPosition(x, y, z, sx, sz),
        TEXCOORD,
        INDICES
    );
  }

  public ModelQuad(int resourceId, float x, float y, float z, float sx, float sz) {
    super();
    setBuffersTextureId(
        OpenglUtil.getTextureSetResourceId().load(resourceId),
        getPosition(x, y, z, sx, sz),
        TEXCOORD,
        INDICES
    );
  }

  private float[] getPosition(float px, float py, float pz, float sx, float sz) {
    float[] result = new float[12];
    for(int i = 0; i != 12; i += 3) {
      float x = VERTICES[i], y = VERTICES[i + 1], z = VERTICES[i + 2];
      result[i] = px + x * sx;
      result[i + 1] = py + y;
      result[i + 2] = pz + z * sz;
    }
    return result;
  }

  public ModelQuad(Holder<Integer> textureId, float left, float top, float right, float bottom, float x, float y, float z, float sx, float sz) {
    super();
    setBuffersTextureId(
        textureId,
        getPosition(x, y, z, sx, sz),
        new float[]{left, 1 - top, right, 1 - top, left, 1 - bottom, right, 1 - bottom},
        INDICES
    );
  }
}
