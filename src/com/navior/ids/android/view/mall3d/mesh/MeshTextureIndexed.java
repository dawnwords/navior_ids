package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCIndexBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCPrimitiveType;
import com.navior.ids.android.view.mall3d.meshComponent.MCTexture;
import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MeshTextureIndexed extends Mesh implements MCAlpha, MCPrimitiveType, MCPositionBuffer, MCIndexBuffer, MCTexture {
  public float alpha;
  public float[] matrixWorld;
  public FloatBuffer positionBuffer;
  public FloatBuffer texcoordBuffer;
  public int indexCount;
  public ShortBuffer indexBuffer;
  public int texture;
  public int primitiveType;

  public MeshTextureIndexed(float[] matrixWorld, float alpha, FloatBuffer positionBuffer, FloatBuffer texcoordBuffer, int indexCount, ShortBuffer indexBuffer, int texture, int primitiveType) {
    this.alpha = alpha;
    this.matrixWorld = matrixWorld;
    this.positionBuffer = positionBuffer;
    this.texcoordBuffer = texcoordBuffer;
    this.indexCount = indexCount;
    this.indexBuffer = indexBuffer;
    this.texture = texture;
    this.primitiveType = primitiveType;
  }

  public MeshTextureIndexed(){}

  @Override
  public int components() {
    return MeshComponent.PRIMITIVETYPE
        | MeshComponent.ALPHA
        | MeshComponent.POSITIONBUFFER
        | MeshComponent.INDEXBUFFER
        | MeshComponent.TEXTURE;
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
  public int getTexture() {
    return texture;
  }

  @Override
  public FloatBuffer getTexcoordBuffer() {
    return texcoordBuffer;
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
