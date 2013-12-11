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

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.model.ArrayColor;
import com.navior.ids.android.view.mall3d.model.ArrayColorIndexed;
import com.navior.ids.android.view.mall3d.model.ArrayLine;
import com.navior.ids.android.view.mall3d.model.ArrayQuad;
import com.navior.ids.android.view.mall3d.model.ModelColor;
import com.navior.ids.android.view.mall3d.model.ModelColorIndexed;
import com.navior.ids.android.view.mall3d.model.ModelLine;
import com.navior.ids.android.view.mall3d.model.ModelQuad;
import com.navior.ids.android.view.mall3d.pass.Pass;
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

public class FloorModel {
  private Floor floor;

  //combined models for rendering shops
  private List<ModelColor> walls = new ArrayList<ModelColor>();
  private List<ModelColorIndexed> roofs = new ArrayList<ModelColorIndexed>();
  private List<ModelLine> edges = new ArrayList<ModelLine>();

  //other kinds of models
  private ArrayList<ShopModel> bgs = new ArrayList<ShopModel>();
  private ArrayList<IconModel> iconModels = new ArrayList<IconModel>();
  private ArrayList<TunnelModel> tunnels = new ArrayList<TunnelModel>();
  private ArrayList<ModelQuad> nameQuads;

  //keep the related icon so that it can be highlighted too.
  private List<ModelQuad> shopIcons;
  public List<ModelQuad> getShopIcons() { return shopIcons; }
  public void setShopIcons(List<ModelQuad> shopIcons) { this.shopIcons = shopIcons; }

  //bounding-box for double-tap zooming
  private AABB aabb = new AABB();
  public AABB getAABB() { return aabb; }

  public FloorModel(Floor floor, final float floorHeight) {
    this.floor = floor;

    //map shop id to 
    List<Shop> shops = floor.getG();
    SparseArray<Shop> shopMap = new SparseArray<Shop>(shops.size());
    SparseArray<ShopModel> shopModelMap = new SparseArray<ShopModel>(shops.size());


    List<ArrayColor> shopWallData = new ArrayList<ArrayColor>();
    List<ModelColor> shopWallReference = new ArrayList<ModelColor>();
    List<ArrayColorIndexed> shopRoofData = new ArrayList<ArrayColorIndexed>();
    List<ModelColorIndexed> shopRoofReference = new ArrayList<ModelColorIndexed>();
    List<ArrayLine> shopEdgeData = new ArrayList<ArrayLine>();
    List<ModelLine> shopEdgeReference = new ArrayList<ModelLine>();

    List<ShopModel> shopModels = new ArrayList<ShopModel>();
    for(Shop shop : shops) {
      shopMap.put(shop.getId(), shop);

      if(shop.getT() == ShapeType.BG.getValue()) {

        ShopModel shopModel = new ShopModel(shop, true, floorHeight);
        if(shopModel.isValid()) {
          shopModel.getWall().finish(shopModel.getWallData());
          shopModel.getRoof().finish(shopModel.getRoofData());
          shopModel.getEdge().finish(shopModel.getEdgeData());
          bgs.add(shopModel);
        }
        aabb.combine(shopModel.getAABB());

        shopModelMap.put(shop.getId(), shopModel);

      } else if(shop.getT() != ShapeType.PICON.getValue()) {

        ShopModel shopModel = new ShopModel(shop, false, floorHeight);
        if(shopModel.isValid()) {
          shopModels.add(shopModel);
          shopWallData.add(shopModel.getWallData());
          shopWallReference.add(shopModel.getWall());
          shopRoofData.add(shopModel.getRoofData());
          shopRoofReference.add(shopModel.getRoof());
          shopEdgeData.add(shopModel.getEdgeData());
          shopEdgeReference.add(shopModel.getEdge());
        }
        aabb.combine(shopModel.getAABB());

        shopModelMap.put(shop.getId(), shopModel);

      } else {

        IconModel iconModel = new IconModel(shop, floorHeight);
        iconModels.add(iconModel);

      }
    }

    walls = ModelColor.combine(shopWallData, shopWallReference); //triangle_strips -> triangle_strips
    roofs = ModelColorIndexed.combine(shopRoofData, shopRoofReference); //triangles -> triangles
    edges = ModelLine.combine(shopEdgeData, shopEdgeReference); //lines -> lines

    for(ShopModel shopModel : shopModels) {
      shopModel.clearArrayData();
    }

    //names
    ArrayList<POI> namePOI = new ArrayList<POI>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<float[]> coordinates = new ArrayList<float[]>();

    //map poi -> shop
    shopIcons = new ArrayList<ModelQuad>();
    List<POI> pois = floor.getPois();
    for(final POI poi : pois) {
      Shop shop = shopMap.get(poi.getShopId());
      if(shop.getT() == ShapeType.STR.getValue()) {
        if(shop.getLogo() != null) {
          Holder<Integer> texture = OpenglUtil.getTextureSetSerialNumber().load(shop.getLogo());
          ModelQuad icon;
          shopIcons.add(icon = new ModelQuad(texture,
              0, 1, 1, 0,
              poi.getX(), floorHeight + ModelConstants.ICON_HEIGHT, poi.getY(),
              ModelConstants.ICON_SIZE * 2, ModelConstants.ICON_SIZE
          ));

          ShopModel shopModel = shopModelMap.get(poi.getShopId());
          shopModel.setShopIcon(icon);
        } else if(shop.getNm() != null) {
          namePOI.add(poi);
          names.add(shop.getNm());
          coordinates.add(new float[]{poi.getX(), poi.getY()});
        }
      }
    }
    nameQuads = generateNameQuads(names, coordinates, floorHeight);
    for(int i=0, size=nameQuads.size(); i!=size; i++) {
      shopModelMap.get(namePOI.get(i).getShopId()).setShopIcon(nameQuads.get(i));
    }

  }

  public void addTunnel(TunnelModel tunnel) {
    tunnels.add(tunnel);
  }

  public void draw() {
    ShopModel selectedShopModel = OpenglRenderer.getInstance().getSelectedShopModel();
    boolean here = false;
    if(selectedShopModel!=null) {
      if(selectedShopModel.getShop().getFloorId() == floor.getId()) {
        here = true;
      }
      OpenglRenderer.getInstance().currentAlpha.set(ModelConstants.UNSELECTED_ALPHA);
    } else {
      OpenglRenderer.getInstance().currentAlpha.set(ModelConstants.SELECTED_ALPHA);
    }

    for(ShopModel bg : bgs) {
      bg.draw();
    }

    for(ModelColorIndexed roof : roofs) {
      roof.draw(Pass.PASS_DRAW);
    }
    for(ModelColor wall : walls) {
      wall.draw(Pass.PASS_DRAW);
    }
    for(ModelLine edge : edges) {
      edge.draw(Pass.PASS_DRAW);
    }
    OpenglRenderer.getInstance().allFlush();

    if(here) {
      OpenglRenderer.getInstance().currentAlpha.set(ModelConstants.SELECTED_ALPHA);
      selectedShopModel.draw();

      if(selectedShopModel.getShopIcon() !=null)
        selectedShopModel.getShopIcon().draw(Pass.PASS_DRAW);

      OpenglRenderer.getInstance().allFlush();
      OpenglRenderer.getInstance().currentAlpha.set(ModelConstants.UNSELECTED_ALPHA);
    }

    for(IconModel iconModel : iconModels) {
      iconModel.draw();
    }
    for(ModelQuad b : nameQuads) {
      b.draw(Pass.PASS_DRAW);
    }
    for(ModelQuad b : shopIcons) {
      b.draw(Pass.PASS_DRAW);
    }

    GLES20.glDepthMask(false);
    OpenglRenderer.getInstance().allFlush();
    GLES20.glDepthMask(true);

    OpenglRenderer.getInstance().currentAlpha.set(ModelConstants.TUNNEL_ALPHA);
    for(TunnelModel tunnel : tunnels) {
      tunnel.draw(Pass.PASS_DRAW);
    }
    OpenglRenderer.getInstance().allFlush();
    OpenglRenderer.getInstance().currentAlpha.set(ModelConstants.SELECTED_ALPHA);
  }

  public void pick() {
    for(ShopModel bg : bgs) {
      bg.pick();
    }

    for(ModelColorIndexed roof : roofs) {
      roof.draw(Pass.PASS_PICK);
    }
    for(ModelColor wall : walls) {
      wall.draw(Pass.PASS_PICK);
    }

    for(IconModel iconModel : iconModels) {
      iconModel.pick();
    }
  }


  // generate billboards with given names.
  private ArrayList<ModelQuad> generateNameQuads(final ArrayList<String> names, final ArrayList<float[]> coordinates, final float floorHeight) {
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

    final ArrayList<ModelQuad> result = new ArrayList<ModelQuad>();
    // start printing
    final float[][] pixelTexcoord = new float[names.size()][2]; // top left pixel coordinates for each billboard.

    final int finalActualSize = actualWidth;
    final float actualWidthD1 = 1f / finalActualSize;
    new Combiner<String, Bitmap>() {
      int i = 0;
      float width;
      Bitmap bmp;
      float x, y;
      int beginIndex, endIndex;

      @Override
      protected boolean beyondLimit(String s) {
        width = widths[i];
        if(x + width >= finalActualSize) { // new line
          y += height;
          x = 0;
          if(y + fm.descent > finalActualSize) { // new texture.
            return true;
          }
        }
        return false;
      }

      @Override
      protected Bitmap open() {
        bmp = Bitmap.createBitmap(finalActualSize, finalActualSize, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bmp);
        x = 0;
        y = -fm.ascent;
        beginIndex = i;
        return bmp;
      }
      @Override
      protected void combine(String name, Bitmap o) {
        canvas.drawText(name, x, y, p);

        pixelTexcoord[i][0] = x;
        pixelTexcoord[i][1] = y;

        x += width;

        i++;
      }

      @Override
      protected void close(Bitmap o) {
        endIndex = i;

        Holder<Integer> texture = new Holder<Integer>(OpenglUtil.getTextureBitmap(o));
        ArrayQuad combinedQuad = new ArrayQuad(endIndex - beginIndex, texture);

        for(int i=beginIndex; i!=endIndex; i++) {
          float x = pixelTexcoord[i][0];
          float y = pixelTexcoord[i][1];
          float left = x * actualWidthD1;
          float right = (x + widths[i]) * actualWidthD1;
          float top = 1 - (y + fm.ascent) * actualWidthD1;
          float bottom = 1 - (y + fm.descent) * actualWidthD1;

          combinedQuad.addQuad(
              coordinates.get(i)[0], floorHeight + ModelConstants.TEXT_HEIGHT, coordinates.get(i)[1], //xyz
              widths[i] / height * ModelConstants.TEXT_QUAD_SIZE/2, ModelConstants.TEXT_QUAD_SIZE/2, //center xy
              left, right, top, bottom //texcoord
          );
        }
        result.add(new ModelQuad(combinedQuad));
        o.recycle();
      }
    }.run(names);

    return result;
  }

}

