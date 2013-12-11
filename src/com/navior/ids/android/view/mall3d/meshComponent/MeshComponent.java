package com.navior.ids.android.view.mall3d.meshComponent;

public interface MeshComponent {

  public int components();

  public static final int PRIMITIVETYPE   = 1 << 1;
  public static final int POSITIONBUFFER  = 1 << 2;
  public static final int COLOR           = 1 << 3;
  public static final int COLORBUFFER     = 1 << 4;
  public static final int TEXTURE         = 1 << 5;
  public static final int INDEXBUFFER     = 1 << 6;
  public static final int LINEWIDTH       = 1 << 7;
  public static final int VERTEXCOUNT     = 1 << 8;
  public static final int ALPHA           = 1 << 9;
}
