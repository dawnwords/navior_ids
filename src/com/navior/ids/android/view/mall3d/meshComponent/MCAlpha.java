package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.util.Holder;

public class MCAlpha implements MeshComponent {
  private Holder<Float> alpha;

  public MCAlpha(Holder<Float> alpha) {
    this.alpha = alpha;
  }

  @Override
  public void set() {
    GLES20.glUniform1f(Location.ALPHA_ULOCATION, alpha.get());
  }
}
