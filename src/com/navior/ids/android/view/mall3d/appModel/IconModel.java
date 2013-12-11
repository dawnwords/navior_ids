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
import com.navior.ids.android.view.mall3d.model.ModelBillboardXZ;
import com.navior.ids.android.view.mall3d.model.ModelQuad;
import com.navior.ids.android.view.mall3d.model.ModelTexture;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ips.model.Shop;
import com.navior.ips.model.type.IcoType;

public class IconModel extends Model {
  private Shop shop;

  private ModelBillboardXZ quad;

  public IconModel(Shop icon, float floorHeight) {
    shop = icon;
    String fileName = IcoType.get(icon.getIco()).getName();

    float[] lr = icon.getLr();

    modelPick();
    float[] pickColor = getPickColor();
    quad = new ModelBillboardXZ(fileName, lr[0] + lr[2] / 2, floorHeight + ModelConstants.ICON_HEIGHT, lr[1] + lr[2] / 2, ModelConstants.ICON_SIZE);

    quad.setPickColor(pickColor);
    quad.setPasses();
  }

  public void pick() {
    if(quad == null)
      return;

    quad.draw(Pass.PASS_PICK);
  }

  public void draw() {
    if(quad == null)
      return;

    quad.draw(Pass.PASS_DRAW);
  }

  public Shop getShop() {
    return shop;
  }
}
