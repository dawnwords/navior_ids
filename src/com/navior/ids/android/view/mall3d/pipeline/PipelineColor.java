package com.navior.ids.android.view.mall3d.pipeline;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.meshComponent.MCIndexBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MCVertexCount;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCAlpha;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCColor;
import com.navior.ids.android.view.mall3d.pipelineComponent.PCPositionBuffer;

public class PipelineColor extends Pipeline {
  private static final String vertexShaderCode =
      "" +
          "uniform mat4 uMVPMatrix;" +
          "attribute vec4 aPosition;" +
          "void main() {" +
          "  gl_Position = uMVPMatrix * aPosition;" +
          "}";

  private static final String fragmentShaderCode =
      "" +
          "precision mediump float;" +
          "uniform vec3 uColor;" +
          "uniform float uAlpha;" +
          "void main() {" +
          "  gl_FragColor = vec4(uColor.r,uColor.g,uColor.b,uAlpha);" +
          "}";

  @Override
  protected int essentialComponents() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.COLOR;
  }

  public PipelineColor() {
    super();

    new PCPositionBuffer(this);
    new PCColor(this);
    new PCAlpha(this);

    super.init(vertexShaderCode, fragmentShaderCode);
  }

  @Override
  public void draw(Mesh mesh) {
    if((mesh.components() & MeshComponent.INDEXBUFFER) > 0)
      GLES20.glDrawElements(((MCPrimitiveType)mesh).getPrimitiveType(), ((MCIndexBuffer)mesh).getIndexCount(), GLES20.GL_UNSIGNED_SHORT, ((MCIndexBuffer)mesh).getIndexBuffer());
    else
      GLES20.glDrawArrays(((MCPrimitiveType)mesh).getPrimitiveType(), 0, ((MCVertexCount)mesh).getVertexCount());
  }
}
