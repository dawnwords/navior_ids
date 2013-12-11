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
 * @date 13-7-12
 */

package com.navior.ids.android.view.mall3d.model;

import android.opengl.Matrix;
import android.util.SparseArray;

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.pass.Pass;

public abstract class Model {
  protected float[] matrixWorld = new float[16];
  public Model() { Matrix.setIdentityM(matrixWorld, 0); }

  protected Mesh[] meshs = new Mesh[Pass.COUNT];
  protected int[] pipelines = new int[Pass.COUNT];

  public final void draw(int pass) {
    Mesh mesh = meshs[pass];
    int pipeline = pipelines[pass];
    if(mesh!=null) {
      OpenglRenderer.getInstance().addMesh(mesh, pipeline);
    }
  }

  protected void setPass(int pass, int pipeline, Mesh mesh) {
    meshs[pass] = mesh;
    pipelines[pass] = pipeline;
  }

  //HashMap<Integer, Model>. from color to mesh.
  private static SparseArray<Model> colorMesh = new SparseArray<Model>();
  private static int color[] = new int[3];
  private static float fColor[];

  //start with black.
  public static void pickDrawStart() {
    colorMesh.clear();
    color[0] = 0;
    color[1] = 0;
    color[2] = 0;
  }

  protected void modelPick() {
    int index = (color[2] << 16) | (color[1] << 8) | (color[0]);
    fColor = new float[]{(float) color[0] / 255.0f, (float) color[1] / 255.0f, (float) color[2] / 255.0f, 1};
    colorMesh.put(index, this);

    //find the next color
    color[0] += 16;
    if(color[0] > 255) {
      color[0] = 0;
      color[1] += 16;
      if(color[1] > 255) {
        color[1] = 0;
        color[2] += 16;
      }
    }
    if(color[2] > 255)
      System.out.println("Renderer ModelPick out of range");
  }
  protected static float[] getPickColor() {
    return fColor;
  }

  //hash map look up.
  public static Model pickMesh(int[] color) {
    //color correction because of 16-bit(5+6+5) frame buffer.
    color[0] = (color[0] + 8) / 16 * 16;
    if(color[0] > 255) color[0] = 255;
    color[1] = (color[1] + 8) / 16 * 16;
    if(color[1] > 255) color[1] = 255;
    color[2] = (color[2] + 8) / 16 * 16;
    if(color[2] > 255) color[2] = 255;

    int index = 0;
    index |= (color[2] << 16) | (color[1] << 8) | (color[0]);

    if(index < 0)
      return null;

    return colorMesh.get(index);
  }
}
