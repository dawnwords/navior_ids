package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColorBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCMatrixWorld;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.util.Holder;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MeshColorBuffer extends Mesh {
  public MeshColorBuffer(Holder<Float> alpha, float[] matrixWorld, FloatBuffer positionBuffer, FloatBuffer colorBuffer, ShortBuffer indexBuffer, int primitiveCount, int primitiveType) {
    addComponent(new MCAlpha(alpha));
    addComponent(new MCMatrixWorld(matrixWorld));
    addComponent(new MCPositionBuffer(positionBuffer));
    addComponent(new MCColorBuffer(colorBuffer));
    this.indexBuffer = indexBuffer;
    this.primitiveCount = primitiveCount;
    this.primitiveType = primitiveType;
  }
}
