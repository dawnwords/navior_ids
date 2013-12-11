package com.navior.ids.android.view.mall3d.util;

public class Holder<T> {
  private T value;

  public Holder(T i) {
    value = i;
  }

  public Holder() {

  }


  public void set(T i) {
    value = i;
  }

  public T get() {
    return value;
  }
}
