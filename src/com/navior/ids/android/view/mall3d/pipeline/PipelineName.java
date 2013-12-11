package com.navior.ids.android.view.mall3d.pipeline;

public enum PipelineName {
  PIPELINE_NOTHING(0),
  PIPELINE_COLOR(1),
  PIPELINE_COLORBUFFER(2),
  PIPELINE_LINESTRIP(3),
  PIPELINE_TEXTURE(4);

  private int index;
  public static final int maxIndex = 5;

  private PipelineName(int index) {
    this.index = index;
  }

  public int get() { return index; }
}
