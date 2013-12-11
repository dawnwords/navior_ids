package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.meshComponent.MCColorBuffer;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;

public class PCColorBuffer extends PipelineComponent {

  public PCColorBuffer(Pipeline pipeline) {
    super(pipeline);
  }

  private int colorHandle;

  @Override
  public void init(int program) {
    colorHandle = GLES20.glGetAttribLocation(program, "aColor");
  }

  @Override
  public void begin() {
    GLES20.glEnableVertexAttribArray(colorHandle);
  }

  @Override
  public void set(Mesh m) {
    MCColorBuffer mesh = (MCColorBuffer)m;
    GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT, false, 12, mesh.getColorBuffer());
  }

  @Override
  public void end() {
    GLES20.glDisableVertexAttribArray(colorHandle);
  }
}
