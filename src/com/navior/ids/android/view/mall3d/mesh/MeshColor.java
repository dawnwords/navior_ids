package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColor;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MCVertexCount;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;

import java.nio.FloatBuffer;

public class MeshColor extends Mesh implements MCAlpha, MCPrimitiveType, MCPositionBuffer, MCColor, MCVertexCount {
  public int primitiveType;
  public float alpha;
  public float[] matrixWorld;
  public int vertexCount;
  public FloatBuffer positionBuffer;
  public float[] color;

  public MeshColor(float[] matrixWorld, float alpha, int vertexCount, FloatBuffer positionBuffer, float[] color, int primitiveType) {
    this.alpha = alpha;
    this.matrixWorld = matrixWorld;
    this.vertexCount = vertexCount;
    this.positionBuffer = positionBuffer;
    this.color = color;
    this.primitiveType = primitiveType;
  }

  public MeshColor(){}

  @Override
  public int components() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.VERTEXCOUNT
        | MeshComponent.COLOR;
  }

  @Override
  public float[] getColor() {
    return color;
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
