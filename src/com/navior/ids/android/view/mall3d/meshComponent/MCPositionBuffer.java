package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class MCPositionBuffer implements MeshComponent {
  private int index;
  private FloatBuffer positionBuffer;

  public MCPositionBuffer(FloatBuffer positionBuffer) {
    this.positionBuffer = positionBuffer;
    this.index = 0;
  }
  public MCPositionBuffer(FloatBuffer positionBuffer, int index) {
    this.positionBuffer = positionBuffer;
    this.index = index;
  }

  @Override
  public void set() {
    GLES20.glVertexAttribPointer(Location.POSITIONBUFFER_ALOCATIONS[index], 3, GLES20.GL_FLOAT, false, 12, positionBuffer);
  }
}
