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

import com.navior.ids.android.data.DBHelper;
import com.navior.ids.android.data.Parameter;
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

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

public abstract class DAO<T extends List> extends AsyncTask<Void, Void, T> {
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
      // do Select
      T result = doSelect();
      // local cache miss
      if (result == null || result.size() == 0) {
        throw new Exception();
      }
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
            // start inserting thread
            new Thread() {
              @Override
              public void run() {
                // do Insert
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

  /**
   * Gson needs Type to parse json to object.
   * We can't get the Type directly from a Generic Type of java.
   * So DAO needs its subclass to tell the true Type of the Genetic Type given.
   *
   * @return the Type of Genetic Type defined by DAO
   */
  protected abstract Type getType();

  /**
   * @param pairs the HTTP Post arguments as NameValuePair for subclass to complete
   */
  protected abstract void setNameValuePair(LinkedList<BasicNameValuePair> pairs);

  /**
   * @return the arguments array for SQL Selection
   */
  protected abstract String[] getArgs();

  /**
   * @return the URL where the HTTPClient fetches data
   */
  protected abstract String getUrl();

  /**
   * Selecting Operation in local DB for subclass to define
   *
   * @return DB selection results
   */
  protected abstract T doSelect();

  /**
   * Inserting Operation in local DB for subclass to define
   *
   * @param result for subclass to insert into DB
   */
  protected abstract void doInsert(T result);

  /**
   * Callback Method after fetching the data successfully
   *
   * @param result given to subclass to use
   */
  protected abstract void doSuccess(T result);

  /**
   * Callback Method if fetching data fails
   */
  protected abstract void doException();
}
