package com.navior.ids.android.view.mall3d.pipelineComponent;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.pipeline.Pipeline;

public abstract class PipelineComponent {
  public PipelineComponent(Pipeline pipeline) {
    pipeline.addComponent(this);
  }
  public abstract void init(int program);
  public abstract void begin();
  public abstract void set(Mesh mesh);
  public abstract void end();
}
