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
 * @date 13-11-5
 */
package com.navior.ids.android.data.actiondao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.data.DBHelper;
import com.navior.ids.android.utils.Util;
import com.navior.ips.util.DES;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.LinkedList;

public abstract class DAO<T extends Serializable> extends AsyncTask<Void, Void, T> {
  protected SQLiteDatabase db;
  private final Context context;

  private class Action {
    private int s;
    private String m;
    private String d;

    public int getS() {
      return s;
    }

    public void setS(int s) {
      this.s = s;
    }

    public String getM() {
      return m;
    }

    public void setM(String m) {
      this.m = m;
    }

    public String getD() {
      return d;
    }

    public void setD(String d) {
      this.d = d;
    }
  }

  public static boolean dropDB(Context context) {
    return context.deleteFile(Parameter.DB_PATH);
  }

  public DAO(Context context) {
    this.context = context;
  }

  @Override
  protected T doInBackground(Void... params) {
    initDB();
    try {
      //do Select
      T result = doSelect();
      closeDB();
      return result;
    } catch (Exception dbException) {
      //start HttpClient
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost post = new HttpPost(Parameter.SERVER_ROOT_URL + getUrl());

      try {
        LinkedList<BasicNameValuePair> pairs = new LinkedList<BasicNameValuePair>();
        setNameValuePair(pairs);
        post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(post);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
          // network normal
          Action result = Util.getGson().fromJson(EntityUtils.toString(response.getEntity()), Action.class);
          if (result != null && result.getD() != null) {
            final T t = Util.getGson().fromJson(DES.decrypt(result.getD()), getType());
            new Thread() {
              @Override
              public void run() {
                doInsert(t);
                closeDB();
              }
            }.start();
            return t;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    // do Exception
    return null;
  }

  @Override
  protected void onPostExecute(T result) {
    if (result == null) {
      closeDB();
      doException();
    } else {
      doSuccess(result);
    }
  }

  protected void initDB() {
    if (db == null || !db.isOpen()) {
      db = DBHelper.getDB(context);
    }
  }

  protected void closeDB() {
    if (db != null && db.isOpen()) {
      db.close();
      DBHelper.releaseDB();
    }
  }

  protected abstract Type getType();

  protected abstract void setNameValuePair(LinkedList<BasicNameValuePair> pairs);

  protected abstract String[] getArgs();

  protected abstract String getUrl();

  protected abstract T doSelect() throws Exception;

  protected abstract void doInsert(T result);

  protected abstract void doSuccess(T result);

  protected abstract void doException();
}
