/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2003 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author grace
 * @date 2011-6-3
 */

package com.navior.ids.android.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.view.mall3d.util.Holder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class DownloadBitmapAction {

  public static final int OUT_IMG_WIDTH = 128;
  public static final int OUT_IMG_HEIGHT = 64;

  public final static void download(Resources resources, String sn,
                                    Object object, int defaultResId) {
    if (sn != null) {
      File snFile = new File(Parameter.EXTERNAL_STORAGE_IMAGE_FOLDER + sn);
      if (snFile.exists()) {
        InputStream inputStream = null;
        try {
          inputStream = new FileInputStream(snFile);
          Bitmap bitmap = getSizeFixedBitmap(inputStream);
          if (object != null) {
            if (object instanceof ImageView) {
              ImageView imageView = (ImageView) object;
              imageView.setImageBitmap(bitmap);
            } else if (object instanceof ImageSwitcher) {
              ImageSwitcher imageSwitcher = (ImageSwitcher) object;
              Drawable drawable = new BitmapDrawable(resources, bitmap);
              imageSwitcher.setImageDrawable(drawable);
            } else if (object instanceof Holder) {
              Holder<Bitmap> bitmapHolder = (Holder<Bitmap>) object;
              bitmapHolder.set(bitmap);
            }
          }
        } catch (FileNotFoundException e) {
          startBitmapDownloaderTask(resources, sn, object, defaultResId, snFile);
        } catch (NullPointerException e) {

        } finally {
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
            }
          }
        }
      } else {
        startBitmapDownloaderTask(resources, sn, object, defaultResId, snFile);
      }
    }
  }

  private static void startBitmapDownloaderTask(Resources resources, String sn, Object object, int defaultResId, File snFile) {
    BitmapDownloaderTask task = new BitmapDownloaderTask(resources, object,
        defaultResId, snFile);
    task.execute(Parameter.SERVER_ROOT_URL + "/fetchBinary.action?sn=" + sn);
  }

  private final static Bitmap downloadBitmap(String url) {
    final HttpClient client = new DefaultHttpClient();
    final HttpGet getRequest = new HttpGet(url);

    try {
      HttpResponse response = client.execute(getRequest);
      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        Log.e("downloadBitmap", "Error " + statusCode
            + " while retrieving bitmap from " + url);
        return null;
      }

      final HttpEntity entity = response.getEntity();
      if (entity != null) {
        InputStream inputStream = null;
        try {
          inputStream = new FlushedInputStream(entity.getContent());
          return getSizeFixedBitmap(inputStream);
        } finally {
          if (inputStream != null) {
            inputStream.close();
          }
          entity.consumeContent();
        }
      }
    } catch (Exception e) {
      getRequest.abort();
      Log.e("downloadBitmap", "Error while retrieving bitmap from " + url, e);
    }
    return null;
  }

  private static Bitmap getSizeFixedBitmap(InputStream inputStream) {
    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    if(bitmap == null)
      return null;
    return Bitmap.createScaledBitmap(bitmap, OUT_IMG_WIDTH, OUT_IMG_HEIGHT, true);
  }

  private static class BitmapDownloaderTask extends
      AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> imageViewReference = null;
    private WeakReference<ImageSwitcher> imageSwitcherReference = null;
    private Holder<Bitmap> bitmapSetterReference = null;
    private int defaultResId;
    private File imageFile;
    private Resources resources;

    public BitmapDownloaderTask(Resources resources, Object object,
                                int defaultResId, File snFile) {
      if (object instanceof ImageView) {
        ImageView imageView = (ImageView) object;
        imageViewReference = new WeakReference<ImageView>(imageView);
      } else if (object instanceof ImageSwitcher) {
        ImageSwitcher imageSwitcher = (ImageSwitcher) object;
        imageSwitcherReference = new WeakReference<ImageSwitcher>(imageSwitcher);
      } else if (object instanceof Holder) {
        bitmapSetterReference = (Holder<Bitmap>) object;
      }
      this.defaultResId = defaultResId;
      this.imageFile = snFile;
      this.resources = resources;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
      return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if (isCancelled()) {
        bitmap = null;
      }
      if (bitmap != null) {
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream(imageFile);
          bitmap.compress(CompressFormat.JPEG, 100, fos);
          fos.flush();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
          if (fos != null) {
            try {
              fos.close();
            } catch (Exception e2) {
            }
          }
        }
      }

      if (imageViewReference != null) {
        ImageView imageView = imageViewReference.get();
        if (imageView != null) {
          if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
          } else if (defaultResId != 0) {
            imageView.setImageResource(defaultResId);
          }
        }
      } else if (imageSwitcherReference != null) {
        ImageSwitcher imageSwitcher = imageSwitcherReference.get();
        if (imageSwitcher != null) {
          if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(resources, bitmap);
            imageSwitcher.setImageDrawable(drawable);
          } else if (defaultResId != 0) {
            imageSwitcher.setImageResource(defaultResId);
          }
        }
      } else if (bitmapSetterReference != null) {
        Holder<Bitmap> bitmapSetter = bitmapSetterReference;
        if (bitmap != null) {
          bitmapSetter.set(bitmap);
        }
      }
    }
  }

  private static class FlushedInputStream extends FilterInputStream {
    public FlushedInputStream(InputStream inputStream) {
      super(inputStream);
    }

    @Override
    public long skip(long n) throws IOException {
      long totalBytesSkipped = 0L;
      while (totalBytesSkipped < n) {
        long bytesSkipped = in.skip(n - totalBytesSkipped);
        if (bytesSkipped == 0L) {
          int b = read();
          if (b < 0) {
            break; // we reached EOF
          } else {
            bytesSkipped = 1; // we read one byte
          }
        }
        totalBytesSkipped += bytesSkipped;
      }
      return totalBytesSkipped;
    }
  }
}
