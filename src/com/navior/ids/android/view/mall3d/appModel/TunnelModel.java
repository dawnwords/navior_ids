package com.navior.ids.android.view.mall3d.appModel;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.mesh.MeshColorBufferIndexed;
import com.navior.ids.android.view.mall3d.pipeline.PipelineName;
import com.navior.ids.android.view.mall3d.model.ModelColorIndexed;
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

    this.setupBuffersColor(
        new float[]{
            x1 + ModelConstants.TUNNEL_SIZE, y1, z1 + ModelConstants.TUNNEL_SIZE,
            x1 + ModelConstants.TUNNEL_SIZE, y1, z1 - ModelConstants.TUNNEL_SIZE,
            x1 - ModelConstants.TUNNEL_SIZE, y1, z1 - ModelConstants.TUNNEL_SIZE,
            x1 - ModelConstants.TUNNEL_SIZE, y1, z1 + ModelConstants.TUNNEL_SIZE,
            x2 + ModelConstants.TUNNEL_SIZE, y2, z2 + ModelConstants.TUNNEL_SIZE,
            x2 + ModelConstants.TUNNEL_SIZE, y2, z2 - ModelConstants.TUNNEL_SIZE,
            x2 - ModelConstants.TUNNEL_SIZE, y2, z2 - ModelConstants.TUNNEL_SIZE,
            x2 - ModelConstants.TUNNEL_SIZE, y2, z2 + ModelConstants.TUNNEL_SIZE
        },
        new float[]{
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
        },
        new short[]{
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            0, 1, 5, 0, 5, 4,
            1, 2, 6, 1, 6, 5,
            2, 3, 7, 2, 7, 6,
            3, 0, 4, 3, 4, 7
        }
    );
  }

  @Override
  public void pick() {

  }

  @Override
  public void draw(boolean selected) {
    OpenglRenderer.getInstance().addMesh(PipelineName.PIPELINE_COLORBUFFER, new MeshColorBufferIndexed(
        matrixWorld, ModelConstants.TUNNEL_ALPHA, verticesBuffer, colorBuffer, indicesNumber, indicesBuffer, GLES20.GL_TRIANGLES
    ));
  }
}
