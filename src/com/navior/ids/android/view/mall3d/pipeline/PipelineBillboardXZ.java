package com.navior.ids.android.view.mall3d.pipeline;

import com.navior.ids.android.view.mall3d.pipelineComponent.PCAlpha;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCMatrixMVP;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCSinCos;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCTexture;

public class PipelineBillboardXZ extends Pipeline {
  private static final String vertexShaderCode =
      "" +
          "uniform mat4 uMatrixMVP;" +
          "uniform vec2 uSinCos;" +
          "uniform vec2 uSinCos1;" +
          "attribute vec4 aPosition;" +
          "attribute vec4 aPosition1;" +
          "attribute vec2 aTexcoord;" +
          "varying vec2 vTexcoord;" +
          "void main() {" +
          "  vec4 diff = aPosition - aPosition1;" +
          "  float dy = diff.y;" +
          "  float ndy = diff.y * uSinCos1.y;" +
          "  float ady = abs(dy);" + //dy + ady = 旧高度(高点)/0(低点)
          "  float andy = abs(ndy);" + //ndy + andy = 新高度(高点)/0(低点)
          "  vec4 realPosition = vec4(" +
          "    -uSinCos.x * diff.z + aPosition1.x + uSinCos1.x * (-diff.y) * uSinCos.y," + //前两部分是绕中心旋转，最后一部分是倒下的偏移，高低点向相反方向
          "    aPosition.y - dy - ady + ndy + andy," + //前三项获得低点的高度(无论该点本身是低点还是高点) 后面加上2ndy达到高点的高度或两者抵消仍然是低点的高度
          "    uSinCos.y * diff.z + aPosition1.z + uSinCos1.x * (-diff.y) * uSinCos.x," +
          "  1);" +
          "  gl_Position = uMatrixMVP * realPosition;" +
          "  vTexcoord = aTexcoord;" +
          "}";

  private static final String fragmentShaderCode =
      "" +
          "precision mediump float;" +
          "uniform sampler2D uTexture;" +
          "uniform float uAlpha;" +
          "varying vec2 vTexcoord;" +
          "void main() {" +
          "  vec4 tex = texture2D(uTexture, vTexcoord);" +
          "  gl_FragColor = vec4(tex.xyz, tex.w * uAlpha);" +
          "}";

  public PipelineBillboardXZ() {
    addUniform(new PCMatrixMVP());
    addUniform(new PCTexture());
    addUniform(new PCAlpha());
    addUniform(new PCSinCos());

    super.init(vertexShaderCode, fragmentShaderCode);
  }
}
