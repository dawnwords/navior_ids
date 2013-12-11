package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColor;
import com.navior.ids.android.view.mall3d.meshComponent.MCIndexBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MeshColorIndexed extends Mesh implements MCAlpha, MCPrimitiveType, MCPositionBuffer, MCColor, MCIndexBuffer {
  public int primitiveType;
  public float alpha;
  public float[] matrixWorld;
  public FloatBuffer positionBuffer;
  public float[] color;
  public int indexCount;
  public ShortBuffer indexBuffer;

  public MeshColorIndexed(float[] matrixWorld, float alpha, FloatBuffer positionBuffer, float[] color, int indexCount, ShortBuffer indexBuffer, int primitiveType) {
    this.alpha = alpha;
    this.matrixWorld = matrixWorld;
    this.positionBuffer = positionBuffer;
    this.color = color;
    this.indexCount = indexCount;
    this.indexBuffer = indexBuffer;
    this.primitiveType = primitiveType;
  }

  public MeshColorIndexed(){}

  @Override
  public int components() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.COLOR
        | MeshComponent.INDEXBUFFER;
  }

  @Override
  public float[] getColor() {
    return color;
  }

  @Override
  public int getIndexCount() {
    return indexCount;
  }

  @Override
  public ShortBuffer getIndexBuffer() {
    return indexBuffer;
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
  public float getAlpha() {
    return alpha;
  }
}
