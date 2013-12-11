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
 * @date 13-7-23
 */

package com.navior.ids.android.view.mall3d.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.navior.ids.android.data.Parameter;
import com.navior.ids.android.utils.DownloadBitmapAction;
import com.navior.ids.android.view.mall3d.OpenglRenderer;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

//opengl related functions
public class OpenglUtil {

  public static TextureSet<String> getTextureSetResourceName() {
    return textureSetResourceName;
  }

  public static TextureSet<Integer> getTextureSetResourceId() {
    return textureSetResourceId;
  }

  public static TextureSet<String> getTextureSetSerialNumber() {
    return textureSetSerialNumber;
  }

  private final static TextureSet<String> textureSetResourceName = new TextureSet<String>(new Loader<Integer, String>() {
    @Override
    public Holder<Integer> load(String input) {
      return new Holder<Integer>(getTextureByResourceId(getResourceId(input)));
    }
  });

  private final static TextureSet<Integer> textureSetResourceId = new TextureSet<Integer>(new Loader<Integer, Integer>() {
    @Override
    public Holder<Integer> load(Integer input) {
      return new Holder<Integer>(getTextureByResourceId(input));
    }
  });

  private final static TextureSet<String> textureSetSerialNumber = new TextureSet<String>(new Loader<Integer, String>() {
    @Override
    public Holder<Integer> load(String input) {
      final Holder<Integer> holder = new Holder<Integer>(-1);

      DownloadBitmapAction.download(OpenglRenderer.getInstance().getContext().getResources(), input, new Holder<Bitmap>() {
        @Override
        public void set(final Bitmap bitmap) {
          OpenglRenderer.getInstance().getOpenglView().queueEvent(new Runnable() {
            @Override
            public void run() {
              holder.set(OpenglUtil.getTextureBitmap(bitmap));
              Parameter.getInstance().requestRender();
            }
          });
        }
      }, 0);

      return holder;
    }
  });

  private static int getResourceId(String name) {
    Context context = OpenglRenderer.getInstance().getContext();
    name = name.toLowerCase();
    return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
  }

  public static void reloadAllTextures() {
    textureSetResourceName.reload();
    textureSetResourceId.reload();
    textureSetSerialNumber.reload();
  }

  public static void clearAllTextures() {
    textureSetResourceName.clear();
    textureSetResourceId.clear();
    textureSetSerialNumber.clear();
  }

  //generate texture and return the id. At least bmp & png are supported.
  //in:  id -> R.drawable.?
  //out: id of the texture
  public static int getTextureByResourceId(int id) {
    InputStream is = OpenglRenderer.getInstance().getContext().getResources().openRawResource(id);
    Bitmap bitmap = BitmapFactory.decodeStream(is);
    return getTextureBitmap(bitmap);
  }

  //including recycling
  public static int getTextureBitmap(Bitmap bitmap) {
    if(bitmap==null)
      return -1;
    int[] textures = new int[1];
    GLES20.glGenTextures(1, textures, 0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    bitmap.recycle();

    return textures[0];
  }

  public static FloatBuffer getFloatBuffer(float[] data) {
    FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    buffer.put(data);
    buffer.position(0);
    return buffer;
  }

  public static ShortBuffer getShortBuffer(short[] data) {
    ShortBuffer buffer = ByteBuffer.allocateDirect(data.length * 2)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer();
    buffer.put(data);
    buffer.position(0);
    return buffer;
  }
}
