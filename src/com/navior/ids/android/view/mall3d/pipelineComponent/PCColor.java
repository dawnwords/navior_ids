package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.meshComponent.Location;

public class PCColor extends PipelineComponent {

  private int colorLocation;

  @Override
  public void init(int program) {
    colorLocation = GLES20.glGetUniformLocation(program, "uColor");
  }

  @Override
  public void set() {
    Location.COLOR_ULOCATION = colorLocation;
  }
}
