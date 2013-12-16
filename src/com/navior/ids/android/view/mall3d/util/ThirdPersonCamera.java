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

package com.navior.ids.android.view.mall3d.util;

import android.opengl.Matrix;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.view.mall3d.OpenglRenderer;

public class ThirdPersonCamera {

  private float[] matrixVP = new float[16];

  public void setMatrixVP(float[] newVP) {
    matrixVP = newVP;
  }
  public float[] getMatrixVP() {
    return matrixVP;
  }

  private float tx, ty, tz, minX, maxX, minY, maxY, minZ, maxZ;
  private float alpha = 0, beta = 0;
  private float distance = 5.0f, maxDistance, minDistance;
  private float ux = 0, uy = 1, uz = 0;

  private float fovy = 45.0f;
  private float aspect;
  private float zNear = 300f;
  private float zFar = 1000f;
  private double[] direction;

  public ThirdPersonCamera() {
    setMinMaxTarget(new float[]{-1e9f, 1e9f, -1e9f, 1e9f, -1e9f, 1e9f});
  }

  public void checkTarget() {
    if(tx < minX)
      tx = minX;
    if(tx > maxX)
      tx = maxX;
    if(ty < minY)
      ty = minY;
    if(ty > maxY)
      ty = maxY;
    if(tz < minZ)
      tz = minZ;
    if(tz > maxZ)
      tz = maxZ;
  }

  public void setMinMaxTarget(float[] minMax) {
    minX = minMax[0];
    maxX = minMax[1];
    minY = minMax[2];
    maxY = minMax[3];
    minZ = minMax[4];
    maxZ = minMax[5];
  }

  public void setTarget(float x, float y, float z) {
    tx = x;
    ty = y;
    tz = z;
    checkTarget();
  }

  public void addTarget(float dx, float dy, float dz) {
    tx += dx;
    ty += dy;
    tz += dz;
    checkTarget();
  }

  public void addTargetByDistance(float dx, float dy, float dz) {
    tx += dx * distance;
    ty += dy * distance;
    tz += dz * distance;
    checkTarget();
  }

  public float[] getTarget() {
    return new float[]{tx, ty, tz};
  }

  public void setUp(float x, float y, float z) {
    ux = x;
    uy = y;
    uz = z;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float d) {
    distance = d;
    if(distance < minDistance) {
      distance = minDistance;
    }
    if(distance > maxDistance) {
      distance = maxDistance;
    }
  }

  public void setMinMaxDistance(float min, float max) {
    maxDistance = max;
    minDistance = min;
  }

  public void multiplyDistance(float c) {
    distance *= c;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public float getBeta() {
    return beta;
  }

  public void setBeta(float beta) {
    this.beta = beta;
  }

  public void addAngle(float dAlpha, float dBeta) {
    alpha += dAlpha;
    beta += dBeta;
  }

  private static final float pi2 = (float) (Math.PI * 2);
  private void checkAngles() {
    while(alpha >= pi2) {
      alpha -= pi2;
    }
    while(alpha < 0) {
      alpha += pi2;
    }
    if(beta >= Math.PI/2-0.009f) {
      beta = (float) (Math.PI/2 - 0.01f);
    }
    if(beta < 0) {
      beta = 0;
    }
  }

  protected float fx, fy, fz;

  public float[] getEyePos() {
    return new float[]{fx, fy, fz};
  }

  public void action() {
    checkAngles();

    float[] matrixV = new float[16];
    float[] matrixP = new float[16];
    Matrix.perspectiveM(matrixP, 0, fovy, aspect, zNear, zFar);

    if(OpenglRenderer.getInstance().isView3D()) {
      //3d
      direction = calcEyeDirection();
      fx = (float) (tx - direction[0]);
      fy = (float) (ty - direction[1]);
      fz = (float) (tz - direction[2]);
      ux = 0;
      uy = 1;
      uz = 0;
      Matrix.setLookAtM(matrixV, 0, fx, fy, fz, tx, ty, tz, ux, uy, uz);
    } else {
      //2d
      fx = tx;
      fy = ty + distance;
      fz = tz;
      direction = new double[]{0, -1, 0};
//      ux = (float) -Math.cos(alpha);
//      uy = 0;
//      uz = (float) -Math.sin(alpha);
      ux = 0;
      uy = 0;
      uz = -1;
      alpha = (float) (Math.PI/2);
      Matrix.setLookAtM(matrixV, 0, fx, fy, fz, tx, ty, tz, ux, uy, uz);
    }

    Matrix.multiplyMM(matrixVP, 0, matrixP, 0, matrixV, 0);
  }

  private double[] calcEyeDirection() {
    double cosBeta = Math.cos(beta);
    return new double[]{
        -Math.cos(alpha) * cosBeta * distance,
        -Math.sin(beta) * distance,
        -Math.sin(alpha) * cosBeta * distance
    };
  }

  public double[] getEyeDirection() {
    return direction;
  }

  public void setAspect(float aspect) {
    this.aspect = aspect;
  }

  public float getFovy() {
    return fovy;
  }

  public void setFovy(float fovy) {
    this.fovy = fovy;
  }

  public float getzNear() {
    return zNear;
  }

  public void setZNear(float zNear) {
    this.zNear = zNear;
  }

  public float getZFar() {
    return zFar;
  }

  public void setZFar(float zFar) {
    this.zFar = zFar;
  }
}
