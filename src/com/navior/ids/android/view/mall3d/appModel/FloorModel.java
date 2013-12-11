/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2003 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author zzx
 * @date 13-7-15
 */
package com.navior.ids.android.view.mall3d.appModel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.util.SparseArray;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.model.Model;
import com.navior.ids.android.view.mall3d.model.ModelColor;
import com.navior.ids.android.view.mall3d.model.ModelColorIndexed;
import com.navior.ids.android.view.mall3d.model.ModelLineStrip;
import com.navior.ids.android.view.mall3d.model.ModelQuad;
import com.navior.ids.android.view.mall3d.util.AABB;
import com.navior.ids.android.view.mall3d.util.Combiner;
import com.navior.ids.android.view.mall3d.util.Holder;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;
import com.navior.ips.model.Floor;
import com.navior.ips.model.POI;
import com.navior.ips.model.Shop;
import com.navior.ips.model.type.ShapeType;

import java.util.ArrayList;
import java.util.List;

public class FloorModel extends Model {
  private Floor floor;

  private List<ShopModel> shopModels = new ArrayList<ShopModel>();
  private List<ModelColor> walls = new ArrayList<ModelColor>();
  private List<ModelColorIndexed> roofs = new ArrayList<ModelColorIndexed>();
  private List<ModelLineStrip> edges = new ArrayList<ModelLineStrip>();
  private ArrayList<IconModel> iconModels = new ArrayList<IconModel>();
  private ArrayList<ShopModel> bg = new ArrayList<ShopModel>();
  private ArrayList<TunnelModel> tunnels = new ArrayList<TunnelModel>();
  private ArrayList<ModelQuad> nameQuads;
  private float height = 0;

  private final ArrayList<String> names;
  private final ArrayList<float[]> coordinates;

  private AABB aabb = new AABB();
  private List<ModelQuad> shopIcons;

  public AABB getAABB() {
    return aabb;
  }

  public FloorModel(Floor floor, final float floorHeight) {
    this.floor = floor;
    this.height = floorHeight;

    List<Shop> shops = floor.getG();
    SparseArray<Shop> shopMap = new SparseArray<Shop>(shops.size());

    for(Shop shop : shops) {
      shopMap.put(shop.getId(), shop);

      if(shop.getT() == ShapeType.BG.getValue()) {

        ShopModel shopModel = new ShopModel(shop, true, floorHeight);
        if(shopModel.getWall()!=null && shopModel.getWall().getVertexArray()!=null) {
          shopModel.getWall().finishNewArray();
          shopModel.getRoof().finishNewArray();
          shopModel.getEdge().finishLine();
          bg.add(shopModel);
        }
        aabb.combine(shopModel.getAABB());

      } else if(shop.getT() != ShapeType.PICON.getValue()) {

        ShopModel shopModel = new ShopModel(shop, false, floorHeight);
        if(shopModel.getWall()!=null && shopModel.getWall().getVertexArray()!=null) {
          shopModels.add(shopModel);
          walls.add(shopModel.getWall());
          roofs.add(shopModel.getRoof());
          edges.add(shopModel.getEdge());
        }
        aabb.combine(shopModel.getAABB());

      } else {

        IconModel iconModel = new IconModel(shop, floorHeight);
        iconModels.add(iconModel);

      }
    }

    walls = ModelColor.combine(walls); //triangle_strips -> triangle_strips
    roofs = ModelColorIndexed.combine(roofs); //triangles -> triangles
    edges = ModelLineStrip.combine(edges); //lines -> lines

    //names
    names = new ArrayList<String>();
    coordinates = new ArrayList<float[]>();

    //map poi -> shop
    shopIcons = new ArrayList<ModelQuad>();
    List<POI> pois = floor.getPois();
    for(final POI poi : pois) {
      Shop shop = shopMap.get(poi.getShopId());
      if(shop.getT() == ShapeType.STR.getValue()) {
        if(shop.getNm() != null) {
          if(shop.getLogo() != null) {
            Holder<Integer> texture = OpenglUtil.getTextureSetSerialNumber().load(shop.getLogo());
            shopIcons.add(new ModelQuad( texture,
                0, 1, 1, 0,
                poi.getX(), floorHeight + ModelConstants.ICON_HEIGHT, poi.getY(),
                ModelConstants.ICON_SIZE * 2, ModelConstants.ICON_SIZE
                ));
            Parameter.getInstance().requestRender();

          } else {
            names.add(shop.getNm());
            coordinates.add(new float[]{poi.getX(), poi.getY()});
          }
        }
      }
    }

  }

  public void addTunnel(TunnelModel tunnel) {
    tunnels.add(tunnel);
  }

  public float getFloorHeight() {
    return height;
  }

  public ArrayList<ShopModel> getBg() {
    return bg;
  }

  private static float lerp(float bottom, float top, float min, float max, float value) {
    if(value < bottom) return min;
    if(value > top) return max;
    if(top == bottom) return top;
    float x = (value - bottom) / (top - bottom);
    return max * x + min * (1 - x);
  }

  @Override
  public void draw(boolean selected) {
    ShopModel selectedShopModel = OpenglRenderer.getInstance().getSelectedShopModel();
    boolean here = false;
    if(selectedShopModel!=null) {
      selected = false;
      if(selectedShopModel.getShop().getFloorId() == floor.getId()) {
        here = true;
      }
    }

    for(ShopModel bg : getBg()) {
      bg.draw(selected);
    }

    for(ModelColorIndexed roof : roofs) {
      roof.draw(selected);
    }
    for(ModelColor wall : walls) {
      wall.draw(selected);
    }
    for(ModelLineStrip edge : edges) {
      edge.draw(selected);
    }

    if(here)
      selectedShopModel.draw(true);

    OpenglRenderer.getInstance().allFlush();

    for(IconModel iconModel : iconModels) {
      iconModel.draw(selected);
    }

    if(nameQuads == null || OpenglRenderer.getInstance().isContextLost()) {
      nameQuads = generateNameQuads(names, coordinates, height);
    }
    if(nameQuads!=null)
      for(ModelQuad b : nameQuads) {
        b.draw(selected);
      }
    for(ModelQuad b : shopIcons) {
      b.draw(selected);
    }

    GLES20.glDepthMask(false);
    OpenglRenderer.getInstance().allFlush();
    GLES20.glDepthMask(true);

    for(TunnelModel tunnel : tunnels) {
      tunnel.draw(selected);
    }
    OpenglRenderer.getInstance().allFlush();
  }
  @Override
  public void pick() {
    for(ShopModel bg : getBg()) {
      bg.pick();
    }

    for(ShopModel shopModel : shopModels) {
      shopModel.pick();
    }

    for(IconModel iconModel : iconModels) {
      iconModel.pick();
    }
  }


  // generate billboards with given names.
  private ArrayList<ModelQuad> generateNameQuads(final ArrayList<String> names, ArrayList<float[]> coordinates, float floorHeight) {
    final List<Bitmap> allBitmaps;

    // setup pen.
    final Paint p = new Paint();
    Typeface font = Typeface.create(ModelConstants.TEXT_FONT_NAME, Typeface.BOLD);
    p.setColor(ModelConstants.TEXT_COLOR);
    p.setTypeface(font);
    p.setTextSize(ModelConstants.TEXT_SIZE);
    final Paint.FontMetrics fm = p.getFontMetrics();

    // calculate widths of names & find the actual width I'm going to use.
    final float height = fm.descent - fm.ascent;
    final float[] widths = new float[names.size()];
    float maxWidth = 1;
    for(int i = 0; i != names.size(); i++) {
      widths[i] = p.measureText(names.get(i));
      if(widths[i] > maxWidth)
        maxWidth = widths[i];
    }
    int actualWidth;
    for(actualWidth = 1; actualWidth < maxWidth; actualWidth *= 2) ;
    if(actualWidth < ModelConstants.MIN_TEXT_TEXTURE_WIDTH)
      actualWidth = ModelConstants.MIN_TEXT_TEXTURE_WIDTH;

    final Canvas canvas = new Canvas();
    canvas.drawColor(0x00ffffff);

    ArrayList<ModelQuad> result = new ArrayList<ModelQuad>();
    // start printing
    final ArrayList<Bitmap> images = new ArrayList<Bitmap>(); // image for each billboard.
    final float[][] pixelTexcoord = new float[names.size()][2]; // top left pixel coordinates for each billboard.

    final int finalActualWidth = actualWidth;
    allBitmaps = new Combiner<String, Bitmap>() {
      int i = 0;
      float width;
      Bitmap bmp;
      float x, y;

      @Override
      protected boolean beyondLimit(String s) {
        width = widths[i];
        if(x + width >= finalActualWidth) { // new line
          y += height;
          x = 0;
          if(y + fm.descent > finalActualWidth) { // new texture.
            return true;
          }
        }
        return false;
      }
      @Override
      protected Bitmap open() {
        bmp = Bitmap.createBitmap(finalActualWidth, finalActualWidth, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bmp);
        x = 0;
        y = -fm.ascent;
        return bmp;
      }
      @Override
      protected void combine(String name, Bitmap o) {
        canvas.drawText(name, x, y, p);

        //save info
        images.add(bmp);
        pixelTexcoord[i][0] = x;
        pixelTexcoord[i][1] = y;

        x += width;

        i++;
      }
      @Override
      protected void close(Bitmap o) {

      }
    }.run(names);

    float x, y;
    Bitmap lastImage = null;
    int currentTexture = -1;
    for(int i = 0; i != names.size(); i++) {
      Bitmap currentImage = images.get(i);
      if(currentImage != lastImage) {
        currentTexture = OpenglUtil.getTextureBitmap(currentImage);
        lastImage = currentImage;
      }

      x = pixelTexcoord[i][0];
      y = pixelTexcoord[i][1];

      float left = x / (float) actualWidth;
      float right = (x + widths[i]) / (float) actualWidth;
      float top = 1 - (y + fm.ascent) / (float) actualWidth;
      float bottom = 1 - (y + fm.descent) / (float) actualWidth;

      ModelQuad b = new ModelQuad(new Holder<Integer>(currentTexture), left, top, right, bottom,
          coordinates.get(i)[0], floorHeight + ModelConstants.TEXT_HEIGHT, coordinates.get(i)[1],
          widths[i] / height * ModelConstants.TEXT_QUAD_SIZE, ModelConstants.TEXT_QUAD_SIZE);

      result.add(b);
    }

    for(Bitmap bitmap : allBitmaps) {
      bitmap.recycle();
    }

    return result;
  }
}

