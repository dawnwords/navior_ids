package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class MCTexcoordBuffer implements MeshComponent {
  private int index;
  private FloatBuffer texcoordBuffer;

  public MCTexcoordBuffer(FloatBuffer texcoordBuffer) {
    this.texcoordBuffer = texcoordBuffer;
    this.index = 0;
  }
  public MCTexcoordBuffer(FloatBuffer texcoordBuffer, int index) {
    this.texcoordBuffer = texcoordBuffer;
    this.index = index;
  }

  @Override
  public void set() {
    GLES20.glVertexAttribPointer(Location.TEXCOORDBUFFER_ALOCATIONS[index], 2, GLES20.GL_FLOAT, false, 8, texcoordBuffer);
  }
}
