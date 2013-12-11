package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class MCColorBuffer implements MeshComponent {
  private FloatBuffer colorBuffer;

  public MCColorBuffer(FloatBuffer colorBuffer) {
    this.colorBuffer = colorBuffer;
  }

  @Override
  public void set() {
    GLES20.glVertexAttribPointer(Location.COLORBUFFER_ALOCATION, 3, GLES20.GL_FLOAT, false, 12, colorBuffer);
  }
}
