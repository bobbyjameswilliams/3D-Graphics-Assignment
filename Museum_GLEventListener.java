import gmaths.*;

import com.jogamp.opengl.*;

import java.util.Collections;
import java.util.List;

public class Museum_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;

  public Museum_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,30f,40f));
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
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }

  public void pose1(){
    updateMove(-4f,-20f,0);
  }

  public void pose2(){
    updateMove(7f,-15f,90f);
  }

  public void pose3(){
    updateMove(8f,5f,90f);
  }

  public void pose4(){
    updateMove(0f,12f,180f);
  }

  public void pose5(){
    updateMove(-15f,0f, -90f);
  }
 
  private void updateMove(float x,float z) {
    robotMoveTranslate.setTransform(Mat4Transform.translate(x,0,z));
    robotMoveTranslate.update();
    xPosition = x;
    zPosition = z;
  }

  private void updateMove(float x,float z, float d) {
    //robotMoveTranslate.setTransform(Mat4Transform.translate(0,0,0));
    //robotMoveTranslate.setTransform(Mat4Transform.rotateAroundZ(-rotation));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(x,0,z));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(d));
    //TransformNode transform = new TransformNode("leftarm scale", m);


    robotMoveTranslate.setTransform(m);
    robotMoveTranslate.update();
    xPosition = x;
    zPosition = z;
    rotation = d;
  }

  private void updatePose(){

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
    leftFeelerRotate.setTransform(Mat4Transform.rotateAroundX(180));
    leftFeelerRotate.update();
    rightFeelerRotate.setTransform(Mat4Transform.rotateAroundX(180));
    rightFeelerRotate.update();
  }
   
  public void raisedArms() {
    stopAnimation();
    leftFeelerRotate.setTransform(Mat4Transform.rotateAroundX(0));
    leftFeelerRotate.update();
    rightFeelerRotate.setTransform(Mat4Transform.rotateAroundX(0));
    rightFeelerRotate.update();
  }

  public void turnOffMainLights() {
    light.setIntensity(0);
    mainLight.setIntensity(0);
  }

  public void turnOnMainLights() {
    light.setIntensity(1);
    mainLight.setIntensity(1);
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere, eye, cube, phoneBaseCube, mobilePhone, backwall, sidewall, windowView;
  private List<Light> lights;
  private Light light;
  private Light mainLight;
  private Light spotLight;

  // Roots

  private SGNode robotRoot;
  private SGNode eggRoot;
  private SGNode phoneRoot;
  private SGNode lampRoot;

  private float xPosition;
  private float zPosition;
  private float rotation = 0;

  private TransformNode translateX;
  private TransformNode robotMoveTranslate;
  private TransformNode eggMoveTranslate;
  private TransformNode phoneMoveTranslate;
  private TransformNode lampMoveTranslate;
  private TransformNode leftFeelerRotate;
  private TransformNode rightFeelerRotate;
  private TransformNode lampRotate;
  
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
    Mat4 modelMatrix =  Mat4Transform.translate(0,(roomSize/2),-(roomSize/2));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize ,1f,roomSize)); ;
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture);
  }

  private Model initialise_sidewall(GL3 gl, Camera camera, List<Light> lights, int[] texture, float roomSize){
    Mesh mesh = new Mesh(gl, WindowedWall.vertices.clone(), WindowedWall.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.glsl", "fs_tt_05.glsl");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 99.0f);
    Mat4 modelMatrix =  Mat4Transform.translate(-(roomSize/2),(roomSize/2),0);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(-90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize ,1f,roomSize));
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

  private Model initialise_phone_base_cube(GL3 gl, Camera camera, List<Light> lights, int[] texture1, int[]texture2){
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

  private Model initialise_eye(GL3 gl, Camera camera, List<Light> lights, int[] texture1, int[] texture2){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.glsl", "fs_cube_04.glsl");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, lights, shader, material, modelMatrix, mesh, texture1, texture2);
  }

  // SCENES

  private void robot_scene(GL3 gl){

    float bodyScale = 5f;
    float footScale = 2f;
    float headScale = 2f;
    float eyeScale = 0.5f;
    float feelerScale = 0.3f;

    float neckScale = bodyScale / 8;
    float footHeight =  footScale / 2 ;
    float bodyHeight = footHeight + footScale / 2 + bodyScale/2 ;
    float neckHeight =  bodyHeight + bodyScale / 2 + neckScale / 2;
    float headHeight =  neckHeight + neckScale / 2 + headScale / 2;
    float eyeHeight = neckHeight + headScale/1.5f;
    float feelerHeight = headHeight + (headScale / 2);

    float leftFeelerStartAngle = -30;
    float rightFeelerStartAngle = 30;
    //
    robotRoot = new NameNode("root");
    robotMoveTranslate = new
            TransformNode("robot transform",Mat4Transform.translate(xPosition,0,zPosition));
    TransformNode robotTranslate = new
            TransformNode("robot transform",Mat4Transform.translate(0,0,0)); //-17f

    //Foot
    NameNode foot = new NameNode ("foot");
    //TransformNode footTranslate = new TransformNode("foot translate", Mat4Transform.translate(0, footHeight,0));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m,Mat4Transform.translate(0,footHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(footScale,footScale,footScale));
    TransformNode footTransform = new TransformNode("head transform", m);
    ModelNode footShape = new ModelNode("Sphere(foot)", sphere);

    NameNode body = new NameNode("body");
    m = Mat4Transform.translate(0,bodyHeight,0);
    m = Mat4.multiply(m, Mat4Transform.scale(bodyScale/2,bodyScale,bodyScale/2));
    TransformNode bodyTransform = new TransformNode("head transform", m);
    ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);

    //Neck
    NameNode neck = new NameNode("neck");
    m = Mat4Transform.translate(0,neckHeight,0);
    m = Mat4.multiply(m, Mat4Transform.scale(neckScale, neckScale, neckScale));
    TransformNode neckTransform = new TransformNode("head transform", m);
    ModelNode neckShape = new ModelNode("Sphere(body)", sphere);

    //Head
    NameNode head = new NameNode("head");
    m = Mat4Transform.translate(0,headHeight,0);
    m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale ,headScale));
    TransformNode headTransform = new TransformNode("head transform", m);
    ModelNode headShape = new ModelNode("Sphere(head)", cube);

    //Left eye
    NameNode leftEye = new NameNode("leftEye");
    m = new Mat4(1);
    m = Mat4.multiply(m,Mat4Transform.translate(-(headScale/3),eyeHeight,(headScale/2)));
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    TransformNode leftEyeTransform = new TransformNode("head transform", m);
    ModelNode leftEyeShape = new ModelNode("Sphere(eye)", eye);

    //Right eye
    NameNode rightEye = new NameNode("rightEye");
    m = new Mat4(1);
    m = Mat4.multiply(m,Mat4Transform.translate((headScale/3),eyeHeight,(headScale/2)));
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    TransformNode rightEyeTransform = new TransformNode("head transform", m);
    ModelNode rightEyeShape = new ModelNode("Sphere(eye)", eye);

    //Right Feeler
    NameNode rightFeeler = new NameNode("right arm");
    TransformNode rightFeelerTranslate = new TransformNode("right feeler translate",
            Mat4Transform.translate(-(headScale/2),feelerHeight,0));
    rightFeelerRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundZ(rightFeelerStartAngle));

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(feelerScale,feelerScale * 10,feelerScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightFeelerScale = new TransformNode("rightarm scale", m);
    ModelNode rightArmShape = new ModelNode("Cube(right arm)", cube);

    //Left Feeler
    NameNode leftFeeler = new NameNode("left arm");
    TransformNode leftFeelerTranslate = new TransformNode("leftarm translate",
            Mat4Transform.translate((headScale/2),feelerHeight,0));
    leftFeelerRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundZ(leftFeelerStartAngle));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(feelerScale,feelerScale * 10,feelerScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftFeelerScale = new TransformNode("leftarm scale", m);
    ModelNode leftFeelerShape = new ModelNode("Cube(left arm)", cube);

    //Robot Scene Graph
    robotRoot.addChild(robotMoveTranslate);
      robotMoveTranslate.addChild(robotTranslate);
        robotTranslate.addChild(foot);
          foot.addChild(footTransform);
            footTransform.addChild(footShape);
              foot.addChild(body);
                body.addChild(neck);
                  neck.addChild(neckTransform);
                    neckTransform.addChild(neckShape);
                body.addChild(bodyTransform);
                    bodyTransform.addChild(bodyShape);
                  neck.addChild(head);
                    head.addChild(headTransform);
                      headTransform.addChild(headShape);
                    head.addChild(leftEye);
                      leftEye.addChild(leftEyeTransform);
                        leftEyeTransform.addChild(leftEyeShape);
                    head.addChild(rightEye);
                      rightEye.addChild(rightEyeTransform);
                        rightEyeTransform.addChild(rightEyeShape);
                    head.addChild(rightFeeler);
                      rightFeeler.addChild(rightFeelerTranslate);
                        rightFeelerTranslate.addChild(rightFeelerRotate);
                          rightFeelerRotate.addChild(rightFeelerScale);
                            rightFeelerScale.addChild(rightArmShape);
                    head.addChild(leftFeeler);
                      leftFeeler.addChild(leftFeelerTranslate);
                        leftFeelerTranslate.addChild(leftFeelerRotate);
                          leftFeelerRotate.addChild(leftFeelerScale);
                          leftFeelerScale.addChild(leftFeelerShape);
  }

  private void egg_scene(GL3 gl){
    float eggScale = 5f;

    eggRoot = new NameNode("root");
    eggMoveTranslate = new TransformNode("egg transform", Mat4Transform.translate(0, 0, 0f));
    TransformNode eggTranslate = new TransformNode("egg transform",Mat4Transform.translate(0,0,0));

    NameNode eggBase = new NameNode("egg base");
    Mat4 m = Mat4Transform.translate(0,eggScale/4,0);
    m = Mat4.multiply(m, Mat4Transform.scale((eggScale),eggScale/2,(eggScale)));
    TransformNode eggBaseTransform =  new TransformNode("egg base transform", m);
    ModelNode eggBaseShape = new ModelNode("Cube(egg base)", cube);


    NameNode egg = new NameNode("egg");
    m = Mat4Transform.translate(0,eggScale + eggScale/2,0);
    m = Mat4.multiply(m, Mat4Transform.scale((eggScale),(eggScale * 2),(eggScale)));
    TransformNode eggTransform = new TransformNode("egg transform", m);
    ModelNode eggShape = new ModelNode("Sphere(egg)", sphere);

    eggRoot.addChild(eggMoveTranslate);
      eggMoveTranslate.addChild(eggTranslate);
        eggTranslate.addChild(eggBase);
          eggBase.addChild(eggBaseTransform);
            eggBaseTransform.addChild(eggBaseShape);
          eggBase.addChild(egg);
            egg.addChild(eggTransform);
              eggTransform.addChild(eggShape);

  }

  private void mobile_phone_scene(GL3 gl) {
    float phoneScale = 5f;

    phoneRoot = new NameNode("root");
    phoneMoveTranslate = new TransformNode("phone transform", Mat4Transform.translate(15, 0, -15f));
    TransformNode phoneTranslate = new TransformNode("phone transform",Mat4Transform.translate(0,0,0));

    NameNode phoneBase = new NameNode("phone base");
    Mat4 m = Mat4Transform.translate(0,phoneScale/4,0);
    m = Mat4.multiply(m, Mat4Transform.scale((phoneScale),phoneScale/2,(phoneScale)));
    TransformNode phoneBaseTransform =  new TransformNode("phone base transform", m);
    ModelNode phoneBaseShape = new ModelNode("Cube(phone base)", phoneBaseCube);


    NameNode phone = new NameNode("phone");
    m = Mat4Transform.translate(0,phoneScale + phoneScale/2,0);
    m = Mat4.multiply(m, Mat4Transform.scale((phoneScale),(phoneScale * 2),(phoneScale / 4)));
    TransformNode phoneTransform = new TransformNode("phone transform", m);
    ModelNode phoneShape = new ModelNode("Cube(phone)", mobilePhone);

    phoneRoot.addChild(phoneMoveTranslate);
    phoneMoveTranslate.addChild(phoneTranslate);
    phoneTranslate.addChild(phoneBase);
    phoneBase.addChild(phoneBaseTransform);
    phoneBaseTransform.addChild(phoneBaseShape);
          phoneBase.addChild(phone);
            phone.addChild(phoneTransform);
              phoneTransform.addChild(phoneShape);
  }

  private void lamp_scene(GL3 gl) {
    float lampScale = 5f;
    Vec3 baseScale = new Vec3( lampScale, lampScale/4, lampScale) ;
    Vec3 standScale = new Vec3(lampScale / 4, lampScale * 4, lampScale / 4);
    Vec3 armScale = new Vec3(lampScale, lampScale / 4, lampScale / 4);
    Vec3 headScale = new Vec3(lampScale / 4, lampScale / 4, lampScale / 4);

    float baseHeight = lampScale/4;
    float standHeight = lampScale * 2 + baseHeight;
    float armHeight = standHeight + lampScale * 2  +  lampScale / 8;
    float headHeight = armHeight;
    System.out.print(armHeight);




    lampRoot = new NameNode("root");
    lampMoveTranslate = new TransformNode("lamp transform", Mat4Transform.translate(17, -lampScale / 8, 5f));
    TransformNode lampTranslate = new TransformNode("lamp transform",Mat4Transform.translate(0,0,0));

    NameNode lampBase = new NameNode("phone base");
    Mat4 m = Mat4Transform.translate(0,baseHeight,0);
    m = Mat4.multiply(m, Mat4Transform.scale(baseScale));
    TransformNode lampBaseTransform =  new TransformNode("phone base transform", m);
    ModelNode lampBaseShape = new ModelNode("Cube(phone base)", cube);


    NameNode lamp1 = new NameNode("lamp1");
    m = Mat4Transform.translate(0, standHeight, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(standScale));
    TransformNode lamp1Transform = new TransformNode("lamp1 transform", m);
    ModelNode lamp1Shape = new ModelNode("Cube(lamp1)", cube);

    NameNode lamp2 = new NameNode("lamp2");
    m = Mat4Transform.translate((-lampScale/2) + lampScale/8 ,
            armHeight,
            0);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale));
    TransformNode lamp2Transform = new TransformNode("lamp2 transform", m);
    ModelNode lamp2Shape = new ModelNode("Cube(lamp2)", cube);

    NameNode lampHead = new NameNode("lamp head");
    TransformNode lampHeadTranslate = new TransformNode("lamp head translate",
            Mat4Transform.translate(-(lampScale),
                    headHeight,
                    0));
    lampRotate = new TransformNode("lamp head rotate",Mat4Transform.rotateAroundZ(0));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(headScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode lampHeadScale = new TransformNode("lamp head scale", m);
    ModelNode lampHeadShape = new ModelNode("Cube(lamp head)", cube);

    lampRoot.addChild(lampMoveTranslate);
      lampMoveTranslate.addChild(lampTranslate);
        lampTranslate.addChild(lampBase);
          lampBase.addChild(lampBaseTransform);
          lampBaseTransform.addChild(lampBaseShape);
           lampBase.addChild(lamp1);
            lamp1.addChild(lamp1Transform);
              lamp1Transform.addChild(lamp1Shape);
            lamp1.addChild(lamp2);
              lamp2.addChild(lamp2Transform);
                lamp2Transform.addChild(lamp2Shape);
              lamp2.addChild(lampHead);
                lampHead.addChild(lampHeadTranslate);
                  lampHeadTranslate.addChild(lampRotate);
                    lampRotate.addChild(lampHeadScale);
                      lampHeadScale.addChild(lampHeadShape);
//                lampHead.addChild(spotLight);
  }

  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] woodFloorTexture = TextureLibrary.loadTexture(gl, "textures/wood_floor.jpg");
    int[] wallTexture = TextureLibrary.loadTexture(gl, "textures/wallTexture.jpg");
    int[] windowViewTexture = TextureLibrary.loadTexture(gl, "textures/windowView.jpg");
    int[] phoneTexture = TextureLibrary.loadTexture(gl, "textures/cube.jpg");
    int[] phoneSpecular = TextureLibrary.loadTexture(gl, "textures/cube_specular.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg");
        
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
    eye = initialise_eye(gl,camera,lights,wallTexture,textureId6);

    phoneBaseCube = initialise_phone_base_cube(gl,camera,lights,textureId5,textureId2);
    mobilePhone = initialise_phone(gl,camera,lights,phoneTexture,phoneSpecular);
//
//  //Calling the scene functions
    robot_scene(gl);
    mobile_phone_scene(gl);
    egg_scene(gl);
    lamp_scene(gl);

    robotRoot.update();  // IMPORTANT - don't forget this
    pose1();
    phoneRoot.update();
    eggRoot.update();
    lampRoot.update();


    //eggRoot.print(0, false);
    //eggRoot.print(0, false);
    lampRoot.print(0,false);
    //System.exit(0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    mainLight.render(gl);
    spotLight.setDirection( lampSwing());
    floor.render(gl);
    backwall.render(gl);
    sidewall.render(gl);
    windowView.render(gl);

//    if (animation) updateLeftFeeler();
//    if (animation) updateRightFeeler();
    robotRoot.draw(gl);
    phoneRoot.draw(gl);
    eggRoot.draw(gl);
    lampRoot.draw(gl);
  }

  private Vec3 lampSwing() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = (180f+90f*(float)Math.sin(elapsedTime * 2)/8);
    lampRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    lampRotate.update();
    return new Vec3(0, -45, -(rotateAngle - 180));
  };

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