package com.navior.ids.android.view.mall3d.appModel;


import android.opengl.Matrix;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.view.mall3d.model.ModelPacman;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ips.model.Floor;
import com.navior.ips.model.Location;
import com.navior.ips.model.Mall;

public class LocationModel extends ModelPacman {

  public static final int LOCATION_HEIGHT = 10;
  float x, y, z, theta;

  public LocationModel(float size) {
    super(size, 4);
  }

  public void setLocation(Location location, float theta) {
    setLocation(location);

    setDirection(theta);
  }

  private void setLocation(Location location) {
    x = location.getX();

    //set y;
    Mall mall = Parameter.getInstance().getCurrentMall(); //A HashMap in Parameter?
    for(int i = 0; i != mall.getL().size(); i++) {
      Floor floor = mall.getL().get(i);
      if(floor.getId() == location.getFloorId()) {
        y = ModelConstants.FLOOR_GAP * i + LOCATION_HEIGHT;
        break;
      }
    }

    z = location.getY();
  }

  private void setDirection(float theta) {
    this.theta = theta;
  }

  public void pick() {
//    super.pick();
  }

  public void draw() {
    if(Parameter.getInstance().isView3D()) {
      Matrix.setIdentityM(matrixWorld, 0);
      Matrix.translateM(matrixWorld, 0, x,y,z);
      Matrix.rotateM(matrixWorld, 0, theta, 0,1,0);
      super.draw(Pass.PASS_DRAW);
    } else {
      Matrix.setIdentityM(matrixWorld, 0);
      Matrix.translateM(matrixWorld, 0, x,y,z);
      Matrix.rotateM(matrixWorld, 0, theta, 0,1,0);
      Matrix.rotateM(matrixWorld, 0, 90f, 1,0,0);
      super.draw(Pass.PASS_DRAW);
    }
  }

}
