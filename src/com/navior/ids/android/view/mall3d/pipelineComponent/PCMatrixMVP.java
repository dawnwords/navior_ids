package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.meshComponent.Location;

public class PCMatrixMVP extends PipelineComponent {

  private int matrixMVPLocation;

  @Override
  public void init(int program) {
    matrixMVPLocation = GLES20.glGetUniformLocation(program, "uMatrixMVP");
  }

  @Override
  public void set() {
    Location.MATRIXMVP_ULOCATION = matrixMVPLocation;
  }
}
