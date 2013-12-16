package com.navior.ids.android.view.mall3d.util;

import com.navior.ids.android.data.Parameter;

import java.util.LinkedList;
import java.util.List;

public class Animator {
  protected List<Animation> animations = new LinkedList<Animation>();
  protected long lastTick = -1;
  protected boolean updateResult = true;

  public Animator() {
    lastTick = System.currentTimeMillis();
  }

  public void addAnimation(Animation animation) {
    animations.add(animation);
    Parameter.getInstance().requestRender();
  }

  public void clearAnimation() {
    animations.clear();
  }

  public void tick() {
    long time = System.currentTimeMillis();
    long deltaTime = time-lastTick;

    updateResult = false;

    for(Animation animation : animations) {
      if(animation.finishTime < time) {
        animation.finish();
        animations.remove(animation); //optimize
      } else {
        long past = time - animation.startTime;
        long future = animation.finishTime - time;
        updateResult |= animation.update(((float)(past))/animation.length, past, future, deltaTime);
      }
    }

    lastTick = time;
  }
}

