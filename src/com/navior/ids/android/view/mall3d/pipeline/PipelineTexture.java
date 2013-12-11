package com.navior.ids.android.view.mall3d.pipeline;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.meshComponent.MCIndexBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCAlpha;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCPositionBuffer;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCTexture;

public class PipelineTexture extends Pipeline {
  private static final String vertexShaderCode =
      "" +
          "uniform mat4 uMVPMatrix;" +
          "attribute vec4 aPosition;" +
          "attribute vec2 aTexcoord;" +
          "varying vec2 vTexcoord;" +
          "void main() {" +
          "  gl_Position = uMVPMatrix * aPosition;" +
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

  @Override
  protected int essentialComponents() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.INDEXBUFFER
        | MeshComponent.TEXTURE;
  }

  public PipelineTexture() {
    super();

    new PCPositionBuffer(this);
    new PCTexture(this);
    new PCAlpha(this);

    super.init(vertexShaderCode, fragmentShaderCode);
  }

  @Override
  public void draw(Mesh mesh) {
    GLES20.glDrawElements(((MCPrimitiveType)mesh).getPrimitiveType(), ((MCIndexBuffer)mesh).getIndexCount(), GLES20.GL_UNSIGNED_SHORT, ((MCIndexBuffer)mesh).getIndexBuffer());
  }
}
