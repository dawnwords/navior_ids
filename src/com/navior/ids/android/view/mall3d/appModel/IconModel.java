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
 * @date 13-7-17
 */
package com.navior.ids.android.view.mall3d.appModel;

import com.navior.ids.android.view.mall3d.model.Model;
import com.navior.ids.android.view.mall3d.model.ModelQuad;
import com.navior.ids.android.view.mall3d.model.ModelTexture;
import com.navior.ips.model.Shop;
import com.navior.ips.model.type.IcoType;

public class IconModel extends Model {
  private Shop shop;

  private ModelTexture quad;

  public IconModel(Shop icon, float floorHeight) {
    shop = icon;
    String fileName = IcoType.get(icon.getIco()).getName();

    float[] lr = icon.getLr();
    quad = new ModelQuad(fileName, lr[0] + lr[2] / 2, floorHeight + ModelConstants.ICON_HEIGHT, lr[1] + lr[2] / 2, ModelConstants.ICON_SIZE, ModelConstants.ICON_SIZE);
  }

  @Override
  public void pick() {
    if(quad == null)
      return;

    modelPick();
    quad.pick();
  }

  @Override
  public void draw(boolean selected) {
    if(quad == null)
      return;

    quad.draw(selected);
  }

  public Shop getShop() {
    return shop;
  }
}
