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
 * @date 13-11-11
 */
package com.navior.ids.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {
  private static boolean getLock = true;
  private static final Object dbLock = new Object();

  public static synchronized SQLiteDatabase getDB(Context context) {
    getLock();

    SQLiteDatabase db = context.openOrCreateDatabase(Parameter.DB_PATH, Context.MODE_PRIVATE, null);
    try {
      db.beginTransaction();
      for (String create : Parameter.CREATE_TABLE) {
        db.execSQL(create);
      }
      for (String insertCity : Parameter.CITY_DATA) {
        db.execSQL("INSERT INTO d_city(id, nm, abbr, code, lat, lng, mapsize) values" + insertCity);
      }
      db.setTransactionSuccessful();
    } catch (Exception ignored) {
    } finally {
      db.endTransaction();
    }
    return db;
  }

  public static void releaseDB() {
    getLock = true;
    synchronized (dbLock) {
      dbLock.notify();
    }
  }

  private static void getLock() {
    synchronized (dbLock) {
      try {
        while (!getLock) {
          dbLock.wait();
        }
      } catch (InterruptedException ignored) {
      }
    }

    getLock = false;
  }
}
