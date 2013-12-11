package com.navior.ids.android.view.mall3d.pipeline;

import android.opengl.GLES20;

import com.navior.ids.android.view.mall3d.mesh.Mesh;
import com.navior.ids.android.view.mall3d.pipelineComponent.PipelineComponent;

import java.util.ArrayList;

public abstract class Pipeline {

  //loaded shaders.
  protected int program;
  //load shader. GLES20.GL_VERTEX_SHADER for vs and GLES20.GL_FRAGMENT_SHADER for ps.
  private static int loadShader(int type, String shaderCode) {
    //create, bind, compile.
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);

    return shader;
  }

  public Pipeline() {

  }

  protected abstract int essentialComponents();

  //components.
  private ArrayList<PipelineComponent> componentList = new ArrayList<PipelineComponent>();
  public void addComponent(PipelineComponent component) {
    componentList.add(component);
  }

  //load shaders, link, get handles(component).
  public void init(String vs, String ps) {
    //load shaders
    int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vs);
    int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, ps);

    //combine
    program = GLES20.glCreateProgram();
    GLES20.glAttachShader(program, vertexShader);
    GLES20.glAttachShader(program, pixelShader);
    GLES20.glLinkProgram(program);

    //components init.
    for(PipelineComponent component : componentList) {
      component.init(program);
    }
  }

  //Add multiple times and draw them all. every frame.
  private ArrayList<Mesh> renderList = new ArrayList<Mesh>();

  public void add(Mesh mesh) {
    renderList.add(mesh);
  }

  public void flush() {
    if(renderList.isEmpty())
      return;

    GLES20.glUseProgram(program);

    for(PipelineComponent component : componentList) {
      component.begin();
    }
    int essentialComponents = essentialComponents();
    for(Mesh mesh : renderList) {
      if((mesh.components() & essentialComponents) == essentialComponents) {
        for(PipelineComponent component : componentList) {
          component.set(mesh);
        }
        draw(mesh);
      } else {
        //report
      }
    }
    for(PipelineComponent component : componentList) {
      component.end();
    }
    renderList.clear();
  }

  public abstract void draw(Mesh mesh);

}