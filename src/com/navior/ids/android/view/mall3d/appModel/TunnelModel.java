package com.navior.ids.android.view.mall3d.appModel;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.mesh.MeshColorBuffer;
import com.navior.ids.android.view.mall3d.model.ModelColorIndexed;
import com.navior.ids.android.view.mall3d.pass.Pass;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;
import com.navior.ids.android.view.mall3d.util.OpenglUtil;
import com.navior.ips.model.POP;

public class TunnelModel extends ModelColorIndexed {

  public TunnelModel(POP a, POP b, int f1, int f2) {
    if(f1 == f2)
      return;
    if(f1 > f2) {
      int ti = f2;
      f2 = f1;
      f1 = ti;
      POP xp = a;
      a = b;
      b = xp;
    }

    float x1 = a.getX(), z1 = a.getY();
    float x2 = b.getX(), z2 = b.getY();
    float y1 = f1 * ModelConstants.FLOOR_GAP + ModelConstants.TUNNEL_LOW;
    float y2 = f2 * ModelConstants.FLOOR_GAP + ModelConstants.TUNNEL_HIGH;

    this.finish(
        8,
        OpenglUtil.getFloatBuffer(new float[]{
            x1 + ModelConstants.TUNNEL_SIZE, y1, z1 + ModelConstants.TUNNEL_SIZE,
            x1 + ModelConstants.TUNNEL_SIZE, y1, z1 - ModelConstants.TUNNEL_SIZE,
            x1 - ModelConstants.TUNNEL_SIZE, y1, z1 - ModelConstants.TUNNEL_SIZE,
            x1 - ModelConstants.TUNNEL_SIZE, y1, z1 + ModelConstants.TUNNEL_SIZE,
            x2 + ModelConstants.TUNNEL_SIZE, y2, z2 + ModelConstants.TUNNEL_SIZE,
            x2 + ModelConstants.TUNNEL_SIZE, y2, z2 - ModelConstants.TUNNEL_SIZE,
            x2 - ModelConstants.TUNNEL_SIZE, y2, z2 - ModelConstants.TUNNEL_SIZE,
            x2 - ModelConstants.TUNNEL_SIZE, y2, z2 + ModelConstants.TUNNEL_SIZE
        }),
        OpenglUtil.getFloatBuffer(new float[]{
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
        }),
        OpenglUtil.getFloatBuffer(new float[]{
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
        }),
        36,
        OpenglUtil.getShortBuffer(new short[]{
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            0, 1, 5, 0, 5, 4,
            1, 2, 6, 1, 6, 5,
            2, 3, 7, 2, 7, 6,
            3, 0, 4, 3, 4, 7
        })
    );
  }

  public void setPasses() {
    setPass(Pass.PASS_DRAW, Pipeline.PIPELINE_COLORBUFFER, new MeshColorBuffer(
        OpenglRenderer.getInstance().currentAlpha, matrixWorld, vertexBuffer, colorBuffer, indexBuffer, indexCount, GLES20.GL_TRIANGLES
    ));
  }
}
