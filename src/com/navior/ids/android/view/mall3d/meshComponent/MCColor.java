package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;

public class MCColor implements MeshComponent {
  private float[] color;

  public MCColor(float[] color) {
    this.color = color;
  }

  @Override
  public void set() {
    GLES20.glUniform3fv(Location.COLOR_ULOCATION, 1, color, 0);
  }
}
