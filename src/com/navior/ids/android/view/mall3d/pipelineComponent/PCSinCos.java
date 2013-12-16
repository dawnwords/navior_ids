package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.meshComponent.Location;

public class PCSinCos extends PipelineComponent {

  private int sincosLocation;
  private int sincos1Location;

  @Override
  public void init(int program) {
    sincosLocation = GLES20.glGetUniformLocation(program, "uSinCos");
    sincos1Location = GLES20.glGetUniformLocation(program, "uSinCos1");
  }

  @Override
  public void set() {
    float alpha = OpenglRenderer.getInstance().getCamera().getAlpha();
    float beta = OpenglRenderer.getInstance().getBillboardBeta();
    float sinAlpha = (float) Math.sin(alpha);
    float cosAlpha = (float) Math.cos(alpha);
    float sinBeta = (float) Math.sin(beta);
    float cosBeta = (float) Math.cos(beta);
    GLES20.glUniform2f(sincosLocation, sinAlpha, cosAlpha);
    GLES20.glUniform2f(sincos1Location, sinBeta, cosBeta);
  }
}
