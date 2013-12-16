package com.navior.ids.android.view.mall3d.meshComponent;

public class Location {

  //attribute location
  public static final int[] POSITIONBUFFER_ALOCATIONS = new int[]{0,1};
  public static final int COLORBUFFER_ALOCATION = 2;
  public static final int[] TEXCOORDBUFFER_ALOCATIONS = new int[]{3,4};

  //uniform location, set when a pipeline flushes.
  public static int COLOR_ULOCATION = -1;
  public static int[] TEXTURE_ULOCATIONS = new int[]{-1,-1};
  public static int MATRIXMVP_ULOCATION = -1;
  public static int ALPHA_ULOCATION = -1;

}
