package com.navior.ids.android.view.mall3d.util;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TextureSet<I> {
  private Loader<Integer, I> loader;

  private List<Pair<Holder<Integer>, I>> data = new ArrayList<Pair<Holder<Integer>, I>>();

  public TextureSet(Loader<Integer, I> loader) {
    this.loader = loader;
  }

  public Holder<Integer> load(I input) {
    Holder<Integer> output = loader.load(input);
    data.add(Pair.create(output, input));
    return output;
  }

  //reload all textures, discarding the old ones.
  public void reload() {
    for(Pair<Holder<Integer>, I> pair : data) {
      pair.first.set(loader.load(pair.second).get());
    }
  }

  public void clear() {
    data.clear();
  }
}
