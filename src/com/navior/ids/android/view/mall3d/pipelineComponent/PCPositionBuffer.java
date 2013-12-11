package com.navior.ids.android.view.mall3d.pipelineComponent;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.navior.ids.android.view.mall3d.OpenglRenderer;
import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.meshComponent.MCPositionBuffer;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;

public class PCPositionBuffer extends PipelineComponent {

  public PCPositionBuffer(Pipeline pipeline) {
    super(pipeline);
  }

  private int matrixWVPHandle;
  private int positionHandle;

  @Override
  public void init(int program) {
    matrixWVPHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
  }

  @Override
  public void begin() {
    GLES20.glEnableVertexAttribArray(positionHandle);
  }

  private static float[] matrixWVP = new float[16];
  @Override
  public void set(Mesh m) {
    MCPositionBuffer mesh = (MCPositionBuffer)m;
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, mesh.getPositionBuffer());

    Matrix.multiplyMM(matrixWVP, 0, OpenglRenderer.getInstance().getCamera().getMatrixVP(), 0, mesh.getMatrixWorld(), 0);
    GLES20.glUniformMatrix4fv(matrixWVPHandle, 1, false, matrixWVP, 0);
  }

  @Override
  public void end() {
    GLES20.glDisableVertexAttribArray(positionHandle);
  }
}
