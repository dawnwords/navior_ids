/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2010 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author cs1
 * @date 13-8-15
 */
package com.navior.ids.android.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.R;
import com.navior.ids.android.utils.HanziToPinyin.Token;
import com.navior.ips.model.City;
import com.navior.ips.model.Mall;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Util {
  private final static Gson gson = new GsonBuilder().create();

  public static String getPinYin(String input) {
    ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
    String full = "";
    String initial = "";
    if (tokens != null && tokens.size() > 0) {
      for (Token token : tokens) {
        if (Token.PINYIN == token.type) {
          full += token.target;
          initial += token.target.charAt(0);
        } else {
          full += token.source;
          initial += token.source;
        }
      }
    }
    return (full + "|" + initial).toLowerCase();
  }

  public static float calculateDistance(Mall mall) {
    LatLng myPosition = Parameter.getInstance().getOutdoorPosition();
    if (myPosition == null) {
      return -1;
    }
    return AMapUtils.calculateLineDistance(new LatLng(mall.getLat(), mall.getLng()), myPosition);
  }

  public static LatLng getSymmetryLatLng(LatLng point) {
    LatLng myPosition = Parameter.getInstance().getOutdoorPosition();
    if (myPosition == null) {
      return point;
    }
    return new LatLng(2 * myPosition.latitude - point.latitude, 2 * myPosition.longitude - point.longitude);
  }

  public static float calculateDistance(City city) {
    LatLng myPosition = Parameter.getInstance().getOutdoorPosition();
    if (myPosition == null) {
      return -1;
    }
    return AMapUtils.calculateLineDistance(new LatLng(city.getLat(), city.getLng()), myPosition);
  }

  public static String getDistanceString(float distance) {
    if (distance < 0)
      return "未知";

    if (distance > 1000) {
      distance /= 1000;
      if (distance > 100)
        return new DecimalFormat(",###千米").format(distance);
      if (distance > 10)
        return new DecimalFormat("##.#千米").format(distance);
      return new DecimalFormat("#.##千米").format(distance);
    }
    if (distance > 100)
      return new DecimalFormat("###.#米").format(distance);
    return new DecimalFormat("#.##米").format(distance);
  }

  public static ProgressDialog getProcessDialog(Context context, int msgRes) {
    return ProgressDialog.show(context, context.getString(R.string.loading), context.getString(msgRes), false, false);
  }

  public final static Gson getGson() {
    return gson;
  }
}
