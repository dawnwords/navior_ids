package com.navior.ids.android.view.mall3d.mesh;

import com.navior.ids.android.view.mall3d.meshComponent.MCAlpha;
import com.navior.ids.android.view.mall3d.meshComponent.MCColor;
import com.navior.ids.android.view.mall3d.meshComponent.MCMatrixWorld;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCTexcoordBuffer;
import com.navior.ids.android.view.mall3d.meshComponent.MCTexture;
import com.navior.ids.android.view.mall3d.util.Holder;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MeshBillBoardXZ extends Mesh {
  public MeshBillBoardXZ(Holder<Float> alpha, float[] matrixWorld, FloatBuffer positionBuffer, FloatBuffer centerBuffer, FloatBuffer texcoordBuffer, Holder<Integer> texture, float[] pickColor, ShortBuffer indexBuffer, int primitiveCount, int primitiveType) {
    addComponent(new MCAlpha(alpha));
    addComponent(new MCMatrixWorld(matrixWorld));
    addComponent(new MCPositionBuffer(positionBuffer, 0));
    addComponent(new MCPositionBuffer(centerBuffer, 1));
    addComponent(new MCTexcoordBuffer(texcoordBuffer));
    addComponent(new MCTexture(texture));
    addComponent(new MCColor(pickColor));
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
