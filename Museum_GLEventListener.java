import gmaths.*;

import com.jogamp.opengl.*;

public class Museum_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Museum_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
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
   
  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateMove();
  }
   
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateMove();
  }
 
  private void updateMove() {
    robotMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    robotMoveTranslate.update();
  }
  
  public void loweredArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    rightArmRotate.update();
  }
   
  public void raisedArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    rightArmRotate.update();
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere, eye, cube, phoneBaseCube, mobilePhone, backwall, sidewall, windowView;
  private Light light;
  // Roots

  private SGNode robotRoot;
  private SGNode eggRoot;
  private SGNode phoneRoot;

  private float xPosition = -4f;
  private TransformNode translateX;
  private TransformNode robotMoveTranslate;
  private TransformNode eggMoveTranslate;
  private TransformNode phoneMoveTranslate2;
  private TransformNode leftArmRotate;
  private TransformNode rightArmRotate;
  
  private Model initialise_floor(GL3 gl, Camera camera, Light light, int[] texture, float roomSize){
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 99.0f);
    Mat4 modelMatrix = Mat4Transform.scale(roomSize,1f,roomSize);
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture);
  }

  private Model initialise_backwall(GL3 gl, Camera camera, Light light, int[] texture, float roomSize){
    Mesh mesh = new Mesh(gl, DoorWall.vertices.clone(), DoorWall.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 99.0f);
    Mat4 modelMatrix =  Mat4Transform.translate(0,(roomSize/2),-(roomSize/2));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize ,1f,roomSize)); ;
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture);
  }

  private Model initialise_sidewall(GL3 gl, Camera camera, Light light, int[] texture, float roomSize){
    Mesh mesh = new Mesh(gl, WindowedWall.vertices.clone(), WindowedWall.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 99.0f);
    Mat4 modelMatrix =  Mat4Transform.translate(-(roomSize/2),(roomSize/2),0);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(-90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize ,1f,roomSize));
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture);
  }

  private Model initialise_skybox(GL3 gl, Camera camera, Light light, int[] texture, float roomSize, float relativeViewOffset){
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(0.0f, 0.0f, 0.0f), 1.0f);
    Mat4 modelMatrix =  Mat4Transform.translate(-relativeViewOffset,(roomSize/2),0);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(-90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(roomSize * 2 ,1f,roomSize * 2));
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture);
  }

  private Model initialise_sphere(GL3 gl, Camera camera, Light light, int[] texture1, int[] texture2){
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture1, texture2);
  }

  private Model initialise_cube(GL3 gl, Camera camera, Light light, int[] texture1, int[]texture2){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture1, texture2);
  }

  private Model initialise_phone_base_cube(GL3 gl, Camera camera, Light light, int[] texture1, int[]texture2){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture1, texture2);
  }

  private Model initialise_phone(GL3 gl, Camera camera, Light light, int[] texture1, int[]texture2){
    Mesh mesh = new Mesh(gl, PhoneCube.vertices.clone(), PhoneCube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture1, texture2);
  }

  private Model initialise_eye(GL3 gl, Camera camera, Light light, int[] texture1, int[] texture2){
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = new Mat4(1);
    return new Model(gl, camera, light, shader, material, modelMatrix, mesh, texture1, texture2);
  }

  // SCENES

  private void robot_scene(GL3 gl){
    float bodyHeight = 3f;
    float bodyWidth = 2f;
    float bodyDepth = 1f;
    float bodyScale = 5f;
    float headScale = 4f;
    float eyeScale = 0.5f;
    float armLength = 3.5f;
    float armScale = 0.5f;
    float legLength = 3.5f;
//    float legScale = 0.67f;
//
    robotRoot = new NameNode("root");
    robotMoveTranslate = new
            TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
//
    TransformNode robotTranslate = new
            TransformNode("robot transform",Mat4Transform.translate(0,0,-17f));


    NameNode body = new NameNode("body");
    Mat4 m = Mat4Transform.translate(0,bodyScale/2,0);
    m = Mat4.multiply(m, Mat4Transform.scale(bodyScale,bodyScale,bodyScale));
    TransformNode bodyTransform = new TransformNode("head transform", m);
    ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);

    NameNode head = new NameNode("head");
    m = Mat4Transform.translate(0,(bodyScale + headScale/2),0);
    m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale ,headScale));
    TransformNode headTransform = new TransformNode("head transform", m);
    ModelNode headShape = new ModelNode("Sphere(head)", cube);

    NameNode leftEye = new NameNode("leftEye");
    m = new Mat4(1);
    m = Mat4.multiply(m,Mat4Transform.translate(-(headScale/3),(bodyScale + (headScale/1.5f)),(headScale/2)));
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    TransformNode leftEyeTransform = new TransformNode("head transform", m);
    ModelNode leftEyeShape = new ModelNode("Sphere(eye)", eye);

    NameNode rightEye = new NameNode("rightEye");
    m = new Mat4(1);
    m = Mat4.multiply(m,Mat4Transform.translate((headScale/3),(bodyScale + (headScale/1.5f)),(headScale/2)));
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    TransformNode rightEyeTransform = new TransformNode("head transform", m);
    ModelNode rightEyeShape = new ModelNode("Sphere(eye)", eye);

    NameNode rightArm = new NameNode("right arm");
    TransformNode rightArmTranslate = new TransformNode("rightarm translate",
            Mat4Transform.translate(-(bodyScale/2),bodyHeight,0));
    rightArmRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundZ(120));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightArmScale = new TransformNode("rightarm scale", m);
    ModelNode rightArmShape = new ModelNode("Cube(right arm)", cube);

    NameNode leftArm = new NameNode("left arm");
    TransformNode leftArmTranslate = new TransformNode("leftarm translate",
            Mat4Transform.translate((bodyScale/2),bodyHeight,0));
    leftArmRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundZ(-120));
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftArmScale = new TransformNode("leftarm scale", m);
    ModelNode leftArmShape = new ModelNode("Cube(left arm)", cube);

    //Robot Scene Graph
    robotRoot.addChild(robotMoveTranslate);
      robotMoveTranslate.addChild(robotTranslate);
        robotTranslate.addChild(body);
          body.addChild(bodyTransform);
            bodyTransform.addChild(bodyShape);
              body.addChild(head);
                head.addChild(headTransform);
                  headTransform.addChild(headShape);
                head.addChild(leftEye);
                  leftEye.addChild(leftEyeTransform);
                    leftEyeTransform.addChild(leftEyeShape);
                head.addChild(rightEye);
                  rightEye.addChild(rightEyeTransform);
                    rightEyeTransform.addChild(rightEyeShape);
              body.addChild(rightArm);
                rightArm.addChild(rightArmTranslate);
                  rightArmTranslate.addChild(rightArmRotate);
                    rightArmRotate.addChild(rightArmScale);
                      rightArmScale.addChild(rightArmShape);
              body.addChild(leftArm);
                leftArm.addChild(leftArmTranslate);
                  leftArmTranslate.addChild(leftArmRotate);
                    leftArmRotate.addChild(leftArmScale);
                    leftArmScale.addChild(leftArmShape);
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
    phoneMoveTranslate2 = new TransformNode("phone transform", Mat4Transform.translate(15, 0, -15f));
    TransformNode phoneTranslate = new TransformNode("phone transform",Mat4Transform.translate(0,0,0));

    NameNode phoneBase = new NameNode("phone base");
    Mat4 m = Mat4Transform.translate(0,phoneScale/4,0);
    m = Mat4.multiply(m, Mat4Transform.scale((phoneScale),phoneScale/2,(phoneScale)));
    TransformNode eggBaseTransform2 =  new TransformNode("phone base transform", m);
    ModelNode phoneBaseShape = new ModelNode("Cube(phone base)", phoneBaseCube);


    NameNode phone = new NameNode("phone");
    m = Mat4Transform.translate(0,phoneScale + phoneScale/2,0);
    m = Mat4.multiply(m, Mat4Transform.scale((phoneScale),(phoneScale * 2),(phoneScale / 4)));
    TransformNode phoneTransform = new TransformNode("phone transform", m);
    ModelNode phoneShape = new ModelNode("Cube(phone)", mobilePhone);

    phoneRoot.addChild(phoneMoveTranslate2);
    phoneMoveTranslate2.addChild(phoneTranslate);
    phoneTranslate.addChild(phoneBase);
    phoneBase.addChild(eggBaseTransform2);
    eggBaseTransform2.addChild(phoneBaseShape);
          phoneBase.addChild(phone);
            phone.addChild(phoneTransform);
              phoneTransform.addChild(phoneShape);
  }

  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] woodFloorTexture = TextureLibrary.loadTexture(gl, "textures/wood_floor.jpg");
    int[] wallTexture = TextureLibrary.loadTexture(gl, "textures/wallTexture.jpg");
    int[] windowViewTexture = TextureLibrary.loadTexture(gl, "textures/windowView.jpg");
    int[] phoneTexure = TextureLibrary.loadTexture(gl, "textures/cube.jpg");
    int[] phoneSpecular = TextureLibrary.loadTexture(gl, "textures/cube_specular.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg");

    
        
    light = new Light(gl);
    light.setCamera(camera);
    float roomSize = 40;
    float viewOffset = 12;
    float relativeViewOffset = (roomSize/2) + viewOffset;

    //floor
    floor = initialise_floor(gl, camera, light, woodFloorTexture, roomSize);
    //back wall
    backwall = initialise_backwall(gl, camera, light, wallTexture, roomSize);
    //windowed wall
    sidewall = initialise_sidewall(gl, camera, light, wallTexture, roomSize);
    //sky box
    windowView = initialise_skybox(gl, camera, light, windowViewTexture, roomSize, relativeViewOffset) ;


    //#################### ROBOT!!! ###############################
    //robot body
    sphere = initialise_sphere(gl,camera,light,textureId1,textureId2);
    cube = initialise_cube(gl,camera,light,textureId1,textureId2);
    eye = initialise_eye(gl,camera,light,wallTexture,textureId6);

    phoneBaseCube = initialise_phone_base_cube(gl,camera,light,textureId5,textureId2);
    mobilePhone = initialise_phone(gl,camera,light,phoneTexure,phoneSpecular);
//
//  //Calling the scene functions
    robot_scene(gl);
    mobile_phone_scene(gl);
    egg_scene(gl);



    robotRoot.update();  // IMPORTANT - don't forget this
    phoneRoot.update();
    eggRoot.update();


    //eggRoot.print(0, false);
    //eggRoot.print(0, false);
    phoneRoot.print(0,false);
    //System.exit(0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);
    backwall.render(gl);
    sidewall.render(gl);
    windowView.render(gl);
    //if (animation) updateLeftArm();
    //if (animation) updateRightArm();
    robotRoot.draw(gl);
    phoneRoot.draw(gl);
    eggRoot.draw(gl);
  }

  private void updateLeftArm() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    leftArmRotate.update();
  }

  private void updateRightArm() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = -(180f+90f*(float)Math.sin(elapsedTime));
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    rightArmRotate.update();
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