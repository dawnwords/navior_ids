package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.meshComponent.Location;

public class PCAlpha extends PipelineComponent {

  private int alphaLocation;

  @Override
  public void init(int program) {
    alphaLocation = GLES20.glGetUniformLocation(program, "uAlpha");
  }

  @Override
  public void set() {
    Location.ALPHA_ULOCATION = alphaLocation;
  }
}
