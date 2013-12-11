package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColor;
import com.navior.ids.android.view.mall3d.meshComponent.MCLineWidth;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MCVertexCount;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;

import java.nio.FloatBuffer;

public class MeshLineStrip extends Mesh implements MCAlpha, MCPrimitiveType, MCVertexCount, MCPositionBuffer, MCColor, MCLineWidth {
  public float alpha;
  public float[] matrixWorld;
  public int vertexCount;
  public FloatBuffer positionBuffer;
  public float[] color;
  public float width;
  public int primitiveType;

  public MeshLineStrip(float[] matrixWorld, float alpha, int vertexCount, FloatBuffer positionBuffer, float[] color, float width, int primitiveType) {
    this.alpha = alpha;
    this.matrixWorld = matrixWorld;
    this.vertexCount = vertexCount;
    this.positionBuffer = positionBuffer;
    this.color = color;
    this.width = width;
    this.primitiveType = primitiveType;
  }

  public MeshLineStrip(){}

  @Override
  public int components() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.VERTEXCOUNT
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.COLOR
        | MeshComponent.LINEWIDTH;
  }

  @Override
  public float[] getColor() {
    return color;
  }

  @Override
  public int getPrimitiveType() {
    return primitiveType;
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
  public float getLineWidth() {
    return this.width;
  }

  @Override
  public float getAlpha() {
    return alpha;
  }

  @Override
  public int getVertexCount() {
    return vertexCount;
  }
}
