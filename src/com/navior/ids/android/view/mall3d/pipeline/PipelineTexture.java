package com.navior.ids.android.view.mall3d.pipeline;

import com.navior.ids.android.view.mall3d.pipelineComponent.PCAlpha;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCMatrixMVP;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCTexture;

public class PipelineTexture extends Pipeline {
  private static final String vertexShaderCode =
      "" +
          "uniform mat4 uMatrixMVP;" +
          "attribute vec4 aPosition;" +
          "attribute vec2 aTexcoord;" +
          "varying vec2 vTexcoord;" +
          "void main() {" +
          "  gl_Position = uMatrixMVP * aPosition;" +
          "  vTexcoord = aTexcoord;" +
          "}";

  private static final String fragmentShaderCode =
      "" +
          "precision mediump float;" +
          "uniform sampler2D uTexture;" +
          "varying vec2 vTexcoord;" +
          "uniform float uAlpha;" +
          "void main() {" +
          "  vec4 tex = texture2D(uTexture, vTexcoord);" +
          "  gl_FragColor = vec4(tex.r,tex.g,tex.b, tex.a * uAlpha);" +
          "}";

  public PipelineTexture() {
    addUniform(new PCMatrixMVP());
    addUniform(new PCTexture());
    addUniform(new PCAlpha());
    super.init(vertexShaderCode, fragmentShaderCode);
  }
}
