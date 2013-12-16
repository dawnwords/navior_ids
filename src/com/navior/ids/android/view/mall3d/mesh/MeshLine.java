package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColor;
import com.navior.ids.android.view.mall3d.meshComponent.MCLineWidth;
import com.navior.ids.android.view.mall3d.meshComponent.MCMatrixWorld;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.util.Holder;

import java.nio.FloatBuffer;

public class MeshLine extends Mesh {
  public MeshLine(Holder<Float> alpha, float[] matrixWorld, FloatBuffer positionBuffer, float[] color, Holder<Float> width, int primitiveCount, int primitiveType) {
    addComponent(new MCAlpha(alpha));
    addComponent(new MCMatrixWorld(matrixWorld));
    addComponent(new MCPositionBuffer(positionBuffer));
    addComponent(new MCColor(color));
    addComponent(new MCLineWidth(width));
    this.primitiveCount = primitiveCount;
    this.primitiveType = primitiveType;
  }
}
