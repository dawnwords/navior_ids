package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.util.Holder;

public class MCTexture implements MeshComponent {
  private Holder<Integer> texture;
  private int index;

  public MCTexture(Holder<Integer> texture) {
    this.texture = texture;
    this.index = 0;
  }
  public MCTexture(Holder<Integer> texture, int index) {
    this.texture = texture;
    this.index = index;
  }

  @Override
  public void set() {
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get());
    GLES20.glUniform1i(Location.TEXTURE_ULOCATIONS[index], 0);
  }
}
