package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColorBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MCVertexCount;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;

import java.nio.FloatBuffer;

public class MeshColorBuffer extends Mesh implements MCAlpha, MCPrimitiveType, MCPositionBuffer, MCVertexCount, MCColorBuffer {
  public float alpha;
  public float[] matrixWorld;
  public int vertexCount;
  public FloatBuffer positionBuffer;
  public FloatBuffer colorBuffer;
  public int primitiveType;

  public MeshColorBuffer(float[] matrixWorld, float alpha, int vertexCount, FloatBuffer positionBuffer, FloatBuffer colorBuffer, int primitiveType) {
    this.alpha = alpha;
    this.matrixWorld = matrixWorld;
    this.vertexCount = vertexCount;
    this.positionBuffer = positionBuffer;
    this.colorBuffer = colorBuffer;
    this.primitiveType = primitiveType;
  }

  public MeshColorBuffer(){}

  @Override
  public int components() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.COLORBUFFER
        | MeshComponent.VERTEXCOUNT;
  }

  @Override
  public FloatBuffer getColorBuffer() {
    return colorBuffer;
  }

  @Override
  public float[] getMatrixWorld() {
    return matrixWorld;
  }

  @Override
  public FloatBuffer getPositionBuffer() {
    return positionBuffer;
  }

  @Override
  public int getPrimitiveType() {
    return primitiveType;
  }

  @Override
  public int getVertexCount() {
    return vertexCount;
  }

  @Override
  public float getAlpha() {
    return alpha;
  }
}
