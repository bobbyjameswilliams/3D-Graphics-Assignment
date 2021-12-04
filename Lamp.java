import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Lamp {
    private GL3 gl;
    private Model cube;

    private float lampScale = 5f;
    private Vec3 baseScale = new Vec3( lampScale, lampScale/4, lampScale) ;
    private Vec3 standScale = new Vec3(lampScale / 4, lampScale * 4, lampScale / 4);
    private Vec3 armScale = new Vec3(lampScale, lampScale / 4, lampScale / 4);
    private Vec3 headScale = new Vec3(lampScale / 4, lampScale / 4, lampScale / 4);

    private float baseHeight = lampScale/4;
    private float standHeight = lampScale * 2 + baseHeight;
    private float armHeight = standHeight + lampScale * 2  +  lampScale / 8;
    private float headHeight = armHeight;

    private SGNode lampRoot;
    private TransformNode lampMoveTranslate;
    private TransformNode lampRotate;

    private double startTime;

    public Lamp(GL3 gl, Model cube, double startTime){
        this.gl = gl;
        this.cube = cube;
        this.sceneGraph();
    }

    private void sceneGraph(){
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

        lampRoot.update();
    }
    public Vec3 lampSwing() {
        double elapsedTime = getSeconds()-startTime;
        float rotateAngle = (180f+90f*(float)Math.sin(elapsedTime * 2)/8);
        lampRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
        lampRotate.update();
        return new Vec3(0, -45, -(rotateAngle - 180));
    };

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
    }

    public void render(){
        lampRoot.draw(gl);
    }
}
