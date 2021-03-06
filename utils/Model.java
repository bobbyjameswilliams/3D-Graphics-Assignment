/* I declare that this code is my own work */
/* Author Bobby Williams bobby.james.williams@outlook.com */
/* The work of Steve Maddock s.maddock@sheffield.ac.uk, with some additions by Bobby Williams */



package utils;

import gmaths.*;
import com.jogamp.opengl.*;
import utils.*;

import java.util.List;

public class Model {

  private Mesh mesh;
  private int[] textureId1;
  private int[] textureId2;
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private List<Light> lights;

  public Model(GL3 gl, Camera camera, List<Light> light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.lights = light;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }

  public Model(GL3 gl, Camera camera, List<Light> light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, null);
  }

  public Model(GL3 gl, Camera camera, List<Light> light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light, shader, material, modelMatrix, mesh, null, null);
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void setLights(Light lights) {
    this.lights.add(lights);
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light1.position", lights.get(0).getPosition());
    shader.setVec3(gl, "light1.ambient", lights.get(0).getMaterial().getAmbient());
    shader.setVec3(gl, "light1.diffuse", lights.get(0).getMaterial().getDiffuse());
    shader.setVec3(gl, "light1.specular", lights.get(0).getMaterial().getSpecular());
    shader.setFloat(gl,"light1.intensity_mod", lights.get(0).getIntensity());

    shader.setVec3(gl, "light2.position", lights.get(1).getPosition());
    shader.setVec3(gl, "light2.ambient", lights.get(1).getMaterial().getAmbient());
    shader.setVec3(gl, "light2.diffuse", lights.get(1).getMaterial().getDiffuse());
    shader.setVec3(gl, "light2.specular", lights.get(1).getMaterial().getSpecular());
    shader.setFloat(gl,"light2.intensity_mod", lights.get(1).getIntensity());

    shader.setVec3(gl, "spotLight.position", new Vec3(12 ,21.8750f ,5));
    shader.setVec3(gl, "spotLight.ambient", lights.get(2).getMaterial().getAmbient());
    shader.setVec3(gl, "spotLight.diffuse", lights.get(2).getMaterial().getDiffuse());
    shader.setVec3(gl, "spotLight.specular", lights.get(2).getMaterial().getSpecular());
    shader.setVec3(gl, "spotLight.direction", lights.get(2).getDirection());
    shader.setFloat(gl,"spotLight.cutOff", (float)Math.cos(Math.toRadians(12.5f)));
    shader.setFloat(gl,"spotLight.outerCutOff", (float)Math.cos(Math.toRadians(17.5f)));
    shader.setFloat(gl,"spotLight.intensity_mod", lights.get(2).getIntensity());

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    //System.out.print(Museum_GLEventListener.lampSwingAngle);

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    mesh.render(gl);
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  public Material getMaterial(){
    return this.material;
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
  }

}