package com.navior.ids.android.view.mall3d.pipeline;

import com.navior.ids.android.view.mall3d.pipelineComponent.PCAlpha;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCColor;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCMatrixMVP;

public class PipelineLine extends Pipeline {
  private static final String vertexShaderCode =
      "" +
          "uniform mat4 uMatrixMVP;" +
          "attribute vec4 aPosition;" +
          "void main() {" +
          "  gl_Position = uMatrixMVP * aPosition;" +
          "}";

  private static final String fragmentShaderCode =
      "" +
          "precision mediump float;" +
          "uniform vec3 uColor;" +
          "uniform float uAlpha;" +
          "void main() {" +
          "  gl_FragColor = vec4(uColor.r, uColor.g, uColor.b, uAlpha);" +
          "}";

  public PipelineLine() {
    addUniform(new PCMatrixMVP());
    addUniform(new PCColor());
    addUniform(new PCAlpha());
    super.init(vertexShaderCode, fragmentShaderCode);
  }
}
