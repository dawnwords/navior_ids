package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCMatrixWorld;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCTexcoordBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCTexture;
import com.navior.ids.android.view.mall3d.util.Holder;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MeshTexture extends Mesh {
  public MeshTexture(Holder<Float> alpha, float[] matrixWorld, FloatBuffer positionBuffer, FloatBuffer texcoordBuffer, Holder<Integer> texture, ShortBuffer indexBuffer, int primitiveCount, int primitiveType) {
    addComponent(new MCAlpha(alpha));
    addComponent(new MCMatrixWorld(matrixWorld));
    addComponent(new MCPositionBuffer(positionBuffer));
    addComponent(new MCTexcoordBuffer(texcoordBuffer));
    addComponent(new MCTexture(texture));
    this.texture = texture; //仅用来检测texture是否已经被赋值，设置uniform仍由MCTexture托管。
    this.indexBuffer = indexBuffer;
    this.primitiveCount = primitiveCount;
    this.primitiveType = primitiveType;
  }

  private Holder<Integer> texture;
  @Override
  public void draw() {
    if(texture.get()>0)
      super.draw();
  }
}
