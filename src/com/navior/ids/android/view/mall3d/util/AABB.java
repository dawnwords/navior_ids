package com.navior.ids.android.view.mall3d.util;

public class AABB {

  public static final float max = 100000f;
  public static final float min = -100000f;

  private float minX = max, minY = max;
  private float maxX = min, maxY = min;
  public AABB() {}

  public void combine(AABB son) {
    if(son.minX < minX) minX = son.minX;
    if(son.minY < minY) minY = son.minY;
    if(son.maxX > maxX) maxX = son.maxX;
    if(son.maxY > maxY) maxY = son.maxY;
  }
  public void combine(float x, float y) {
    if(x < minX) minX = x;
    if(y < minY) minY = y;
    if(x > maxX) maxX = x;
    if(y > maxY) maxY = y;
  }

  //float[] { minX, minY, maxX, maxY }
  public float[] getFloat() {
    return new float[] { minX, minY, maxX, maxY };
  }
  public float getMinX() { return minX; }
  public float getMinY() { return minY; }
  public float getMaxX() { return maxX; }
  public float getMaxY() { return maxY; }

}
