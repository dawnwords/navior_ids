package com.navior.ids.android.view.mall3d;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.view.list.FloorSelector;
import com.navior.ids.android.view.mall3d.appModel.IconModel;
import com.navior.ids.android.view.mall3d.appModel.ModelConstants;
import com.navior.ids.android.view.mall3d.appModel.ShopModel;
import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.model.Model;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;
import com.navior.ids.android.view.mall3d.pipeline.PipelineBillboardXZ;
import com.navior.ids.android.view.mall3d.pipeline.PipelineBillboardXZPick;
import com.navior.ids.android.view.mall3d.pipeline.PipelineColor;
import com.navior.ids.android.view.mall3d.pipeline.PipelineColorBuffer;
import com.navior.ids.android.view.mall3d.pipeline.PipelineLine;
import com.navior.ids.android.view.mall3d.pipeline.PipelineTexture;
import com.navior.ids.android.view.mall3d.appModel.MallModel;
import com.navior.ids.android.view.mall3d.util.Holder;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;
import com.navior.ids.android.view.mall3d.util.ThirdPersonCamera;
import com.navior.ips.model.Mall;
import com.navior.ips.model.Shop;
import com.navior.ips.model.type.ShapeType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Observable;
import java.util.Observer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenglRenderer implements GLSurfaceView.Renderer{
  private static OpenglRenderer instance = null;
  public static void clearInstance() { instance = null; }
  public static OpenglRenderer getInstance() { return instance; }

  private MallModel mallModel;

  private Context context;
  public Context getContext() { return context; }

  private OpenglView openglView;
  public OpenglView getOpenglView() { return openglView; }

  private ThirdPersonCamera camera = new ThirdPersonCamera();
  public ThirdPersonCamera getCamera() { return camera; }

  private ShopModel selectedShopModel = null;
  private ShopModel lastSelectedShopModel = null;
  public ShopModel getSelectedShopModel() { return selectedShopModel; }

  public final Holder<Float> currentAlpha = new Holder<Float>(1f);

  private FloorSelector selector;
  public FloorSelector getSelector() { return selector; };
  public void setSelector(FloorSelector selector) { this.selector = selector; }

  private ProgressDialog dialog;
  public void dismissDialog() { if(dialog!=null) dialog.dismiss(); dialog = null; }
  public void setDialog(ProgressDialog dialog) { this.dialog = dialog; }


  //---------------------------------------
  //--------------Init & Load--------------

  public OpenglRenderer(OpenglView openglView, Context context) {
    this.openglView = openglView;
    instance = this;
    this.context = context;
  }

  public void setMall(Mall mall) {
    this.mallModel = new MallModel(mall);
    Parameter.getInstance().requestRender();
  }

  @Override
  public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    System.out.println("Renderer onSurfaceCreated");
    if(width == -1) {
      onFirstActivated();
    } else {
      onContextLost();
    }
    onLoad();
  }

  public void onFirstActivated() {
    OpenglUtil.clearAllTextures();
    Parameter.getInstance().setSelectedShop(null, null);

    Parameter.getInstance().setCurrentFloorIndex(0);
    Parameter.getInstance().clearFloorIndexObservers();
    Parameter.getInstance().addFloorIndexObserver(new Observer() {
      @Override
      public void update(Observable observable, Object o) {
        int currentFloorIndex = (Integer) o;
        if((int)(camera.getTarget()[1] / ModelConstants.FLOOR_GAP + ModelConstants.FLOOR_SELECT_OFFSET) != currentFloorIndex)
          camera.setTarget(camera.getTarget()[0], currentFloorIndex * ModelConstants.FLOOR_GAP, camera.getTarget()[2]);
      }
    });
  }

  public void onContextLost() {
    OpenglUtil.reloadAllTextures();
    System.out.println("Renderer Context lost");
  }

  public void onLoad() {
    loadPipelines();

    GLES20.glDisable(GLES20.GL_CULL_FACE);
    GLES20.glClearDepthf(1.0f);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glDepthFunc(GLES20.GL_LEQUAL);

    //alpha blending
    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
  }

  public int width = -1, height = -1;
  @Override
  public void onSurfaceChanged(GL10 gl10, int width, int height) {
    System.out.println("Renderer onSurfaceChanged " + width + "," + height);

    //TODO move to camera
    GLES20.glViewport(0, 0, width, height);
    camera.setAspect((float) width / (float) height);

    if(this.width == -1) {
      if(width > height)
        camera.setFovy(ModelConstants.LANDSCAPE_FOVY);
      else
        camera.setFovy(ModelConstants.PORTRAIT_FOVY);
    } else {
      double oldFovy = camera.getFovy() / 180f * Math.PI;
      double fov = Math.tan(oldFovy/2) / (this.height / 2);
      double h2 = fov * height / 2;
      double at = Math.atan(h2) * 180f / Math.PI;
      camera.setFovy((float) (at * 2));
    }
    this.width = width;
    this.height = height;

    int size;
    if(width > height)
      size = width;
    else
      size = height;
    ModelConstants.EDGE_WIDTH = ModelConstants.EDGE_WIDTH_RADIO * size;

  }

  //---------------------------------------
  //-------------Touch & Draw--------------

  private final static float X_TO_ALPHA = 0.005f, Y_TO_BETA = 0.004f, Y_TO_Y = 2f, X_TO_LEFT = 0.5f, Y_TO_BACK = 0.5f;
  private boolean oldOne;
  private boolean oldTwo;
  private boolean one = false;
  private float x, y;
  private boolean two = false;
  private float cx, cy, d, distance;
  private float downX, downY;
  private boolean touch;
  public boolean onTouchEvent(MotionEvent e) {

    if (e.getAction() == MotionEvent.ACTION_DOWN) {
      downX = e.getX();
      downY = e.getY();
    }
    if (e.getAction() == MotionEvent.ACTION_UP) {
      if (Math.abs(downX - e.getX()) < 10 && Math.abs(downY - e.getY()) < 10)
        touch(e.getX(), e.getY());
    }
//    if(e.getPointerCount() == 3)
//      OpenglRenderer.reloadMall();

    int action = e.getAction() & MotionEvent.ACTION_MASK;
    switch (action) {

      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_DOWN:
      case MotionEvent.ACTION_POINTER_UP:
        oldOne = one;
        oldTwo = two;
        one = e.getPointerCount() == 1;
        two = e.getPointerCount() == 2;
        if (one) {
          setXY(e);
        }
        if (two) {
          cx = (e.getX(0) + e.getX(1)) / 2;
          cy = (e.getY(0) + e.getY(1)) / 2;
          float dx = e.getX(0) - e.getX(1);
          float dy = e.getY(0) - e.getY(1);
          d = (float) Math.sqrt(dx * dx + dy * dy);
          distance = camera.getDistance();
        }
        break;

      case MotionEvent.ACTION_MOVE:
        oldOne = one;
        oldTwo = two;
        one = e.getPointerCount() == 1;
        two = e.getPointerCount() == 2;

        if (one && oldOne) { // drag
          if(Parameter.getInstance().isView3D()) {
            camera.addAngle((e.getX() - x) * X_TO_ALPHA, 0);
            camera.addTarget(0, (e.getY() - y) * Y_TO_Y / height * ModelConstants.FLOOR_GAP, 0);
          } else {
            float deltaX = e.getX() - x;
            float deltaY = e.getY() - y;

            addTarget(deltaX, deltaY);
          }
          setXY(e);
        }
        if (one && oldTwo) { // second finger lost.
          setXY(e);
        }
        if (two && oldTwo) { // pinch
          if(Parameter.getInstance().isView3D()) {
            float dx = e.getX(0) - e.getX(1);
            float dy = e.getY(0) - e.getY(1);
            float nd = (float) Math.sqrt(dx * dx + dy * dy);

            if (d > 100 && nd > 100) {
              camera.setDistance(distance * d / nd);
            }

            float ncx = (e.getX(0) + e.getX(1)) / 2;
            float ncy = (e.getY(0) + e.getY(1)) / 2;

            float deltaX = ncx - cx;
            float deltaY = ncy - cy;

            addTarget(deltaX, deltaY);

            cx = ncx;
            cy = ncy;
          } else {
            float dx = e.getX(0) - e.getX(1);
            float dy = e.getY(0) - e.getY(1);
            float nd = (float) Math.sqrt(dx * dx + dy * dy);

            if (d > 100 && nd > 100) {
              camera.setDistance(distance * d / nd);
            }
          }
        }
        break;

      default:

    }
    clearSelectedShop();
    return true;
  }
  private void clearSelectedShop() {
    selectedShopModel = null;
    Parameter.getInstance().setSelectedShop(null, null);
    Parameter.getInstance().requestRender();
  }

  private void addTarget(float deltaX, float deltaY) {
    float smaller;
    if(width > height)
      smaller = height;
    else
      smaller = width;
    float left = deltaX * X_TO_LEFT / smaller;
    float back = deltaY * Y_TO_BACK / smaller;
    float alpha = camera.getAlpha();
    float dleft = (float) (-left * Math.sin(alpha) - back * Math.cos(alpha));
    float dback = (float) (left * Math.cos(alpha) - back * Math.sin(alpha));
    camera.addTargetByDistance(dleft, 0, dback);
  }

  private void setXY(MotionEvent e) {
    x = e.getX();
    y = e.getY();
  }

  @Override
  public void onDrawFrame(GL10 gl10) {
    long time = System.currentTimeMillis();

    float target = camera.getTarget()[1];
    int floor = (int) (target / ModelConstants.FLOOR_GAP + ModelConstants.FLOOR_SELECT_OFFSET);
    if(floor != Parameter.getInstance().getCurrentFloorIndex()) {
      Parameter.getInstance().setCurrentFloorIndex(floor);
      selector.setFloorIndex(floor);
    }

    if(touch) {

      GLES20.glClearColor(1, 1, 1, 1.0f);
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

      if(mallModel != null)
        mallModel.pick();

      //read the color.
      ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
      //height - y because y comes from touch event, not opengl.
      GLES20.glReadPixels((int) x, height - (int) y, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
      byte c[] = new byte[3];
      int b[] = new int[3];
      pixelBuffer.get(c);
      for(int i = 0; i != 3; i++) // convert signed bytes to (un)signed integers.
        b[i] = (c[i] + 256) % 256;
      if(x + y != 0) {
        //Log.v("Pick", "" + x + " " + y + " color " + b[0] + " " + b[1] + " " + b[2]);
        Model model = Model.pickMesh(b);

        selectMesh(model, x, y);
      }

      // consume the event.
      touch = false;
      x = 0;
      y = 0;
    }

    // do the rendering
    GLES20.glClearColor(0.988f, 0.961f, 0.890f, 1.0f);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    if(mallModel != null)
      mallModel.draw();

    System.out.println("Renderer frame time = " + (System.currentTimeMillis() - time) + "ms");
  }

  private void selectMesh(Model model, float x, float y) {
    if(model instanceof ShopModel) {
      ShopModel shopModel = (ShopModel) model;
      Shop shop = shopModel.getShop();
      if(shop.getT()== ShapeType.STR.getValue() || shop.getT()== ShapeType.PICON.getValue()) {
        if(selectedShopModel == model) {
          setSelectShop(x, y, null, null);
        } else {
          setSelectShop(x, y, shop, shopModel);
        }
      }
    } else if(model instanceof IconModel) {
      Shop icon = ((IconModel) model).getShop();
      setSelectShop(x, y, icon, null);
    } else {
      setSelectShop(x, y, null, null);
    }
  }


  private void setSelectShop(float x, float y, Shop shop, ShopModel shopModel) {
    if(lastSelectedShopModel == shopModel && shopModel!=null) {
      lastSelectedShopModel = null;
      selectedShopModel = null;
      Parameter.getInstance().setSelectedShop(null, null);
    } else {
      selectedShopModel = shopModel;
      if(selectedShopModel!=null)
        lastSelectedShopModel = shopModel;
      Parameter.getInstance().setSelectedShop(shop, new Point((int) x, (int) y));
    }
  }

  public void touch(float x, float y) {
    this.x = x;
    this.y = y;
    touch = true;
  }


  //---------------------------------------
  //---------------Pipelines---------------
  private Pipeline[] pipelines = new Pipeline[Pipeline.COUNT];
  private void loadPipelines() {
    pipelines[Pipeline.PIPELINE_COLOR] = new PipelineColor();
    pipelines[Pipeline.PIPELINE_COLORBUFFER] = new PipelineColorBuffer();
    pipelines[Pipeline.PIPELINE_LINESTRIP] = new PipelineLine();
    pipelines[Pipeline.PIPELINE_TEXTURE] = new PipelineTexture();
    pipelines[Pipeline.PIPELINE_BILLBOARDXZ] = new PipelineBillboardXZ();
    pipelines[Pipeline.PIPELINE_BILLBOARDXZ_PICK] = new PipelineBillboardXZPick();
  }

  public void addMesh(Mesh mesh, int pipeline) {
    pipelines[pipeline].add(mesh);
  }

  public void allFlush() {
    for(int i=0; i!= Pipeline.COUNT; i++) {
      pipelines[i].flush();
    }
  }

}
