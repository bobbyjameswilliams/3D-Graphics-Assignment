import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import utils.Model;

public class Egg {

    private float eggScale = 5f;

    private GL3 gl;
    private Model sphere;
    private Model cube;
    private SGNode eggRoot;
    private TransformNode eggMoveTranslate;


    public Egg(GL3 gl, Model cube, Model sphere){
        this.gl = gl;
        this.cube = cube;
        this.sphere = sphere;
        this.sceneGraph();
    }

    private void sceneGraph(){
        eggRoot = new NameNode("root");
        eggMoveTranslate = new TransformNode("egg transform", Mat4Transform.translate(0, 0, 0f));
        TransformNode eggTranslate = new TransformNode("egg transform",Mat4Transform.translate(0,0,0));

        NameNode eggBase = new NameNode("egg base");
        Mat4 m = Mat4Transform.translate(0,eggScale/4,0);
        m = Mat4.multiply(m, Mat4Transform.scale((eggScale),eggScale/2,(eggScale)));
        TransformNode eggBaseTransform =  new TransformNode("egg base transform", m);
        ModelNode eggBaseShape = new ModelNode("vertexes.Cube(egg base)", cube);


        NameNode egg = new NameNode("egg");
        m = Mat4Transform.translate(0,eggScale + eggScale/2,0);
        m = Mat4.multiply(m, Mat4Transform.scale((eggScale),(eggScale * 2),(eggScale)));
        TransformNode eggTransform = new TransformNode("egg transform", m);
        ModelNode eggShape = new ModelNode("vertexes.Sphere(egg)", sphere);

        eggRoot.addChild(eggMoveTranslate);
        eggMoveTranslate.addChild(eggTranslate);
        eggTranslate.addChild(eggBase);
        eggBase.addChild(eggBaseTransform);
        eggBaseTransform.addChild(eggBaseShape);
        eggBase.addChild(egg);
        egg.addChild(eggTransform);
        eggTransform.addChild(eggShape);

        eggRoot.update();
    }

    public void render(){
        eggRoot.draw(gl);
    }
}
