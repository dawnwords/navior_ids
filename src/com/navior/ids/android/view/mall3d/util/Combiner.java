package com.navior.ids.android.view.mall3d.util;

import java.util.LinkedList;
import java.util.List;

public abstract class Combiner<I, O> {
  protected List<I> inputList = new LinkedList<I>();

  //would clear previous "add" calls.
  public void set(List<I> input) {
    inputList = input;
  }
  public void add(I item) {
    inputList.add(item);
  }

  protected List<O> outputList = new LinkedList<O>();

  //would clear previous "add" calls.
  public List<O> run(List<I> input) {
    set(input);
    return run();
  }

  public List<O> run() {
    outputList.clear();

    if(inputList.isEmpty())
      return outputList;

    O o = open();
    outputList.add(o);
    for(I item : inputList) {
      if(beyondLimit(item)) {
        close(o);
        o = open();
        outputList.add(o);
      }

      combine(item, o);
    }
    close(o);
    return outputList;
  }

  //whether input would breach the limit. yes -> close old and open new.
  protected abstract boolean beyondLimit(I i);

  //generate a new one and return it.
  protected abstract O open();

  //key logic.
  protected abstract void combine(I i, O o);

  //close the old one.
  protected abstract void close(O o);
}
