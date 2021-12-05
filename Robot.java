import com.jogamp.opengl.GL3;
import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Robot {
    private GL3 gl;
    private Model sphere;
    private Model eye;
    private Model cube;

    double startTime;

    //Transform Nodes
    public TransformNode robotMoveTranslate;
    public TransformNode leftFeelerRotate;
    public TransformNode rightFeelerRotate;
    public TransformNode headRotate;
    public TransformNode bodyRotate;
    private SGNode robotRoot;

    //Main robot transform variables
    public float xPosition;
    public float zPosition;
    public float rotation;

    //Body scale variables
    float bodyScaleFactor = 5f;
    float footScaleFactor = 2f;
    float headScaleFactor = 2f;
    float eyeScaleFactor = 0.5f;
    float feelerScaleFactor = 0.3f;
    float neckScaleFactor = bodyScaleFactor / 8;

    //Body height variables
    float footHeight =  footScaleFactor / 2 ;
    float bodyHeight = footHeight + footScaleFactor / 2 + bodyScaleFactor/2 ;
    float neckHeight =  bodyHeight + bodyScaleFactor / 2 - (footHeight * 2);
    float headHeight =  neckScaleFactor / 2 + headScaleFactor / 2;
    float eyeHeight = headHeight + headScaleFactor/10 ;
    float feelerHeight = headHeight + (headScaleFactor / 2);

    //Body angles
    Vec3 leftFeelerAngle = new Vec3(0,0,-30f);
    Vec3 rightFeelerAngle = new Vec3(0,0,30f);
    Vec3 bodyRotateAngle = new Vec3(0,0,0);
    Vec3 headRotateAngle = new Vec3(0,0,0);

    // UNUSED EXPERIMENTAL Animation Variables
    Vec3 oldPosition;
    //Vec3 currentPosition;
    Vec3 newPosition;
    Vec3 increments = new Vec3(0,0,0);
    float speed = 200f;
    boolean moving = false;


    public Robot(GL3 gl, Model cube, Model eye, Model sphere, double startTime){
        this.gl = gl;
        this.sphere = sphere;
        this.eye = eye;
        this.cube = cube;
        this.startTime = startTime;
        this.sceneGraph();
    }

    private void sceneGraph(){
        //
        robotRoot = new NameNode("root");
        robotMoveTranslate = new
                TransformNode("robot transform", Mat4Transform.translate(xPosition,0,zPosition));
        TransformNode robotTranslate = new
                TransformNode("robot transform",Mat4Transform.translate(0,0,0)); //-17f

        //Foot
        NameNode foot = new NameNode ("foot");
        //TransformNode footTranslate = new TransformNode("foot translate", Mat4Transform.translate(0, footHeight,0));
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.translate(0,footHeight,0));
        m = Mat4.multiply(m, Mat4Transform.scale(footScaleFactor,footScaleFactor,footScaleFactor));
        TransformNode footTransform = new TransformNode("head transform", m);
        ModelNode footShape = new ModelNode("Sphere(foot)", sphere);

        //Body needs a rotate node
        NameNode body = new NameNode("body");
        TransformNode bodyTranslate = new TransformNode("body translate",
                Mat4Transform.translate(0,footHeight * 2,0));
        m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(bodyRotateAngle.x));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(bodyRotateAngle.y));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundZ(bodyRotateAngle.z));
        bodyRotate = new TransformNode("body rotate",m);

        m = Mat4Transform.translate(0,bodyHeight / 2,0);
        m = Mat4.multiply(m, Mat4Transform.scale(bodyScaleFactor/2,bodyScaleFactor,bodyScaleFactor/2));
        TransformNode bodyScale = new TransformNode("head transform", m);
        ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);

        //Neck
        NameNode neck = new NameNode("neck");
        m = Mat4Transform.translate(0,neckHeight,0);
        m = Mat4.multiply(m, Mat4Transform.scale(neckScaleFactor, neckScaleFactor, neckScaleFactor));
        TransformNode neckTransform = new TransformNode("head transform", m);
        ModelNode neckShape = new ModelNode("Sphere(body)", sphere);

        //Head needs a rotate node (between it and neck)
        //Head
        NameNode head = new NameNode("head");
        TransformNode headTranslate = new TransformNode("body translate",
                Mat4Transform.translate(0,neckHeight,0));
        m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(headRotateAngle.x));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(headRotateAngle.y));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundZ(headRotateAngle.z));
        headRotate = new TransformNode("head rotate", m);
        m = Mat4Transform.translate(0,headHeight,0);
        m = Mat4.multiply(m, Mat4Transform.scale(headScaleFactor,headScaleFactor ,headScaleFactor));
        TransformNode headScale = new TransformNode("head transform", m);
        ModelNode headShape = new ModelNode("Sphere(head)", cube);

        //Left eye
        NameNode leftEye = new NameNode("leftEye");
        m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.translate(-(headScaleFactor/3),eyeHeight,(headScaleFactor/2)));
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScaleFactor, eyeScaleFactor, eyeScaleFactor));
        TransformNode leftEyeTransform = new TransformNode("left eye transform", m);
        ModelNode leftEyeShape = new ModelNode("Sphere(eye)", eye);

        //Right eye
        NameNode rightEye = new NameNode("rightEye");
        m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.translate((headScaleFactor/3),eyeHeight,(headScaleFactor/2)));
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScaleFactor, eyeScaleFactor, eyeScaleFactor));
        TransformNode rightEyeTransform = new TransformNode("right eye transform", m);
        ModelNode rightEyeShape = new ModelNode("Sphere(eye)", eye);

        //Right Feeler
        NameNode rightFeeler = new NameNode("right arm");
        TransformNode rightFeelerTranslate = new TransformNode("right feeler translate",
                Mat4Transform.translate(-(headScaleFactor/2),feelerHeight,0));
        rightFeelerRotate = new TransformNode("rightarm rotate",Mat4Transform.rotateAroundZ(rightFeelerAngle.z));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(feelerScaleFactor,feelerScaleFactor * 10,feelerScaleFactor));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode rightFeelerScale = new TransformNode("right feeler scale", m);
        ModelNode rightArmShape = new ModelNode("Cube(right feeler)", cube);

        //Left Feeler
        NameNode leftFeeler = new NameNode("left feeler");
        TransformNode leftFeelerTranslate = new TransformNode("left feeler translate",
                Mat4Transform.translate((headScaleFactor/2),feelerHeight,0));
        leftFeelerRotate = new TransformNode("leftfeeler rotate",Mat4Transform.rotateAroundZ(leftFeelerAngle.z));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(feelerScaleFactor,feelerScaleFactor * 10,feelerScaleFactor));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode leftFeelerScale = new TransformNode("leftfeeler scale", m);
        ModelNode leftFeelerShape = new ModelNode("Cube(left feelet)", cube);

        //Robot Scene Graph
        robotRoot.addChild(robotMoveTranslate);
            robotMoveTranslate.addChild(robotTranslate);
                robotTranslate.addChild(foot);
                    foot.addChild(footTransform);
                        footTransform.addChild(footShape);
                    foot.addChild(body);
                        body.addChild(bodyTranslate);
                            bodyTranslate.addChild(bodyRotate);
                                bodyRotate.addChild(bodyScale);
                                    bodyScale.addChild(bodyShape);
                                bodyRotate.addChild(neck);
                                    neck.addChild(neckTransform);
                                        neckTransform.addChild(neckShape);
                                    neck.addChild(head);
                                        head.addChild(headTranslate);
                                            headTranslate.addChild(headRotate);
                                                headRotate.addChild(headScale);
                                                    headScale.addChild(headShape);
                                                headRotate.addChild(leftEye);
                                                    leftEye.addChild(leftEyeTransform);
                                                        leftEyeTransform.addChild(leftEyeShape);
                                                headRotate.addChild(rightEye);
                                                    rightEye.addChild(rightEyeTransform);
                                                        rightEyeTransform.addChild(rightEyeShape);
                                                headRotate.addChild(rightFeeler);
                                                    rightFeeler.addChild(rightFeelerTranslate);
                                                        rightFeelerTranslate.addChild(rightFeelerRotate);
                                                            rightFeelerRotate.addChild(rightFeelerScale);
                                                                rightFeelerScale.addChild(rightArmShape);
                                                headRotate.addChild(leftFeeler);
                                                    leftFeeler.addChild(leftFeelerTranslate);
                                                        leftFeelerTranslate.addChild(leftFeelerRotate);
                                                            leftFeelerRotate.addChild(leftFeelerScale);
                                                                leftFeelerScale.addChild(leftFeelerShape);

        robotRoot.update();
    }

    public void render(){
        robotRoot.draw(gl);
        feelerSwing();
    }

    public void pose1(){
        Vec3 head = new Vec3(-30,0,0);
        Vec3 body = new Vec3(30,0,0);

        Vec3 leftFeeler = new Vec3(0,0,30f);
        Vec3 rightFeeler = new Vec3(0,0,30f);
        updatePose(head,body,leftFeeler,rightFeeler);
        updateMove(-4f,-20f,0);
    }

    public void pose2(){
        Vec3 head = new Vec3(-30,0,0);
        Vec3 body = new Vec3(30,0,0);

        Vec3 leftFeeler = new Vec3(30,0,10f);
        Vec3 rightFeeler = new Vec3(30,0,10f);
        updatePose(head,body,leftFeeler,rightFeeler);
        updateMove(7f,-15f,90f);
    }

    public void pose3(){
        Vec3 head = new Vec3(-50,40,-30);
        Vec3 body = new Vec3(0,0,30);

        Vec3 leftFeeler = new Vec3(70,0,0f);
        Vec3 rightFeeler = new Vec3(70,0,0f);
        updatePose(head,body,leftFeeler,rightFeeler);
        updateMove(8f,5f,90f);
    }

    public void pose4(){
        Vec3 head = new Vec3(0,0,-30);
        Vec3 body = new Vec3(0,0,30);


        Vec3 leftFeeler = new Vec3(0,0,30f);
        Vec3 rightFeeler = new Vec3(0,0,30f);
        updatePose(head,body,leftFeeler,rightFeeler);
        updateMove(0f,12f,180f);
    }

    public void pose5(){
        Vec3 head = new Vec3(20,0,0);
        Vec3 body = new Vec3(-40,0,0);

        Vec3 leftFeeler = new Vec3(-15,0,0f);
        Vec3 rightFeeler = new Vec3(-15,0,0f);
        updatePose(head,body,leftFeeler,rightFeeler);
        updateMove(-15f,0f, -90f);
    }

    private void updateMove(float x,float z) {
        this.robotMoveTranslate.setTransform(Mat4Transform.translate(x,0,z));
        this.robotMoveTranslate.update();
        this.xPosition = x;
        this.zPosition = z;
    }

    private void updateMove(float x,float z, float d) {
        //robotMoveTranslate.setTransform(Mat4Transform.translate(0,0,0));
        //robotMoveTranslate.setTransform(Mat4Transform.rotateAroundZ(-rotation));
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(x,0,z));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(d));
        //TransformNode transform = new TransformNode("leftarm scale", m);

        this.robotMoveTranslate.setTransform(m);
        this.robotMoveTranslate.update();
        this.xPosition = x;
        this.zPosition = z;
        this.rotation = d;
    }

    private void updatePose(Vec3 head, Vec3 body, Vec3 leftFeeler, Vec3 rightFeeler){
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.rotateAroundX(body.x));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(body.y));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundZ(body.z));
        bodyRotate.setTransform(m);
        bodyRotate.update();

        m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.rotateAroundX(head.x));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(head.y));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundZ(head.z));
        headRotate.setTransform(m);
        bodyRotate.update();

        m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.rotateAroundX(leftFeeler.x));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(-leftFeeler.y));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundZ(-leftFeeler.z));
        leftFeelerRotate.setTransform(m);
        leftFeelerRotate.update();

        m = new Mat4(1);
        m = Mat4.multiply(m,Mat4Transform.rotateAroundX(rightFeeler.x));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundY(rightFeeler.y));
        m = Mat4.multiply(m,Mat4Transform.rotateAroundZ(rightFeeler.z));
        rightFeelerRotate.setTransform(m);
        rightFeelerRotate.update();

        this.bodyRotateAngle = body;
        this.headRotateAngle = head;
        this.leftFeelerAngle = leftFeeler;
        this.rightFeelerAngle = rightFeeler;
    }

    //UNUSED EXPERIMENTAL ANIMATION METHODS
    private void animateTo(Vec3 oldPosition){
        updateMove((oldPosition.x + increments.x), (oldPosition.z + increments.z) );
    }

    private void setIncrements(Vec3 oldPosition, Vec3 newPosition){
        float xInc = (oldPosition.x - newPosition.x) /speed;
        float yInc = 0;
        float zInc = (oldPosition.z - newPosition.z)/speed;
        this.newPosition = newPosition;
        moving = true;
        increments = new Vec3(-xInc,yInc,-zInc);
    }

    public void feelerSwing() {
        double elapsedTime = getSeconds()-startTime;
        float rotateAngle = (180f+90f*(float)Math.sin(elapsedTime * 2)/8 - 180);

        Mat4 m = leftFeelerRotate.getTransform();
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(-rotateAngle));
        leftFeelerRotate.setTransform(m);
        leftFeelerRotate.update();

        m = rightFeelerRotate.getTransform();
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(rotateAngle));
        rightFeelerRotate.setTransform(m);
        rightFeelerRotate.update();
    };

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
    }

}
