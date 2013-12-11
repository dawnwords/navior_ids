package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.meshComponent.Location;

public class PCTexture extends PipelineComponent {

  private int texture0Location;
  private int texture1Location;

  @Override
  public void init(int program) {
    texture0Location = GLES20.glGetUniformLocation(program, "uTexture");
    texture1Location = GLES20.glGetUniformLocation(program, "uTexture1");
  }

  @Override
  public void set() {
    Location.TEXTURE_ULOCATIONS[0] = texture0Location;
    Location.TEXTURE_ULOCATIONS[1] = texture1Location;
  }
}
