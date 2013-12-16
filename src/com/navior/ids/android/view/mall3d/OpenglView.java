package com.navior.ids.android.view.mall3d;

import android.app.ProgressDialog;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.view.list.FloorSelector;

import java.util.Observable;
import java.util.Observer;

public class OpenglView extends GLSurfaceView {
  private boolean started = false;
  private OpenglRenderer renderer;

  public OpenglView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setEGLContextClientVersion(2);
    setPreserveEGLContextOnPause(true);
    this.setRenderer(renderer = new OpenglRenderer(this, context));
    setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


    Parameter.getInstance().clearRequestRenderObserver();
    Parameter.getInstance().addRequestRenderObserver(new Observer() {
      @Override
      public void update(Observable observable, Object o) {
        requestRender();
      }
    });
  }

  public void start(final FloorSelector selector, ProgressDialog dialog) {
    renderer.setMall(Parameter.getInstance().getCurrentMall());
    renderer.setSelector(selector);
    renderer.setDialog(dialog);
    Parameter.getInstance().setView3D(true);
    started = true;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    renderer.onTouchEvent(event);
    return true;
  }

  @Override
  public void onPause() {
    if(started)
      super.onPause();
  }

  @Override
  public void onResume() {
    if(started)
      super.onResume();
  }

}
