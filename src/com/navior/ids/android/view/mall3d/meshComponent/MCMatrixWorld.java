package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.navior.ids.android.view.mall3d.OpenglRenderer;

public class MCMatrixWorld implements MeshComponent {
  private float[] matrixWorld;

  public MCMatrixWorld(float[] matrixWorld) {
    this.matrixWorld = matrixWorld;
  }

  private static float[] matrixWVP = new float[16];
  @Override
  public void set() {
    Matrix.multiplyMM(matrixWVP, 0, OpenglRenderer.getInstance().getCamera().getMatrixVP(), 0, matrixWorld, 0);
    GLES20.glUniformMatrix4fv(Location.MATRIXMVP_ULOCATION, 1, false, matrixWVP, 0);
  }
}
