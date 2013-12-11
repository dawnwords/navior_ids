package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.meshComponent.MCTexture;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;

public class PCTexture extends PipelineComponent {

  public PCTexture(Pipeline pipeline) {
    super(pipeline);
  }

  private int texcoordHandle;
  private int textureHandle;

  @Override
  public void init(int program) {
    texcoordHandle = GLES20.glGetAttribLocation(program, "aTexcoord");
    textureHandle = GLES20.glGetUniformLocation(program, "uTexture");
  }

  @Override
  public void begin() {
    GLES20.glEnableVertexAttribArray(texcoordHandle);
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
  }

  @Override
  public void set(Mesh m) {
    MCTexture mesh = (MCTexture)m;
    GLES20.glVertexAttribPointer(texcoordHandle, 3, GLES20.GL_FLOAT, false, 8, mesh.getTexcoordBuffer());
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mesh.getTexture()); //TODO optimize
    GLES20.glUniform1i(textureHandle, 0);
  }

  @Override
  public void end() {
    GLES20.glDisableVertexAttribArray(texcoordHandle);
  }
}
