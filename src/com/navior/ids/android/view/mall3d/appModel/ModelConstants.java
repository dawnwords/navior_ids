package com.navior.ids.android.view.mall3d.appModel;

public class ModelConstants {

  public static final float FLOOR_GAP = 300.0f;

  public static final float FLOOR_ALPHA_MIN = 1f;
  public static final float FLOOR_ALPHA_MAX = 1f;
  public static final float FLOOR_SELECT_OFFSET = 0.5f;

  public static final float BG_HEIGHT = 4.0f;
  public static final float SHOP_HEIGHT = 20.0f;
  public static final float ICON_HEIGHT = 21.0f;
  public static final float ICON_SIZE = 20.0f;

  public static final float SELECTED_ALPHA = 1.0f;
  public static final float UNSELECTED_ALPHA = 0.4f;

  public static final float TEXT_HEIGHT = 22.0f;
  public static final float TEXT_QUAD_SIZE = 15.0f;
  public static final String TEXT_FONT_NAME = "黑体";
  public static final int TEXT_SIZE = 48;
  public static final int TEXT_COLOR = 0xfff47920;
  public static final int MIN_TEXT_TEXTURE_WIDTH = 512;

  public static final float ROUTE_HEIGHT = 5;

  public static final float TUNNEL_ALPHA = 0.2f;
  public static final float TUNNEL_SIZE = 5.0f;
  public static final float TUNNEL_LOW = 1;
  public static final float TUNNEL_HIGH = -1;

  public static final float[] EDGE_MATERIAL = new float[]{0.8f,0.8f,0.8f};
  public static final float[] ROOF_MATERIAL = new float[]{1,1,1};
  public static final float[] WALL_MATERIAL = new float[]{1,1,1};
  public static final float[] LIGHT_DIRECTION_XZ = new float[]{0.8f,0.6f};
  public static final float EDGE_Y_OFFSET = 0.2f;
  public static final float LANDSCAPE_FOVY = 21f;
  public static final float PORTRAIT_FOVY = 45f;
  public static float EDGE_WIDTH = 5.0f;
  public static final float REFERENCE_EDGE_WIDTH = 5.0f / 1920.0f;

  public static final float DUPLICATE_VERTEX_ANGLE_THRESHOLD = 0.5f;
  public static final float BEZIER_DIVIDE_INTERVAL = 2f;
  public static final int VERTEX_BUFFER_MAX_LENGTH = 30000;
  public static final int INDEX_BUFFER_MAX_LENGTH = 30000;
}
