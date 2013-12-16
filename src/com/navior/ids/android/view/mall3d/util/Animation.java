package com.navior.ids.android.view.mall3d.util;

public abstract class Animation {

  long startTime;
  long length;
  long finishTime;

  public Animation(long length) {
    this.startTime = System.currentTimeMillis();
    this.length = length;
    this.finishTime = startTime + length;

    start();
  }

  public abstract void start();
  public abstract boolean update(float percentage, long past, long future, long deltaTime);
  public abstract void finish();
}
