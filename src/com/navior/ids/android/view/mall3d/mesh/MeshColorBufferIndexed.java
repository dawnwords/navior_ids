package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColorBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCIndexBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MeshColorBufferIndexed extends Mesh implements MCAlpha, MCPrimitiveType, MCPositionBuffer, MCIndexBuffer, MCColorBuffer {
  public float alpha;
  public float[] matrixWorld;
  public FloatBuffer positionBuffer;
  public FloatBuffer colorBuffer;
  public int indexCount;
  public ShortBuffer indexBuffer;
  public int primitiveType;

  public MeshColorBufferIndexed(float[] matrixWorld, float alpha, FloatBuffer positionBuffer, FloatBuffer colorBuffer, int indexCount, ShortBuffer indexBuffer, int primitiveType) {
    this.alpha = alpha;
    this.matrixWorld = matrixWorld;
    this.positionBuffer = positionBuffer;
    this.colorBuffer = colorBuffer;
    this.indexCount = indexCount;
    this.indexBuffer = indexBuffer;
    this.primitiveType = primitiveType;
  }

  public MeshColorBufferIndexed(){}

  @Override
  public int components() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.COLORBUFFER
        | MeshComponent.INDEXBUFFER;
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
  public int getIndexCount() {
    return indexCount;
  }

  @Override
  public ShortBuffer getIndexBuffer() {
    return indexBuffer;
  }

  @Override
  public float getAlpha() {
    return alpha;
  }
}
