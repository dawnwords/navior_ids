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
 * @date 13-7-22
 */
package com.navior.ids.android.view.mall3d.util;

public class Vector3 {
  public float x, y, z;

  public Vector3(double[] d) {
    if(d.length >= 3) {
      this.x = (float) d[0];
      this.y = (float) d[1];
      this.z = (float) d[2];
    }
  }

  public Vector3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3() {
    x = y = z = 0;
  }

  public Vector3(float[] array) {
    if(array.length >= 3) {
      x = array[0];
      y = array[1];
      z = array[2];
    } else {
      x = y = z = 0;
    }
  }

  public Vector3 plus(Vector3 v) {
    return new Vector3(x + v.x, y + v.y, z + v.z);
  }

  public Vector3 neg() {
    return new Vector3(-x, -y, -z);
  }

  public Vector3 minus(Vector3 v) {
    return new Vector3(x - v.x, y - v.y, z - v.z);
  }

  public float dot(Vector3 v) {
    return x * v.x + y * v.y + z * v.z;
  }

  public Vector3 cross(Vector3 v) {
    return new Vector3(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    );
  }

  public float length() {
    float l2 = x * x + y * y + z * z;
    return (float) (Math.sqrt(l2));
  }

  public Vector3 normalize() {
    float l2 = x * x + y * y + z * z;
    if(Math.abs(l2) < 0.0001f)
      return new Vector3(1, 0, 0); // just return something so caller would be happy.
    float l1 = (float) (1 / Math.sqrt(l2));
    return this.multiply(l1);
  }

  public Vector3 multiply(float c) {
    return new Vector3(x * c, y * c, z * c);
  }
}
