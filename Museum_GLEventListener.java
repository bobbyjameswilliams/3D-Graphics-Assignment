import gmaths.*;

import com.jogamp.opengl.*;

import java.util.Collections;
import java.util.List;

public class Museum_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;

  public Museum_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(-2f,15f,40f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }


  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean animation = false;
  private double savedTime = 0;
  private Robot robot;
  private Egg egg;
  private Mobile phone;
  private Lamp lamp;


  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }


  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }


  public void outsideNight(){
    windowView.getMaterial().setAmbient(0.1f,0.1f,0.1f);
    //Slightly red to simulate dusk
    windowView.getMaterial().setDiffuse(0.3f,0.2f,0.2f);
  }


  public void outsideDay(){
    windowView.getMaterial().setAmbient(1f,1f,1f);
    windowView.getMaterial().setDiffuse(0.6f,0.6f,0.6f);
  }


  public void loweredArms() {
    stopAnimation();
    robot.leftFeelerRotate.setTransform(Mat4Transform.rotateAroundX(180));
    robot.leftFeelerRotate.update();
    robot.rightFeelerRotate.setTransform(Mat4Transform.rotateAroundX(180));
    robot.rightFeelerRotate.update();
  }


  public void raisedArms() {
    stopAnimation();
    robot.leftFeelerRotate.setTransform(Mat4Transform.rotateAroundX(0));
    robot.leftFeelerRotate.update();
    robot.rightFeelerRotate.setTransform(Mat4Transform.rotateAroundX(0));
    robot.rightFeelerRotate.update();
  }


  public void turnOffMainLights() {
    light.setIntensity(0);
    mainLight.setIntensity(0);
  }


  public void turnOnMainLights() {
    light.setIntensity(1);
    mainLight.setIntensity(1);
  }


  // ROBOTS POSES
  public void pose1(){
    robot.pose1();
  }


  public void pose2(){
    robot.pose2();
  }


  public void pose3(){
    robot.pose3();
  }


  public void pose4(){
    robot.pose4();
  }


  public void pose5(){
    robot.pose5();
  }


  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere, eye, cube, box, mobilePhone, backwall, sidewall, windowView, lampStand;
  private List<Light> lights;
  private Light light;
  private Light mainLight;
  private Light spotLight;

  //Initialise models

  private Model initialise_floor(GL3 gl, Camera camera, List<Light> lights, int[] texture, float roomSize){
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.glsl", "fs_tt_05.glsl");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 99.0f);
    Mat4 modelMatrix = Mat4Transform.scale(roomSize,1f,roomSize);
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture);
  }


  private Model initialise_backwall(GL3 gl, Camera camera, List<Light> lights, int[] texture, float roomSize){
    Mesh mesh = new Mesh(gl, DoorWall.vertices.clone(), DoorWall.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.glsl", "fs_tt_05.glsl");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 99.0f);
    Mat4 modelMatrix =  Mat4Transform.translate(0,(roomSize/3f),-(roomSize/2));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize ,1f,roomSize/1.5f)); ;
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture);
  }


  private Model initialise_sidewall(GL3 gl, Camera camera, List<Light> lights, int[] texture, float roomSize){
    Mesh mesh = new Mesh(gl, WindowedWall.vertices.clone(), WindowedWall.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.glsl", "fs_tt_05.glsl");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 99.0f);
    Mat4 modelMatrix =  Mat4Transform.translate(-(roomSize/2),(roomSize/3),0);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(-90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize ,1f,roomSize/1.5f));
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture);
  }


  private Model initialise_skybox(GL3 gl, Camera camera, List<Light> lights, int[] texture, float roomSize, float relativeViewOffset){
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.glsl", "fs_tt_05.glsl");
    Material material = new Material(new Vec3(1f, 1f, 1f), new Vec3(0.6f, 0.6f, 0.6f), new Vec3(0.0f, 0.0f, 0.0f), 1.0f);
    Mat4 modelMatrix =  Mat4Transform.translate(-relativeViewOffset,(roomSize/2),0);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(-90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize * 2 ,1f,roomSize * 2));
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture);
  }


  private Model initialise_sphere(GL3 gl, Camera camera, List<Light> lights, int[] texture1, int[] texture2){
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.glsl", "fs_cube_04.glsl");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture1, texture2);
  }


  private Model initialise_cube(GL3 gl, Camera camera, List<Light> lights, int[] texture1, int[]texture2){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.glsl", "fs_cube_04.glsl");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture1, texture2);
  }


  private Model initialise_phone(GL3 gl, Camera camera, List<Light> lights, int[] texture1, int[]texture2){
    Mesh mesh = new Mesh(gl, PhoneCube.vertices.clone(), PhoneCube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.glsl", "fs_cube_04.glsl");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture1, texture2);
  }

  //Initialise and scene


  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] woodFloorTexture = TextureLibrary.loadTexture(gl, "textures/wood_floor.jpg");
    int[] wallTexture = TextureLibrary.loadTexture(gl, "textures/wallTexture.jpg");
    int[] windowViewTexture = TextureLibrary.loadTexture(gl, "textures/windowView.jpg");
    int[] phoneTexture = TextureLibrary.loadTexture(gl, "textures/cube.jpg");
    int[] phoneSpecular = TextureLibrary.loadTexture(gl, "textures/cube_specular.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] boxTexture = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] boxSpecular = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg");
    int[] lampStandTexture = TextureLibrary.loadTexture(gl, "textures/lampStand.jpg");
    int[] lampStandSpecular = TextureLibrary.loadTexture(gl,"textures/jup0vss1_specular.jpg");
        
    light = new Light(gl);
    light.setCamera(camera);
    mainLight = new Light(gl);
    mainLight.setCamera(camera);
    mainLight.setPosition(4,30,4);
    spotLight = new Light(gl);
    spotLight.setCamera(camera);
    spotLight.setDirection(0,0,0);

    List<Light> lights = new java.util.ArrayList<>(Collections.emptyList());
    lights.add(light);
    lights.add(mainLight);
    lights.add(spotLight);

    float roomSize = 40;
    float viewOffset = 8;
    float relativeViewOffset = (roomSize/2) + viewOffset;


    //floor
    floor = initialise_floor(gl, camera, lights, woodFloorTexture, roomSize);
    //back wall
    backwall = initialise_backwall(gl, camera, lights, wallTexture, roomSize);
    //windowed wall
    sidewall = initialise_sidewall(gl, camera, lights, wallTexture, roomSize);
    //sky box
    windowView = initialise_skybox(gl, camera, lights, windowViewTexture, roomSize, relativeViewOffset) ;


    //#################### ROBOT!!! ###############################
    //robot body
    sphere = initialise_sphere(gl,camera,lights,textureId1,textureId2);
    cube = initialise_cube(gl,camera,lights,textureId1,textureId2);
    eye = initialise_cube(gl,camera,lights,wallTexture,textureId6);

    lampStand = initialise_cube(gl,camera,lights,lampStandTexture, lampStandSpecular);

    box = initialise_cube(gl,camera,lights,boxTexture,boxSpecular);
    mobilePhone = initialise_phone(gl,camera,lights,phoneTexture,phoneSpecular);
//
//  //Creating classes for scene objects
    robot = new Robot(gl, cube,eye,sphere);
    egg = new Egg(gl, box , sphere);
    phone = new Mobile(gl,mobilePhone, box);
    lamp = new Lamp(gl,lampStand,startTime);

    //Default robot pose is pose 1
    pose1();
  }


  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    mainLight.render(gl);
    spotLight.setDirection(lamp.lampSwing());
    floor.render(gl);
    backwall.render(gl);
    sidewall.render(gl);
    windowView.render(gl);

    robot.render();
    egg.render();
    phone.render();
    lamp.render();
  }


  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }

}