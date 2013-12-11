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

public abstract class Model {

  public float[] matrixWorld = new float[16];

  public Model() {
    Matrix.setIdentityM(matrixWorld, 0);
  }

  public abstract void draw(boolean selected);

  public abstract void pick();

  //HashMap<Integer, Model>. from color to mesh.
  private static SparseArray<Model> colorMesh = new SparseArray<Model>();
  private static int color[] = new int[3];

  //start with black.
  public static void pickDrawStart() {
    color[0] = 0;
    color[1] = 0;
    color[2] = 0;
  }

  //clear hash map for GC & next frame.
  public static void pickDrawEnd() {
    colorMesh.clear();
  }

  private static Model nextModel;
  protected void modelPick() {
    nextModel = this;
  }
  protected static float[] getPickColor() {
    //generate current color and save.
    float[] result = new float[]{(float) color[0] / 255.0f, (float) color[1] / 255.0f, (float) color[2] / 255.0f, 1};
    int index = (color[2] << 16) | (color[1] << 8) | (color[0]);
    colorMesh.put(index, nextModel);

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

    return result;
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
