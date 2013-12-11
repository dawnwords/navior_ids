package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.meshComponent.MCColor;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;

public class PCColor extends PipelineComponent {

  public PCColor(Pipeline pipeline) {
    super(pipeline);
  }

  private int colorHandle;

  @Override
  public void init(int program) {
    colorHandle = GLES20.glGetUniformLocation(program, "uColor");
  }

  @Override
  public void begin() {
  }

  @Override
  public void set(Mesh m) {
    MCColor mesh = (MCColor)m;
    GLES20.glUniform3fv(colorHandle, 1, mesh.getColor(), 0);
  }

  @Override
  public void end() {
  }
}
