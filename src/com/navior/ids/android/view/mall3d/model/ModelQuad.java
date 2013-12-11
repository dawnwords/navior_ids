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
  public static final float[] VERTEX_QUAD = new float[]{-0.5f, 0, -0.5f, 0.5f, 0, -0.5f, -0.5f, 0, 0.5f, 0.5f, 0, 0.5f};
  public static final float[] VERTEX_BILLBOARD = new float[]{0, 1, 0.5f,  0, 1, -0.5f,  0, 0, 0.5f,  0, 0, -0.5f};
  public static final short[] INDEX = new short[]{0, 1, 2, 1, 3, 2};

  public ModelQuad(String fileName, float x, float y, float z, float sx, float sz) {
    super();
    setBuffersTextureId(
        OpenglUtil.getTextureSetResourceName().load(fileName),
        ArrayQuad.getPosition(x, y, z, sx, sz),
        TEXCOORD,
        INDEX
    );
  }

  public ModelQuad(int resourceId, float x, float y, float z, float sx, float sz) {
    super();
    setBuffersTextureId(
        OpenglUtil.getTextureSetResourceId().load(resourceId),
        ArrayQuad.getPosition(x, y, z, sx, sz),
        TEXCOORD,
        INDEX
    );
  }

  public ModelQuad(Holder<Integer> textureId, float left, float top, float right, float bottom, float x, float y, float z, float sx, float sz) {
    super();
    setBuffersTextureId(
        textureId,
        ArrayQuad.getPosition(x, y, z, sx, sz),
        new float[]{left, 1 - top, right, 1 - top, left, 1 - bottom, right, 1 - bottom},
        INDEX
    );
  }

  public ModelQuad(ArrayQuad arrayQuad) {
    super();
    setBuffersTextureId(arrayQuad.texture, arrayQuad.vertexArray, arrayQuad.texcoordArray, arrayQuad.indexArray);
  }
}
