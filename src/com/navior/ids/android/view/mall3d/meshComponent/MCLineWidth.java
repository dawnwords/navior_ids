package com.navior.ids.android.view.mall3d.meshComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.util.Holder;

public class MCLineWidth implements MeshComponent {
  private Holder<Float> lineWidth;

  public MCLineWidth(Holder<Float> lineWidth) {
    this.lineWidth = lineWidth;
  }

  @Override
  public void set() {
    GLES20.glLineWidth(lineWidth.get());
  }
}
