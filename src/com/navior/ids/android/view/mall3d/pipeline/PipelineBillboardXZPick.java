package com.navior.ids.android.view.mall3d.pipeline;

import com.navior.ids.android.view.mall3d.pipelineComponent.PCColor;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCMatrixMVP;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCSinCos;

public class PipelineBillboardXZPick extends Pipeline {
  private static final String vertexShaderCode =
      "" +
          "uniform mat4 uMatrixMVP;" +
          "uniform vec2 uSinCos;" +
          "uniform vec2 uSinCos1;" +
          "attribute vec4 aPosition;" +
          "attribute vec4 aPosition1;" +
          "void main() {" +
          "  vec4 diff = aPosition - aPosition1;" +
          "  float dy = diff.y;" +
          "  float ndy = diff.y * uSinCos1.y;" +
          "  float ady = abs(dy);" +
          "  float andy = abs(ndy);" +
          "  vec4 realPosition = vec4(" +
          "    -uSinCos.x * diff.z + aPosition1.x + uSinCos1.x * (-diff.y) * uSinCos.y," +
          "    aPosition.y - dy - ady + ndy + andy," +
          "    uSinCos.y * diff.z + aPosition1.z + uSinCos1.x * (-diff.y) * uSinCos.x," +
          "  1);" +
          "  gl_Position = uMatrixMVP * realPosition;" +
          "}";

  private static final String fragmentShaderCode =
      "" +
          "precision mediump float;" +
          "uniform vec3 uColor;" +
          "void main() {" +
          "  gl_FragColor = vec4(uColor.r,uColor.g,uColor.b,1);" +
          "}";

  public PipelineBillboardXZPick() {
    addUniform(new PCMatrixMVP());
    addUniform(new PCSinCos());
    addUniform(new PCColor());

    super.init(vertexShaderCode, fragmentShaderCode);
  }
}
