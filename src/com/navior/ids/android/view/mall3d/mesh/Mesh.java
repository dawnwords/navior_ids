package com.navior.ids.android.view.mall3d.mesh;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.meshComponent.MeshComponent;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class Mesh {

  private List<MeshComponent> components = new ArrayList<MeshComponent>();
  protected void addComponent(MeshComponent component) { components.add(component); }

  public void set() {
    for(MeshComponent component : components) {
      component.set();
    }
  }

  protected int primitiveType;
  protected int primitiveCount;
  protected ShortBuffer indexBuffer;

  public void draw() {
    if(indexBuffer!=null)
      GLES20.glDrawElements(primitiveType, primitiveCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    else
      GLES20.glDrawArrays(primitiveType, 0, primitiveCount);
  }
}
