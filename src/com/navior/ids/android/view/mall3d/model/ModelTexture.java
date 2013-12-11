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
import com.navior.ids.android.view.mall3d.mesh.MeshColorIndexed;
import com.navior.ids.android.view.mall3d.mesh.MeshTextureIndexed;
import com.navior.ids.android.view.mall3d.pipeline.PipelineName;
import com.navior.ids.android.view.mall3d.util.Holder;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Scanner;

public class ModelTexture extends Model {

  float[] vertices;
  float[] texcoord;
  short[] indices;

  private FloatBuffer verticesBuffer;
  private FloatBuffer texcoordBuffer;

  private int indicesNumber;
  private ShortBuffer indicesBuffer;

  private Holder<Integer> texture;

  public ModelTexture() {
  }

  public ModelTexture(int meshResourceId, int textureResourceId) {
    super();

    readMeshFile(meshResourceId);

    setBuffersTextureId(OpenglUtil.getTextureSetResourceId().load(textureResourceId), vertices, texcoord, indices);

//    vertices = null;
//    texcoord = null;
//    indices = null;
  }

  protected void readMeshFile(int meshFileNumber) {
    InputStream inputStream = OpenglRenderer.getInstance().getContext().getResources().openRawResource(meshFileNumber);
    Scanner input = new Scanner(inputStream);

    int verticesNumber = Integer.parseInt(input.next());
    vertices = new float[verticesNumber * 3];
    texcoord = new float[verticesNumber * 2];
    for(int i = 0; i != verticesNumber; i++) {
      vertices[i * 3] = Float.parseFloat(input.next());
      vertices[i * 3 + 1] = Float.parseFloat(input.next());
      vertices[i * 3 + 2] = Float.parseFloat(input.next());
      texcoord[i * 2] = Float.parseFloat(input.next());
      texcoord[i * 2 + 1] = Float.parseFloat(input.next()); // GL2ES1 1-y. GL10 y.
    }

    indicesNumber = Integer.parseInt(input.next());
    indices = new short[indicesNumber];
    for(int i = 0; i != indicesNumber; i++) {
      indices[i] = Short.parseShort(input.next());
    }

    input.close();
  }

  protected void setBuffersTextureId(Holder<Integer> textureId, float[] vertices, float[] texcoord, short[] indices) {
    //will change to string based resource management system
    verticesBuffer = OpenglUtil.getFloatBuffer(vertices);
    texcoordBuffer = OpenglUtil.getFloatBuffer(texcoord);
    indicesBuffer = OpenglUtil.getShortBuffer(indices);
    indicesNumber = indices.length;
    this.texture = textureId;
  }

  @Override
  public void draw(boolean selected) {
    if(texture==null || texture.get() == -1)
      return;
    if(verticesBuffer!=null)
      OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_TEXTURE, new MeshTextureIndexed(
          matrixWorld, selected? ModelConstants.SELECTED_ALPHA:ModelConstants.UNSELECTED_ALPHA, verticesBuffer, texcoordBuffer, indicesNumber, indicesBuffer, texture.get(), GLES20.GL_TRIANGLES
      ));
  }

  @Override
  public void pick() {
    if(texture==null || texture.get() == -1)
      return;
    if(verticesBuffer!=null)
      OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_COLOR, new MeshColorIndexed(
          matrixWorld, 1, verticesBuffer, getPickColor(), indicesNumber, indicesBuffer, GLES20.GL_TRIANGLES
      ));
  }
}
