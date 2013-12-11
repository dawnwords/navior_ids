package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;

public class PCAlpha extends PipelineComponent {

  public PCAlpha(Pipeline pipeline) {
    super(pipeline);
  }

  private int alphaHandle;

  @Override
  public void init(int program) {
    alphaHandle = GLES20.glGetUniformLocation(program, "uAlpha");
  }

  @Override
  public void begin() {
  }

  @Override
  public void set(Mesh m) {
    MCAlpha mesh = (MCAlpha)m;
    GLES20.glUniform1f(alphaHandle, mesh.getAlpha());
  }

  @Override
  public void end() {
  }
}
