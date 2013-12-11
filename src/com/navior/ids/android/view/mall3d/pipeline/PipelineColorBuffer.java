package com.navior.ids.android.view.mall3d.pipeline;

import com.navior.ids.android.view.mall3d.pipelineComponent.PCAlpha;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCMatrixMVP;

public class PipelineColorBuffer extends Pipeline {
  private static final String vertexShaderCode =
      "" +
          "uniform mat4 uMatrixMVP;" +
          "attribute vec4 aPosition;" +
          "attribute vec3 aColor;" +
          "varying vec3 vColor;" +
          "void main() {" +
          "  gl_Position = uMatrixMVP * aPosition;" +
          "  vColor = aColor;" +
          "}";

  private static final String fragmentShaderCode =
      "" +
          "precision mediump float;" +
          "varying vec3 vColor;" +
          "uniform float uAlpha;" +
          "void main() {" +
          "  gl_FragColor = vec4(vColor.r,vColor.g,vColor.b,uAlpha);" +
          "}";

  public PipelineColorBuffer() {
    addUniform(new PCMatrixMVP());
    addUniform(new PCAlpha());
    super.init(vertexShaderCode, fragmentShaderCode);
  }
}
