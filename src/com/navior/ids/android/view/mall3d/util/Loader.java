package com.navior.ids.android.view.mall3d.util;

public interface Loader<O, I> {
  public Holder<O> load(I input);
}
