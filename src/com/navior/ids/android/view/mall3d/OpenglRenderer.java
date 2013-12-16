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
import com.navior.ids.android.view.mall3d.appModel.MallModel;
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
import com.navior.ids.android.view.mall3d.util.AABB;
import com.navior.ids.android.view.mall3d.util.Animation;
import com.navior.ids.android.view.mall3d.util.Animator;
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
  public static final float DEFAULT_3D_CAMERA_ALPHA = (float) (Math.PI / 2);
  public static final float DEFAULT_3D_CAMERA_BETA = 0.5f;
  private static final long DOUBLE_CLICK_INTERVAL = 300;
  private static final long DOUBLE_CLICK_ZOOM_MAX_TIME = 500;
  private static final long DOUBLE_CLICK_ZOOM_MAX_TARGET_DISTANCE = 200;
  private static final long DOUBLE_CLICK_ZOOM_MAX_VIEW_DISTANCE = 200;
  private static final float ICON_ZOOM_DISTANCE = 200;
  private static final long SWITCH_VIEW_ANIMATION_TIME = 500;

  private static OpenglRenderer instance = null;
  public static OpenglRenderer getInstance() { return instance; }

  private MallModel mallModel;

  private Context context;
  public Context getContext() { return context; }

  private OpenglView openglView;
  public OpenglView getOpenglView() { return openglView; }

  private ThirdPersonCamera camera = new ThirdPersonCamera();
  public ThirdPersonCamera getCamera() { return camera; }

  private Animator animator = new Animator() {
    @Override
    public void tick() {
      super.tick();
      if(!this.animations.isEmpty())
        Parameter.getInstance().requestRender();
    }
  };
  public Animator getAnimator() { return animator; }

  private ShopModel selectedShopModel = null;
  private ShopModel lastSelectedShopModel = null;
  public ShopModel getSelectedShopModel() { return selectedShopModel; }

  public final Holder<Float> currentAlpha = new Holder<Float>(1f);

  private boolean switchingView = false;
  private boolean view3D = true;
  public boolean isView3D() { return view3D; }
  private float billboardBeta = 0;
  public float getBillboardBeta() { return billboardBeta; }

  private FloorSelector selector;
  public FloorSelector getSelector() { return selector; }
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
        selectedShopModel = null;
        lastSelectedShopModel = null;
        Parameter.getInstance().setSelectedShop(null, null);
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
  private boolean touchEnabled = true;
  private long lastClick = -1;
  private final static float X_TO_ALPHA = 0.005f, Y_TO_BETA = 0.004f, Y_TO_Y = 2f, X_TO_LEFT = 0.5f, Y_TO_BACK = 0.5f;
  private boolean oldOne;
  private boolean oldTwo;
  private boolean one = false;
  private float x, y;
  private boolean two = false;
  private float cx, cy, d, distance;
  private float downX, downY;
  private boolean singleClick, doubleClick;
  public boolean onTouchEvent(MotionEvent e) {
    if(!touchEnabled) {
      oldOne = false;
      oldTwo = false;
      x = e.getX();
      y = e.getY();
      return true;
    }

    if (e.getAction() == MotionEvent.ACTION_DOWN) {
      downX = e.getX();
      downY = e.getY();
    }
    if (e.getAction() == MotionEvent.ACTION_UP) {
      if (Math.abs(downX - e.getX()) < 10 && Math.abs(downY - e.getY()) < 10) {
        x = e.getX();
        y = e.getY();
        long time = System.currentTimeMillis();
        doubleClick = (time - lastClick) < DOUBLE_CLICK_INTERVAL;
        singleClick = !doubleClick;
        lastClick = System.currentTimeMillis();
      }
    }

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
          x = e.getX();
          y = e.getY();
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
          if(view3D) {
            camera.addAngle((e.getX() - x) * X_TO_ALPHA, 0);
            camera.addTarget(0, (e.getY() - y) * Y_TO_Y / height * ModelConstants.FLOOR_GAP, 0);
          } else {
            float deltaX = e.getX() - x;
            float deltaY = e.getY() - y;

            addTarget(deltaX, deltaY);
          }
          x = e.getX();
          y = e.getY();
        }
        if (one && oldTwo) { // second finger lost.
          x = e.getX();
          y = e.getY();
        }
        if (two && oldTwo) { // pinch
          if(OpenglRenderer.getInstance().isView3D()) {
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

  @Override
  public void onDrawFrame(GL10 gl10) {
    long time = System.currentTimeMillis();

    animator.tick();
    camera.action();

    float target = camera.getTarget()[1];
    int floor = (int) (target / ModelConstants.FLOOR_GAP + ModelConstants.FLOOR_SELECT_OFFSET);
    if(floor != Parameter.getInstance().getCurrentFloorIndex()) {
      Parameter.getInstance().setCurrentFloorIndex(floor);
      selector.setFloorIndex(floor);
    }

    if( touchEnabled && (singleClick || doubleClick) ) {

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
      singleClick = false;
      doubleClick = false;
      x = 0;
      y = 0;
    }

    // do the rendering
    GLES20.glClearColor(0.988f, 0.961f, 0.890f, 1.0f);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    if(mallModel != null)
      mallModel.draw();

    if(!touchEnabled)
      Parameter.getInstance().requestRender();

    System.out.println("Renderer frame time = " + (System.currentTimeMillis() - time) + "ms");
  }

  private void selectMesh(Model model, float x, float y) {
    if(model instanceof ShopModel) {
      ShopModel shopModel = (ShopModel) model;
      Shop shop = shopModel.getShop();
      if(shop.getT()== ShapeType.STR.getValue() || shop.getT()== ShapeType.PICON.getValue()) {
        if(selectedShopModel == model) {
          setSelectShop(x, y, null, null, null);
        } else {
          setSelectShop(x, y, shop, shopModel, null);
        }
      }
    } else if(model instanceof IconModel) {
      Shop icon = ((IconModel) model).getShop();
      setSelectShop(x, y, icon, null, (IconModel) model);
    } else {
      setSelectShop(x, y, null, null, null);
    }
  }

  private void setSelectShop(final float x, final float y, final Shop shop, final ShopModel shopModel, final IconModel iconModel) {
    if(doubleClick) {
      if(shopModel==null && iconModel==null) {
        final float[] distances = new float[4];
        distances[0] = camera.getDistance();
        distances[3] = camera.getDistance() * 2;
        distances[1] = distances[0]*1f + distances[3]*0f;
        distances[2] = distances[0]*0f + distances[3]*1f;
        animator.addAnimation(new Animation(DOUBLE_CLICK_ZOOM_MAX_TIME) {
          float d;

          @Override
          public void start() {
            touchEnabled = false;
          }

          @Override
          public boolean update(float percentage, long past, long future, long deltaTime) {
            float a = 1-percentage, b = percentage; //bezier
            float[] c = new float[]{a*a*a, 3*a*a*b, 3*a*b*b, b*b*b};
            d = 0;
            for(int i=0; i!=4; i++) {
              d += c[i] * distances[i];
            }
            OpenglRenderer.getInstance().getCamera().setDistance(d);
            return true;
          }

          @Override
          public void finish() {
            touchEnabled = true;
            update(1, 0, 0, 0);
          }
        });
      } else {
        final float[] distances = new float[4];
        final float[][] targets = new float[4][];
        targets[0] = camera.getTarget();
        distances[0] = camera.getDistance();

        if(shopModel!=null) {
          AABB aabb = shopModel.getAABB();
          targets[3] = new float[3];
          targets[3][0] = (aabb.getMinX() + aabb.getMaxX())/2;
          targets[3][1] = shopModel.getFloorHeight() + ModelConstants.SHOP_HEIGHT;
          targets[3][2] = (aabb.getMinY() + aabb.getMaxY())/2;
          distances[3] = ((aabb.getMaxX() - aabb.getMinX()) + (aabb.getMaxY() - aabb.getMinY())) / ((float)Math.tan(camera.getFovy()/2));
        } else {
          float[] lr = shop.getLr();
          targets[3] = new float[3];
          targets[3][0] = lr[0];
          targets[3][1] = iconModel.getFloorHeight() + ModelConstants.ICON_HEIGHT;
          targets[3][2] = lr[1];
          distances[3] = ICON_ZOOM_DISTANCE;
        }

        targets[1] = new float[3]; targets[2] = new float[3];
        for(int i=0; i!=3; i++) {
          targets[1][i] = targets[0][i]*1f + targets[3][i]*0f;
          targets[2][i] = targets[0][i]*0f + targets[3][i]*1f;
        }
        distances[1] = distances[0]*1f + distances[3]*0f;
        distances[2] = distances[0]*0f + distances[3]*1f;

        float d = Math.abs(distances[3] - distances[0]);
        float totalTargetDistance = 0;
        for(int i=0; i!=3; i++)
          totalTargetDistance += Math.abs(targets[3][i] - targets[0][i]);
        long time = 0;
        time += (long) ((float)DOUBLE_CLICK_ZOOM_MAX_TIME * totalTargetDistance / DOUBLE_CLICK_ZOOM_MAX_TARGET_DISTANCE);
        time += (long) ((float)DOUBLE_CLICK_ZOOM_MAX_TIME * d / DOUBLE_CLICK_ZOOM_MAX_VIEW_DISTANCE);
        if(time > DOUBLE_CLICK_ZOOM_MAX_TIME)
          time = DOUBLE_CLICK_ZOOM_MAX_TIME;

        animator.addAnimation(new Animation(time) {

          float cx,cy,cz;
          float d;

          @Override
          public void start() {
            touchEnabled = false;
          }

          @Override
          public boolean update(float percentage, long past, long future, long deltaTime) {
            float a = 1-percentage, b = percentage; //bezier
            float[] c = new float[]{a*a*a, 3*a*a*b, 3*a*b*b, b*b*b};
            cx = 0; cy = 0; cz = 0; d = 0;
            for(int i=0; i!=4; i++) {
              cx += c[i] * targets[i][0];
              cy += c[i] * targets[i][1];
              cz += c[i] * targets[i][2];
              d += c[i] * distances[i];
            }
            OpenglRenderer.getInstance().getCamera().setDistance(d);
            OpenglRenderer.getInstance().getCamera().setTarget(cx, cy, cz);
            return true;
          }

          @Override
          public void finish() {
            touchEnabled = true;
            update(1,0,0,0);
            selectedShopModel = shopModel;
            if (selectedShopModel != null)
              lastSelectedShopModel = shopModel;
            Parameter.getInstance().setSelectedShop(shop, new Point((int) x, (int) y));
          }
        });
      }
    } else if(lastSelectedShopModel == shopModel && shopModel!=null) {
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
  //---------------------------------------

  public boolean switchView() {
    if(switchingView) return false;
    switchingView = true; //not perfect but secure enough
    selectedShopModel = null;
    lastSelectedShopModel = null;
    Parameter.getInstance().setSelectedShop(null, null);
    animator.addAnimation(new Animation(SWITCH_VIEW_ANIMATION_TIME) {
      float startBillboardBeta, targetBillboardBeta;
      float startCameraBeta, targetCameraBeta;
      float startCameraAlpha, targetCameraAlpha;
      boolean f2t3;
      boolean f3t2;

      @Override
      public void start() {
        touchEnabled = false;
        if(view3D) {
          f2t3 = false; f3t2 = true;
          startBillboardBeta = 0;
          targetBillboardBeta = (float) Math.PI/2;
          startCameraBeta = DEFAULT_3D_CAMERA_BETA;
          targetCameraBeta = (float) Math.PI/2;
          startCameraAlpha = (camera.getAlpha()>Math.PI * 3 / 2) ? (float) (camera.getAlpha() - Math.PI * 2) : camera.getAlpha();
          targetCameraAlpha = (float) Math.PI/2;
        } else {
          f2t3 = true; f3t2 = false;
          startBillboardBeta = (float) Math.PI/2;
          targetBillboardBeta = 0;
          startCameraBeta = (float) Math.PI/2;
          targetCameraBeta = DEFAULT_3D_CAMERA_BETA;
          startCameraAlpha = (float) Math.PI/2;
          targetCameraAlpha = (float) Math.PI/2;
        }
      }

      @Override
      public boolean update(float percentage, long past, long future, long deltaTime) {
        billboardBeta = (1-percentage) * startBillboardBeta + percentage * targetBillboardBeta;
        camera.setAlpha((1-percentage) * startCameraAlpha + percentage * targetCameraAlpha);
        camera.setBeta((1-percentage) * startCameraBeta + percentage * targetCameraBeta);
        if(f2t3)
          view3D = true;
        return true;
      }

      @Override
      public void finish() {
        update(1, 0, 0, 0);
        if(f3t2)
          view3D = false;
        switchingView = false;
        touchEnabled = true;
      }
    });
    return true;
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

  public boolean isSwitching() {
    return switchingView;
  }
  //---------------------------------------
}
